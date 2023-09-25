package com.mobile.mobilebackend.model.dto;

import lombok.Data;

@Data
public class TeamJoinRequest {
    /**
     * 队伍Id
     */
    Long teamId;
    /**
     * 队伍密码
     */
    String password;
}
