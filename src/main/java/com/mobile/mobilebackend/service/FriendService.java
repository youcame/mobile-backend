package com.mobile.mobilebackend.service;

import com.mobile.mobilebackend.model.domain.Friend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mobile.mobilebackend.model.vo.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
* @author HUANG
* @description 针对表【friend】的数据库操作Service
* @createDate 2023-11-13 10:48:26
*/
public interface FriendService extends IService<Friend> {

    List<UserVo> getUserVoListFromDifferentStatus(int status, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException;
}
