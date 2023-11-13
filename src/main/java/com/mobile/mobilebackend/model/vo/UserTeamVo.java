package com.mobile.mobilebackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 1对n，一个team多个user
 */
@Data
public class UserTeamVo implements Serializable {

    /**
     *队伍Id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 创建人Id
     */
    private Long creatorId;

    /**
     * 0-公开， 1-私有， 2-加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 队伍中人物的列表
     */
    private List<UserVo> userVoList;

    /**
     * 队伍中目前的人数
     */
    private Integer teamNowNumber;

    /**
     * 创建人
     */
    private UserVo createUser;

    /**
     * 当前用户是否加入这个队伍
     */
    private Boolean isInTeam = false;
}
