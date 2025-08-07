package org.example.cloudgallery.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.cloudgallery.model.dto.user.UserQueryRequest;
import org.example.cloudgallery.model.dto.user.UserRegisterEmailRequest;
import org.example.cloudgallery.model.entity.User;
import org.example.cloudgallery.model.vo.LoginUserVO;
import org.example.cloudgallery.model.vo.UserVO;

import java.util.List;


/**
* @author dingwan
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-03-14 14:53:41
*/
public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword, String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);

    boolean userLogout(HttpServletRequest request);

    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> userList);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    String getEncryptPassword(String userPassword);

    long userRegisterOfEmail(UserRegisterEmailRequest registerEmailRequestl);

    boolean checkEmail(String email);

    boolean sendVerificationCode(String email);
}
