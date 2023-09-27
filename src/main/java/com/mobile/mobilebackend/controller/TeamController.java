package com.mobile.mobilebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mobile.mobilebackend.authority.UserAuthority;
import com.mobile.mobilebackend.common.BaseResponse;
import com.mobile.mobilebackend.common.ErrorCode;
import com.mobile.mobilebackend.common.ResultUtil;
import com.mobile.mobilebackend.exception.BusinessException;
import com.mobile.mobilebackend.model.domain.Team;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.model.dto.*;
import com.mobile.mobilebackend.model.vo.UserTeamVo;
import com.mobile.mobilebackend.service.TeamService;
import com.mobile.mobilebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
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
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数为空");
        }
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
     * 通过id获取
     *
     * @param id
     * @return
     */
    @GetMapping("/getById")
    public BaseResponse<Team> getTeamById(@RequestBody long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数为空");
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAM_NULL);
        }
        return ResultUtil.success(team);
    }


    @GetMapping("/list")
    public BaseResponse<List<UserTeamVo>> getTeams(TeamQuery teamQuery, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "传入数据为空");
        }
        List<UserTeamVo> list = teamService.teamList(teamQuery);
        return ResultUtil.success(list);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamsPage(TeamQuery teamQuery, Integer pageSize, Integer pageNumber) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "传入数据为空");
        }
        if (pageSize == null) pageSize = 10;
        if (pageNumber == null) pageNumber = 1;
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team, teamQuery);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> page = teamService.page(new Page<>(pageNumber, pageSize), queryWrapper);
        return ResultUtil.success(page);
    }

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