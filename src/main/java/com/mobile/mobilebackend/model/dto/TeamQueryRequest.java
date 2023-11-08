package com.mobile.mobilebackend.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

@Data
public class TeamQueryRequest {

    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍id列表
     */
    private List<Long> idList;

    /**
     * 队伍描述(同时对队伍描述和名称搜索)
     */
    private String searchText;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 创建人Id
     */
    private Long creatorId;

    /**
     * 0-公开， 1-私有， 2-加密
     */
    private Integer status;
}
