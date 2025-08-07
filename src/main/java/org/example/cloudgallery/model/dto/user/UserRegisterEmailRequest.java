package org.example.cloudgallery.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserRegisterEmailRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 邮箱
     */
    @NotBlank
    private String email;

    /**
     * 验证码
     */
    @NotBlank
    private String code;

    /**
     * 密码
     */
    @NotBlank
    private String password;

    /**
     * 确认密码
     */
    @NotBlank
    private String checkPassword;
}
