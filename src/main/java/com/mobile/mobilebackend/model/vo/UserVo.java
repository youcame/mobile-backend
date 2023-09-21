package com.mobile.mobilebackend.model.vo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserVo implements Serializable {
    /**
     * 用户Id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别  0-男 1-女
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 权限 0-普通用户
     */
    private Integer userRole;

    /**
     * 标签列表 json格式
     */
    private String tags;

    private static final long serialVersionUID = 1L;
}
