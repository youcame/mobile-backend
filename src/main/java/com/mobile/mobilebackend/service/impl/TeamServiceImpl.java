package com.mobile.mobilebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobile.mobilebackend.common.ErrorCode;
import com.mobile.mobilebackend.common.ResultUtil;
import com.mobile.mobilebackend.exception.BusinessException;
import com.mobile.mobilebackend.model.domain.Team;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.model.domain.UserTeam;
import com.mobile.mobilebackend.model.dto.TeamJoinRequest;
import com.mobile.mobilebackend.model.dto.TeamQueryRequest;
import com.mobile.mobilebackend.model.dto.TeamQuitRequest;
import com.mobile.mobilebackend.model.vo.UserTeamVo;
import com.mobile.mobilebackend.model.vo.UserVo;
import com.mobile.mobilebackend.service.TeamService;
import com.mobile.mobilebackend.mapper.TeamMapper;
import com.mobile.mobilebackend.service.UserService;
import com.mobile.mobilebackend.service.UserTeamService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static com.mobile.mobilebackend.constant.TeamConstant.*;

/**
 * @author HUANG
 * @description 针对表【team】的数据库操作Service实现
 * @createDate 2023-09-19 14:21:54
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    @Resource
    TeamMapper teamMapper;
    @Resource
    UserTeamService userTeamService;

    @Resource
    UserService userService;

    /**
     * 创建队伍校验
     *
     * @param team
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //队伍不为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        //创建人不为空
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        final Long userId = loginUser.getId();
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        //队伍人数限制
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "队伍人数不符合要求");
        }
        String teamName = team.getName();
        //队伍名字长度限制
        if (teamName.length() > 20 || StringUtils.isBlank(teamName)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "队伍名称过长或为空");
        }
        String description = team.getDescription();
        //队伍描述限制
        if (description.length() > 512 && StringUtils.isNotBlank(description)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "队伍描述过长");
        }
        int status = Optional.ofNullable(team.getStatus()).orElse(-1);
        //检测队伍状态
        if (status != PUBLIC_TEAM && status != PRIVATE_TEAM && status != SECRET_TEAM) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "队伍状态错误");
        }
        String password = team.getPassword();
        if (status == PRIVATE_TEAM && (StringUtils.isBlank(password) || password.length() > 32)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "密码格式错误");
        }
        Date expireTime = team.getExpireTime();
        //检测队伍是否过期
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAM_NULL, "队伍已过时");
        }
        //检测创建人一共创建的队伍数量
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creatorId", userId);
        long count = this.count(queryWrapper);
        if (count >= 10) {
            throw new BusinessException(ErrorCode.NO_AUTH, "创建队伍数过多");
        }
        team.setCreatorId(userId);
        team.setId(null);
        team.setCreateTime(new Date());
        team.setUpdateTime(new Date());
        boolean save = this.save(team);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存队伍失败");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        userTeam.setCreateTime(new Date());
        userTeam.setUpdateTime(new Date());
        boolean saveResult = userTeamService.save(userTeam);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存关系表错误");
        }
        return team.getId();
    }

    /**
     * 搜索队伍（List）中的所有人
     * @param teamQueryRequest
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Override
    public List<UserTeamVo> teamList(TeamQueryRequest teamQueryRequest) throws InvocationTargetException, IllegalAccessException {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if(teamQueryRequest !=null){
            Long id = teamQueryRequest.getId();
            if(id!=null && id>0){
                queryWrapper.eq("id", id);
            }
            List<Long> ids = teamQueryRequest.getIdList();
            if(CollectionUtils.isNotEmpty(ids)) {
                queryWrapper.in("id", ids);
            }
            String searchText = teamQueryRequest.getSearchText();
            if(StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw -> qw.like("name",searchText).or().like("description",searchText));
            }
            String name = teamQueryRequest.getName();
            if(StringUtils.isNotBlank(name)){
                queryWrapper.like("name", name);
            }
            String description = teamQueryRequest.getDescription();
            if(StringUtils.isNotBlank(description)){
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQueryRequest.getMaxNum();
            if(maxNum!=null && maxNum>0){
                queryWrapper.eq("maxNum", maxNum);
            }
            Long creatorId = teamQueryRequest.getCreatorId();
            if(creatorId!=null && creatorId>0){
                queryWrapper.eq("creatorId",creatorId);
            }
            Integer status = teamQueryRequest.getStatus();
            if(status!=null && status>=PUBLIC_TEAM){
                queryWrapper.eq("status",status);
            }
        }
        List<UserTeamVo> userTeamVoList = new ArrayList<>();
        //获取到的队伍列表
        List<Team> teamList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        for (Team team : teamList) {
            Long creatorId = team.getCreatorId();
            if(creatorId==null)continue;
            User user = userService.getById(creatorId);
            User safeUser = userService.getSafeUser(user);
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(userVo, safeUser);
            UserTeamVo userTeamVo = new UserTeamVo();
            BeanUtils.copyProperties(userTeamVo, team);
            userTeamVo.setCreateUser(userVo);
            //下面查找队伍中的用户（数量），list先不查
            QueryWrapper<UserTeam> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("teamId",team.getId());
            Long n=userTeamService.count(queryWrapper1);
            userTeamVo.setTeamNowNumber(Math.toIntExact(n));
            userTeamVoList.add(userTeamVo);
        }
        return userTeamVoList;
    }

    /**
     * 更新队伍
     * @param team
     * @param loginUser
     * @return
     */
    @Override
    public boolean updateTeam(Team team, User loginUser) {
        Long id = team.getId();
        if(id==null||id<0){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"id不存在或为空");
        }
        Team oleTeam = this.getById(id);
        if(oleTeam == null)throw new BusinessException(ErrorCode.PARAM_NULL,"查不到这个队伍");
        if(!oleTeam.getCreatorId().equals(loginUser.getId())&&loginUser.getUserRole()!=1)throw new BusinessException(ErrorCode.NO_AUTH);
        boolean b = this.updateById(team);
        return b;
    }

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        Long teamId = teamJoinRequest.getTeamId();
        String password = teamJoinRequest.getPassword();
        Long currentUserId = loginUser.getId();
        synchronized (this) {
            //判断是否满足加入条件
            QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teamId", teamId);
            long nowNumber = userTeamService.count(queryWrapper);
            queryWrapper.eq("userId", currentUserId);
            List<UserTeam> list = userTeamService.list(queryWrapper);
            //我是否加入队伍（1为加入）
            long count = userTeamService.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.ACCOUNT_SAME, "已经加入这个队伍了");
            }
            Team team = this.getById(teamId);
            if (team == null) {
                throw new BusinessException(ErrorCode.PARAM_NULL, "无该队伍");
            }
            Integer status = team.getStatus();
            if (status.equals(PRIVATE_TEAM)) {
                if (StringUtils.isBlank(password) || !team.getPassword().equals(password)) {
                    throw new BusinessException(ErrorCode.VERIFY_ERROR, "密码错误");
                }
            }
            if (status.equals(SECRET_TEAM)) {
                throw new BusinessException(ErrorCode.NO_AUTH, "禁止加入私有队伍");
            }
            if (team.getMaxNum() <= nowNumber) {
                throw new BusinessException(ErrorCode.NO_AUTH, "该队伍人数已满");
            }
            if (team.getExpireTime() != null && team.getExpireTime().before(new Date())) {
                throw new BusinessException(ErrorCode.NO_AUTH, "队伍已过期");
            }

            //修改队伍信息
            UserTeam userTeam = new UserTeam();
            userTeam.setUserId(currentUserId);
            userTeam.setTeamId(teamId);
            userTeam.setJoinTime(new Date());
            userTeam.setCreateTime(new Date());
            userTeam.setUpdateTime(new Date());
            boolean save = userTeamService.save(userTeam);
            if (!save) throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");
            return true;
        }
    }

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param user
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User user) {
        if(teamQuitRequest==null)throw new BusinessException(ErrorCode.PARAM_ERROR,"请求参数为空");
        Long teamId = teamQuitRequest.getId();
        if(teamId==null||teamId<0)throw new BusinessException(ErrorCode.PARAM_ERROR,"传入Id不合法");
        Team team = this.getById(teamId);
        if(team==null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"找不到队伍");
        }
        Long userId=user.getId();
        QueryWrapper<UserTeam> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        long teamHasJoinNumber=userTeamService.count(queryWrapper);//当前队伍剩余人数
        queryWrapper.eq("userId",userId);
        long count = userTeamService.count(queryWrapper);
        if(count!=1)throw new BusinessException(ErrorCode.NO_AUTH,"您已经退出队伍了");
        //只剩最后一个人，移除队伍
        if(teamHasJoinNumber==1){
            boolean remove = userTeamService.remove(queryWrapper);
            if(!remove)throw new BusinessException(ErrorCode.SYSTEM_ERROR,"退出队伍失败");
            boolean remove1 = this.removeById(teamId);
            if(!remove1)throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍信息失败");
        }else{
            //退出的人是队长，转接队长
            if(team.getCreatorId().equals(userId)){
                QueryWrapper<UserTeam> newQueryWrapper=new QueryWrapper<>();
                newQueryWrapper.eq("teamId",teamId);
                newQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> list = userTeamService.list(newQueryWrapper);
                if(list==null||list.size()<=1)throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍人数已经小于二了");
                team.setCreatorId(list.get(1).getUserId());
                boolean updateResult = this.updateById(team);
                if(!updateResult)throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队长失败");
                boolean remove = userTeamService.remove(queryWrapper);
                if(!remove)throw new BusinessException(ErrorCode.SYSTEM_ERROR,"退出队伍失败");
            }else {
                boolean remove = userTeamService.remove(queryWrapper);
                if(!remove)throw new BusinessException(ErrorCode.SYSTEM_ERROR,"退出队伍失败");
            }
        }
        return true;
    }

    /**
     * 解散队伍
     * @param id
     * @param user
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(@RequestBody Long id, User user) {
        if(id==null||id<0)throw new BusinessException(ErrorCode.PARAM_ERROR,"请求参数为空或小于零");
        Team team = this.getById(id);
        if(team==null)throw new BusinessException(ErrorCode.PARAM_ERROR,"队伍不存在");
        if(!team.getCreatorId().equals(user.getId()))throw new BusinessException(ErrorCode.NO_AUTH,"您没权限解散队伍");
        boolean result = this.removeById(team);
        if(!result)throw new BusinessException(ErrorCode.SYSTEM_ERROR,"解散队伍失败");
        QueryWrapper<UserTeam> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("teamId",id);
        boolean result2 = userTeamService.remove(queryWrapper);
        if(!result2)throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍关联关系失败");
        return true;
    }

    @Override
    public List<UserTeamVo> filterTeamWithoutMe(List<UserTeamVo> list, User currentUser) {
        List<Long> teamIdList = list.stream().map(UserTeamVo::getId).collect(Collectors.toList());
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",currentUser.getId());
        if(teamIdList.size()==0)return new ArrayList<>();
        queryWrapper.in("teamId",teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        Set<Long> collect = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
        list.forEach(userTeamVo -> {
            userTeamVo.setIsInTeam(collect.contains(userTeamVo.getId()));
        });
        return list;
    }
}




