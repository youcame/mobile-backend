package com.mobile.mobilebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobile.mobilebackend.authority.UserAuthority;
import com.mobile.mobilebackend.model.domain.Friend;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.model.vo.UserVo;
import com.mobile.mobilebackend.service.FriendService;
import com.mobile.mobilebackend.mapper.FriendMapper;
import com.mobile.mobilebackend.service.UserService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
* @author HUANG
* @description 针对表【friend】的数据库操作Service实现
* @createDate 2023-11-13 10:48:26
*/
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend>
    implements FriendService{

    @Resource
    UserService userService;

    @Override
    public List<UserVo> getUserVoListFromDifferntStatus(int status, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        User currentUser = UserAuthority.getCurrentUser(request);
        Long id = currentUser.getId();
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("secondUserId",id);
        queryWrapper.eq("status",status);
        List<Friend> list = this.list(queryWrapper);
        List<UserVo> ansList = new ArrayList<>();
        for (Friend friend : list) {
            Long firstUserId = friend.getFirstUserId();
            User user = userService.getById(firstUserId);
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(userVo,user);
            ansList.add(userVo);
        }
        return ansList;
    }
}




