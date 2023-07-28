package com.mobile.mobilebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobile.mobilebackend.model.domain.user;
import com.mobile.mobilebackend.service.userService;
import com.mobile.mobilebackend.mapper.userMapper;
import org.springframework.stereotype.Service;

/**
* @author hp
* @description 针对表【user(用户信息表)】的数据库操作Service实现
* @createDate 2023-07-28 15:04:35
*/
@Service
public class userServiceImpl extends ServiceImpl<userMapper, user>
    implements userService{

}




