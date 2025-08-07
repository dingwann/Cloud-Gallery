-- 创建数据库
create database if not exists cloud_gallery;

-- 切换数据库
use cloud_gallery;

-- 用户表
create table if not exists user
(
    id              bigint auto_increment comment 'id' primary key,
    userAccount     varchar(256)                           not null comment '账号',
    userPassword    varchar(512)                           not null comment '密码',
    userName        varchar(256)                           null comment '用户昵称',
    userAvatar      varchar(1024)                          null comment '用户头像',
    userProfile     varchar(512)                           null comment '用户简介',
    userRole        varchar(256) default 'user'            not null comment '用户角色：user/admin',
    preferences     varchar(512)                           null comment '用户偏好标签',
    editTime        datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime      datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    logicDelete     tinyint      default 0                 not null comment '是否删除(逻辑)',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 功能扩展（user）
-- 新增列：userEmail、shareCode、inviteUser、vipExpireTime、vipCode、vipNumber
alter table user
    add column userEmail varchar(64) null comment '用户邮箱',
    add column userShareCode varchar(20) default null comment '用户分享码',
    add column inviteUser bigint default null comment '邀请人id',
    add column vipExpireTime datetime null comment 'vip过期时间',
    add column vipCode varchar(128) null comment 'vip兑换码',
    add column vipNumber int null comment 'vip等级';

-- ...
