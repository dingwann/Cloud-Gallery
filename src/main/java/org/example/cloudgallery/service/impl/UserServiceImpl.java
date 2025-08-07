package org.example.cloudgallery.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudgallery.constant.UserConstant;
import org.example.cloudgallery.email.html.Template;
import org.example.cloudgallery.exception.BusinessException;
import org.example.cloudgallery.exception.ErrorCode;
import org.example.cloudgallery.mapper.UserMapper;
import org.example.cloudgallery.model.dto.user.UserQueryRequest;
import org.example.cloudgallery.model.dto.user.UserRegisterEmailRequest;
import org.example.cloudgallery.model.entity.User;
import org.example.cloudgallery.model.enums.UserRoleEnum;
import org.example.cloudgallery.model.vo.LoginUserVO;
import org.example.cloudgallery.model.vo.UserVO;
import org.example.cloudgallery.service.IEmailService;
import org.example.cloudgallery.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.example.cloudgallery.constant.UserConstant.USER_LOGIN_STATE;


/**
 * @author dingwan
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-03-14 14:53:41
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    private final IEmailService emailService;

    private final StringRedisTemplate redisTemplate;

    // 构造注入
    public UserServiceImpl(IEmailService emailService, StringRedisTemplate redisTemplate) {
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 用户注册
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 注册成功的用户ID
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 2. 检查账号是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        // 3. 对密码进行加密
        String encryptPassword = getEncryptPassword(userPassword);

        // 4. 插入用户数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(String.format("defaultName-%s", RandomUtil.randomInt(99999)));
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
    }


    @Override
    public long userRegisterOfEmail(UserRegisterEmailRequest registerEmailRequest) {
        String email = registerEmailRequest.getEmail();
        String code = registerEmailRequest.getCode();
        String password = registerEmailRequest.getPassword();
        String checkPassword = registerEmailRequest.getCheckPassword();
        // 校验密码
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 校验验证码
        String redisCode = redisTemplate.opsForValue().get(UserConstant.EMAIL_VERIFICATION_CODE + ":" + email);
        if (!code.equals(redisCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
        User user = new User();
        user.setUserAccount(email);
        // 加密密码
        String encryptPassword = getEncryptPassword(password);
        user.setUserPassword(encryptPassword);
        user.setUserName(RandomUtil.randomString(8));
        user.setUserRole(UserRoleEnum.USER.getValue());
        user.setUserEmail(email);
        this.save(user);

        return user.getId();
    }

    @Override
    public boolean checkEmail(String email) {
        // 1. 检查邮箱是否已注册
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userEmail", email);
        User user = this.getOne(queryWrapper);

        // 2. 返回注册状态（触发前端验证码流程）
        return user != null;
    }

    @Override
    public boolean sendVerificationCode(String email) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        // 检查是否已有验证码
        String redisCode = operations.get(UserConstant.EMAIL_VERIFICATION_CODE + ":" + email);
        if (redisCode != null) {
            Long expire = redisTemplate.getExpire(UserConstant.EMAIL_VERIFICATION_CODE + ":" + email, TimeUnit.SECONDS);
            // 判断是否已过60s
            long diff = 300 - expire;
            if (diff < 60) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, String.format("请%d秒后再次获取", 60 - diff));
            }
            // 删除旧验证码
            redisTemplate.delete(UserConstant.EMAIL_VERIFICATION_CODE + ":" + email);
        }

        // 1. 生成6位随机验证码
        String code = RandomUtil.randomNumbers(6);

        // 2. 存储到Redis（5分钟过期）
        operations.set(
                UserConstant.EMAIL_VERIFICATION_CODE + ":" + email,
                code,
                5, TimeUnit.MINUTES
        );

        // 3. 发送邮件
        try {
            emailService.sendHtml("dingwann@qq.com", email, "云图库注册验证码", Template.getRegisterTemplate(code));
            return true;
        } catch (Exception e) {
            log.error("send email failed", e);
            return false;
        }
    }

    /**
     * 用户登录
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      HTTP请求对象
     * @return 登录用户信息
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验参数
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        // 2. 对密码进行加密
        String encryptPassword = getEncryptPassword(userPassword);

        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);

        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取当前登录用户
     * @param request HTTP请求对象
     * @return 当前登录用户信息
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 从数据库查询（追求性能的话可以注释，直接返回上述结果）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取脱敏的登录用户信息
     * @param user 用户实体
     * @return 脱敏后的登录用户信息
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 用户注销
     * @param request HTTP请求对象
     * @return 注销结果
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }

        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 获取脱敏的用户信息
     * @param user 用户实体
     * @return 脱敏后的用户信息
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取用户脱敏信息列表
     * @param userList 用户实体列表
     * @return 脱敏后的用户信息列表
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * 获取查询条件
     * @param userQueryRequest 用户查询请求参数
     * @return 查询条件构造器
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        // 构造查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 获取加密后的密码
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值 混淆加密
        final String SALT = "wangcai";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

}