package com.mobile.mobilebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mobile.mobilebackend.authority.UserAuthority;
import com.mobile.mobilebackend.common.BaseResponse;
import com.mobile.mobilebackend.common.ErrorCode;
import com.mobile.mobilebackend.common.ResultUtil;
import com.mobile.mobilebackend.exception.BusinessException;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.model.dto.UserLoginRequest;
import com.mobile.mobilebackend.model.dto.UserRegisterRequest;
import com.mobile.mobilebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
 * 用户接口
 *
 * @author HUANG
 */
@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173/"}, allowCredentials ="true",allowedHeaders ={"*"})
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求为空");
        }
        long result = userService.userRegister(userRegisterRequest.getUserAccount(), userRegisterRequest.getPassword(), userRegisterRequest.getCheckPassword());
        return ResultUtil.success(result);
    }

    /**
     * 查询当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        } else {
            long id = user.getId();
            User currentUser = userService.getById(id);
            return ResultUtil.success(userService.getSafeUser(currentUser));
        }
    }
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(int pageSize, int pageNumber, HttpServletRequest request){
        QueryWrapper queryWrapper= new QueryWrapper<User>();
        Long id = UserAuthority.getCurrentUserId(request);
        String redisKey = String.format("mobile:user:recommend:%s",id);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Page<User> userList = (Page<User>) valueOperations.get(redisKey);
        if(userList != null){
            return ResultUtil.success(userList);
        }
        userList = userService.page(new Page<>(pageNumber, pageSize),queryWrapper);
        try {
            valueOperations.set(redisKey,userList, 60000, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            log.error("write redis error:",e);
        }
        return ResultUtil.success(userList);
    }
    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        return ResultUtil.success(userService.userLogin(userLoginRequest.getUserAccount(), userLoginRequest.getPassword(), request));
    }

    /**
     * 用户登出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        return ResultUtil.success(userService.userLogout(request));
    }

    /**
     * 搜索用户
     *
     * @param userAccount
     * @param username
     * @param request
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String userAccount, String username, HttpServletRequest request) {
        if (!UserAuthority.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userAccount)) {
            queryWrapper.like("userAccount", userAccount);
        }
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        return ResultUtil.success(userService.list(queryWrapper));
    }

    /**
     * 通过id删除用户
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody Long id, HttpServletRequest request) {
        if (!UserAuthority.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id < 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "删除的id不正确");
        } else return ResultUtil.success(userService.removeById(id));
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> getUserByTags(@RequestParam(required = false) List<String> tags, HttpServletRequest request) {
//        if(!UserAuthority.isAdmin(request)){
//            throw new BusinessException(ErrorCode.NO_AUTH);
//        }
        List<User> list = userService.searchUserByTags(tags);
        return ResultUtil.success(list);
    }

    /**
     * 更新队伍
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody User user, HttpServletRequest request){
        if(user == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"更新用户为空");
        }
        User loginUser = userService.getLoginUser(request);
        Boolean b = userService.updateFrontUser(user, loginUser, request);
        return ResultUtil.success(b);
    }
}