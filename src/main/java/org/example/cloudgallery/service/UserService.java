package org.example.template.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.example.template.model.dto.user.UserQueryRequest;
import org.example.template.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.template.model.vo.LoginUserVO;
import org.example.template.model.vo.UserVO;

import java.util.List;

/**
* @author liuwang
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-03-14 14:53:41
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);


    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> userList);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    String getEncryptPassword(String userPassword);
}
