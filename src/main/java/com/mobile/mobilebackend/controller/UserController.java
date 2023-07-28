package com.mobile.mobilebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mobile.mobilebackend.Authority.UserAuthority;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.model.dto.UserLoginRequest;
import com.mobile.mobilebackend.model.dto.UserRegisterRequest;
import com.mobile.mobilebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.mobile.mobilebackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author HUANG
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        long result = userService.userRegister(userRegisterRequest.getUserAccount(), userRegisterRequest.getPassword(), userRegisterRequest.getCheckPassword());
        return result;
    }

    @GetMapping("/current")
    public User getCurrentUser(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        if(user == null){
            return null;
        }
        else {
            long id =user.getId();
            User currentUser = userService.getById(id);
            //todo 校验用户是否合法
            return userService.getSafeUser(currentUser);
        }

    }
    @PostMapping("/login")
    public User userRegister(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        return userService.userLogin(userLoginRequest.getUserAccount(), userLoginRequest.getPassword(), request);
    }

    @PostMapping("/logout")
    public Integer userLogout(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return userService.userLogout(request);
    }

    @GetMapping("/search")
    public List<User> searchUsers(String userAccount, String username, HttpServletRequest request) {
        if(!UserAuthority.isAdmin(request)){
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userAccount)) {
            queryWrapper.like("userAccount", userAccount);
        }
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        return userService.list(queryWrapper);
    }

    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody Long id, HttpServletRequest request) {
        if(!UserAuthority.isAdmin(request)){
            return false;
        }
        if (id < 0) {
            return false;
        } else return userService.removeById(id);
    }

//    @PostMapping("/update")
//    public boolean updateUser(@RequestBody ModifyUserRequest user, HttpServletRequest request){
//        if(user == null){
//            return false;
//        }
//        else{
//            return userService.updateFrontUser(user);
//        }
//    }

}
