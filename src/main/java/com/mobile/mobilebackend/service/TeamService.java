package com.mobile.mobilebackend.service;

import com.mobile.mobilebackend.common.ErrorCode;
import com.mobile.mobilebackend.exception.BusinessException;
import com.mobile.mobilebackend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.model.dto.TeamQuery;
import com.mobile.mobilebackend.model.vo.UserTeamVo;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
* @author HUANG
* @description 针对表【team】的数据库操作Service
* @createDate 2023-09-19 14:21:54
*/
public interface TeamService extends IService<Team> {
    public long addTeam(Team team, User loginUser);

    List<UserTeamVo> teamList(TeamQuery teamQuery) throws InvocationTargetException, IllegalAccessException;

    boolean updateTeam(Team team, User loginUser);
}
