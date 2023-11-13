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
        queryWrapper.eq("secondUserId",receiverId);
        queryWrapper.eq("firstUserId",senderId);
        long count = friendService.count(queryWrapper);
        if(count!=0){
            throw new BusinessException(ErrorCode.ACCOUNT_SAME,"请勿重复发送请求");
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
    @GetMapping("/unresolved")
    public BaseResponse<List<UserVo>> getUnresolvedRequest(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        List<UserVo> list = friendService.getUserVoListFromDifferentStatus(0,request);
        return ResultUtil.success(list);
    }

    /**
     * 拒绝好友请求
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/deny")
    public BaseResponse<Boolean> denyRequest(@RequestParam Long id, HttpServletRequest request){
        if(id==null||id<=0){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User currentUser = UserAuthority.getCurrentUser(request);
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("secondUserId",currentUser.getId());
        queryWrapper.eq("firstUserId",id);
        boolean remove = friendService.remove(queryWrapper);
        if(!remove){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"不存在这名好友");
        }
        return ResultUtil.success(true);
    }

    /**
     * 接受好友请求
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/accept")
    public BaseResponse<Boolean> acceptRequest(@RequestParam Long id, HttpServletRequest request){
        if(id==null||id<=0){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        User currentUser = UserAuthority.getCurrentUser(request);
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("secondUserId",currentUser.getId());
        queryWrapper.eq("firstUserId",id);
        Friend friend = friendService.getOne(queryWrapper);
        if(friend.getStatus()==1){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"您已经添加了该好友~");
        }
        if(friend==null){
            throw new BusinessException(ErrorCode.PARAM_NULL,"好友请求不存在");
        }
        friend.setStatus(1);
        boolean b = friendService.updateById(friend);
        return ResultUtil.success(b);
    }



    /**
     * 获取好友列表
     * @param request
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @GetMapping("/list")
    public BaseResponse<List<UserVo>> getFriendList(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        List<UserVo> list = friendService.getUserVoListFromDifferentStatus(1,request);
        return ResultUtil.success(list);
    }
}
