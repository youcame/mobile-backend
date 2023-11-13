package com.mobile.mobilebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mobile.mobilebackend.authority.UserAuthority;
import com.mobile.mobilebackend.common.BaseResponse;
import com.mobile.mobilebackend.common.ErrorCode;
import com.mobile.mobilebackend.common.IdRequest;
import com.mobile.mobilebackend.common.ResultUtil;
import com.mobile.mobilebackend.exception.BusinessException;
import com.mobile.mobilebackend.model.domain.Team;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.model.domain.UserTeam;
import com.mobile.mobilebackend.model.dto.*;
import com.mobile.mobilebackend.model.vo.UserTeamVo;
import com.mobile.mobilebackend.model.vo.UserVo;
import com.mobile.mobilebackend.service.TeamService;
import com.mobile.mobilebackend.service.UserService;
import com.mobile.mobilebackend.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * 队伍接口
 *
 * @author HUANG
 */
@Slf4j
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173/"}, allowCredentials = "true", allowedHeaders = {"*"})
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;
    @Resource
    private UserTeamService userTeamService;

    /**
     * 创建队伍
     * @param teamAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> createTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数为空");
        }
        User user = UserAuthority.getCurrentUser(request);
        Team team=new Team();
        BeanUtils.copyProperties(team,teamAddRequest);
        Long save = teamService.addTeam(team, user);
        return ResultUtil.success(save);
    }

    /**
     * 删除队伍
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if(idRequest==null || idRequest.getId()<=0){
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数为空");
        }
        Long id = idRequest.getId();
        User user = UserAuthority.getCurrentUser(request);
        boolean delete = teamService.deleteTeam(id,user);
        return ResultUtil.success(true);
    }

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest
     * @param request
     * @return 更新成功
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        User loginUser = UserAuthority.getCurrentUser(request);
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不需要更新！");
        }
        Team team = new Team();
        BeanUtils.copyProperties(team, teamUpdateRequest);
        boolean update = teamService.updateTeam(team, loginUser);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍失败");
        }
        return ResultUtil.success(true);
    }

    /**
     * 通过id获取队伍
     *
     * @param id
     * @return
     */
    @GetMapping("/getById")
    public BaseResponse<Team> getTeamById(@RequestParam long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数为空");
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAM_NULL);
        }
        return ResultUtil.success(team);
    }

    /**
     * 通过队伍id获取用户列表
     *
     * @param teamId
     * @return
     */
    @GetMapping("/getByTeamId")
    public BaseResponse<List<UserVo>> getByTeamId(@RequestParam Long teamId) throws InvocationTargetException, IllegalAccessException {
        if (teamId==null||teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数为空");
        }
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        List<UserTeam> list = userTeamService.list(queryWrapper);
        List<UserVo> ansList = new ArrayList<>();
        for (UserTeam userTeam : list) {
            UserVo userVo = new UserVo();
            Long userId = userTeam.getUserId();
            User user = userService.getById(userId);
            BeanUtils.copyProperties(userVo,user);
            ansList.add(userVo);
        }
        return ResultUtil.success(ansList);
    }

    /**
     * 获取队伍列表
     * @param teamQueryRequest
     * @param request
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @GetMapping("/list")
    public BaseResponse<List<UserTeamVo>> getTeams(TeamQueryRequest teamQueryRequest, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "传入数据为空");
        }
        List<UserTeamVo> list = teamService.teamList(teamQueryRequest);
        //判断当前用户是否在队伍中，在的话则剔除掉
        User currentUser = UserAuthority.getCurrentUser(request);
        teamService.filterTeamWithoutMe(list,currentUser);
        return ResultUtil.success(list);
    }

    /**
     * 获取我创建的队伍
     * @param teamQueryRequest
     * @param request
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @GetMapping("/list/myTeams")
    public BaseResponse<List<UserTeamVo>> getMyTeams(TeamQueryRequest teamQueryRequest, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "传入数据为空");
        }
        User currentUser = UserAuthority.getCurrentUser(request);
        teamQueryRequest.setCreatorId(currentUser.getId());
        List<UserTeamVo> userTeamVos = teamService.teamList(teamQueryRequest);
        return ResultUtil.success(userTeamVos);
    }

    /**
     * 获取我加入的队伍列表
     * @param teamQueryRequest
     * @param request
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @GetMapping("/list/myJoinTeam")
    public BaseResponse<List<UserTeamVo>> getMyJoinTeams(TeamQueryRequest teamQueryRequest, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "传入数据为空");
        }
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        User currentUser = UserAuthority.getCurrentUser(request);
        queryWrapper.eq("userId",currentUser.getId());
        List<UserTeam> list = userTeamService.list(queryWrapper);
        HashSet<Long> hs = new HashSet<>();
        for (UserTeam userTeam : list) {
            Long teamId = userTeam.getTeamId();
            QueryWrapper<Team> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("id",teamId);
            Team team = teamService.getOne(queryWrapper1);
            if(!team.getCreatorId().equals(currentUser.getId()))hs.add(teamId);
        }
        if(hs.size()==0)return ResultUtil.success(new ArrayList<>());
        ArrayList<Long> teamIdList = new ArrayList<>(hs);
        teamQueryRequest.setIdList(teamIdList);
        List<UserTeamVo> userTeamVoList = teamService.teamList(teamQueryRequest);
        return this.getTeams(teamQueryRequest,request);
    }

    /**
     * 获取队伍分页列表
     * @param teamQueryRequest
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamsPage(TeamQueryRequest teamQueryRequest, Integer pageSize, Integer pageNumber) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "传入数据为空");
        }
        if (pageSize == null) pageSize = 10;
        if (pageNumber == null) pageNumber = 1;
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team, teamQueryRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> page = teamService.page(new Page<>(pageNumber, pageSize), queryWrapper);
        return ResultUtil.success(page);
    }


    /**
     * 加入队伍
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) throw new BusinessException(ErrorCode.PARAM_ERROR, "请求参数为空");
        User loginUser = UserAuthority.getCurrentUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtil.success(true);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求参数不能为空");
        }
        User user = UserAuthority.getCurrentUser(request);
        boolean b = teamService.quitTeam(teamQuitRequest, user);
        return ResultUtil.success(true);
    }

}