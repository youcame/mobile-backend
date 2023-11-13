package com.mobile.mobilebackend.service;

import com.mobile.mobilebackend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.model.dto.TeamJoinRequest;
import com.mobile.mobilebackend.model.dto.TeamQueryRequest;
import com.mobile.mobilebackend.model.dto.TeamQuitRequest;
import com.mobile.mobilebackend.model.vo.UserTeamVo;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
* @author HUANG
* @description 针对表【team】的数据库操作Service
* @createDate 2023-09-19 14:21:54
*/
public interface TeamService extends IService<Team> {
    long addTeam(Team team, User loginUser);

    List<UserTeamVo> teamList(TeamQueryRequest teamQueryRequest) throws InvocationTargetException, IllegalAccessException;

    boolean updateTeam(Team team, User loginUser);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    boolean quitTeam(TeamQuitRequest teamQuitRequest, User user);

    boolean deleteTeam(Long id, User user);

    List<UserTeamVo> filterTeamWithoutMe(List<UserTeamVo> list,User currentUser);
}
