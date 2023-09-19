package com.mobile.mobilebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobile.mobilebackend.model.domain.UserTeam;
import com.mobile.mobilebackend.service.UserTeamService;
import com.mobile.mobilebackend.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author HUANG
* @description 针对表【user_team】的数据库操作Service实现
* @createDate 2023-09-19 14:21:54
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




