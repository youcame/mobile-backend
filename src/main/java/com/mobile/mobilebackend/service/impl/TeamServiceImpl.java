package com.mobile.mobilebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobile.mobilebackend.Authority.UserAuthority;
import com.mobile.mobilebackend.common.ErrorCode;
import com.mobile.mobilebackend.exception.BusinessException;
import com.mobile.mobilebackend.mapper.UserMapper;
import com.mobile.mobilebackend.model.domain.Team;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.model.domain.UserTeam;
import com.mobile.mobilebackend.service.TeamService;
import com.mobile.mobilebackend.mapper.TeamMapper;
import com.mobile.mobilebackend.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

import static com.mobile.mobilebackend.constant.TeamConstant.*;

/**
* @author HUANG
* @description 针对表【team】的数据库操作Service实现
* @createDate 2023-09-19 14:21:54
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    UserMapper teamMapper;
    @Resource
    UserTeamService userTeamService;
    /**
     * 创建队伍校验
     * @param team
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //队伍不为空
        if(team==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        //创建人不为空
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        final Long userId = loginUser.getId();
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        //队伍人数限制
        if(maxNum<1||maxNum>20){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍人数不符合要求") ;
        }
        String teamName = team.getName();
        //队伍名字长度限制
        if(teamName.length()>20|| StringUtils.isBlank(teamName)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍名称过长或为空");
        }
        String description = team.getDescription();
        //队伍描述限制
        if(description.length()>512||StringUtils.isNotBlank(description)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍描述过长");
        }
        int status = Optional.ofNullable(team.getStatus()).orElse(-1);
        //检测队伍状态
        if(status!=PUBLIC_TEAM||status!=PRIVATE_TEAM||status!=SECRET_TEAM){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍状态错误");
        }
        String password = team.getPassword();
        if(status==PRIVATE_TEAM && (StringUtils.isBlank(password) || password.length()>32)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"密码格式错误");
        }
        Date expireTime = team.getExpireTime();
        //检测队伍是否过期
        if(new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAM_NULL,"队伍已过时");
        }
        //检测创建人一共创建的队伍数量
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creatorId", userId);
        long count = this.count(queryWrapper);
        if(count>5){
            throw new BusinessException(ErrorCode.NO_AUTH,"创建队伍数过多");
        }
        team.setId(userId);
        boolean save = this.save(team);
        team.setId(null);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"保存队伍失败");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        userTeam.setCreateTime(new Date());
        userTeam.setUpdateTime(new Date());
        boolean saveResult = userTeamService.save(userTeam);
        if(!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"保存关系表错误");
        }
        return userTeam.getId();
    }
}




