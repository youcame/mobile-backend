package com.mobile.mobilebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mobile.mobilebackend.authority.UserAuthority;
import com.mobile.mobilebackend.common.BaseResponse;
import com.mobile.mobilebackend.common.ErrorCode;
import com.mobile.mobilebackend.common.ResultUtil;
import com.mobile.mobilebackend.exception.BusinessException;
import com.mobile.mobilebackend.model.domain.Friend;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.model.vo.UserVo;
import com.mobile.mobilebackend.service.FriendService;
import com.mobile.mobilebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 队伍接口
 *
 * @author HUANG
 */
@Slf4j
@RestController
@RequestMapping("/friend")
@CrossOrigin(origins = {"http://localhost:5173/"}, allowCredentials = "true", allowedHeaders = {"*"})
public class FriendController {

    @Resource
    FriendService friendService;

    @Resource
    UserService userService;

    /**
     * 发送好友请求
     * @param receiverId
     * @param request
     * @return
     */
    @PostMapping("/send/request")
    public BaseResponse<Boolean> sentFriendRequest(@RequestParam Long receiverId, HttpServletRequest request){
        if(receiverId==null||receiverId<=0){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User currentUser = UserAuthority.getCurrentUser(request);
        Long senderId = currentUser.getId();
        if(senderId.equals(receiverId)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"不能给自己发送请求哦~");
        }
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("firstUserId",senderId);
        queryWrapper.eq("secondUserId",receiverId);
        long count = friendService.count(queryWrapper);
        if(count!=0){
            throw new BusinessException(ErrorCode.ACCOUNT_SAME,"已发送过请求,请勿重复发送");
        }
        Friend friend = new Friend();
        friend.setFirstUserId(senderId);
        friend.setSecondUserId(receiverId);
        friend.setStatus(0);
        boolean save = friendService.save(friend);
        return ResultUtil.success(save);
    }

    /**
     * 获取没解决的好友请求
     * @param request
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/unresolved")
    public BaseResponse<List<UserVo>> getUnresolvedRequest(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        List<UserVo> list = friendService.getUserVoListFromDifferntStatus(0,request);
        return ResultUtil.success(list);
    }

    /**
     * 获取好友列表
     * @param request
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/friend/list")
    public BaseResponse<List<UserVo>> getFriendList(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        List<UserVo> list = friendService.getUserVoListFromDifferntStatus(1,request);
        return ResultUtil.success(list);
    }
}
