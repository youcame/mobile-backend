package com.mobile.mobilebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mobile.mobilebackend.Authority.UserAuthority;
import com.mobile.mobilebackend.common.BaseResponse;
import com.mobile.mobilebackend.common.ErrorCode;
import com.mobile.mobilebackend.common.ResultUtil;
import com.mobile.mobilebackend.exception.BusinessException;
import com.mobile.mobilebackend.model.domain.Team;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.model.dto.TeamQuery;
import com.mobile.mobilebackend.model.dto.UserLoginRequest;
import com.mobile.mobilebackend.model.dto.UserRegisterRequest;
import com.mobile.mobilebackend.service.TeamService;
import com.mobile.mobilebackend.service.UserService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mobile.mobilebackend.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 队伍接口
 *
 * @author HUANG
 */
@Slf4j
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173/"}, allowCredentials ="true",allowedHeaders ={"*"})
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody Team team, HttpServletRequest request){
        if(team == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"参数为空");
        }
        User user = UserAuthority.getCurrentUser(request);
        Long save = teamService.addTeam(team, user);
        return ResultUtil.success(save);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id){
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"参数为空");
        }
        boolean delete = teamService.removeById(id);
        if(!delete){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建队伍失败");
        }
        return ResultUtil.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> deleteTeam(@RequestBody Team team){
        if(team == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"参数为空");
        }
        boolean update = teamService.updateById(team);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队伍失败");
        }
        return ResultUtil.success(true);
    }

    @GetMapping("/getById")
    public BaseResponse<Team> getTeamById(@RequestBody long id){
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"参数为空");
        }
        Team team = teamService.getById(id);
        if(team == null){
            throw new BusinessException(ErrorCode.PARAM_NULL);
        }
        return ResultUtil.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<Team>> getTeams(TeamQuery teamQuery){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"传入数据为空");
        }
        Team team = new Team();
        try{
            BeanUtils.copyProperties(team, teamQuery);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        List<Team> list = teamService.list(queryWrapper);
        return ResultUtil.success(list);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamsPage(TeamQuery teamQuery,Integer pageSize,Integer pageNumber){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"传入数据为空");
        }
        if(pageSize == null)pageSize = 10;
        if(pageNumber == null)pageNumber = 1;
        Team team = new Team();
        try{
            BeanUtils.copyProperties(team, teamQuery);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> page = teamService.page(new Page<>(pageNumber, pageSize), queryWrapper);
        return ResultUtil.success(page);
    }
}