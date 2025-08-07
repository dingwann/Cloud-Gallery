package org.example.cloudgallery.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 用户偏好标签
     */
    private String preferences;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 分享码
     */
    private String userShareCode;

    /**
     * 邀请人
     */
    private Long inviteUser;

    /**
     * vip过期时间
     */
    private Date vipExpireTime;

    /**
     * vip兑换码
     */
    private String vipCode;

    /**
     * vip等级
     */
    private Integer vipNumber;


    /**
     * 是否删除(逻辑)
     */
    @TableLogic
    private Integer logicDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}