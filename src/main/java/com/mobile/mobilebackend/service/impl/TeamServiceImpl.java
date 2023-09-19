package com.mobile.mobilebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobile.mobilebackend.model.domain.Team;
import com.mobile.mobilebackend.service.TeamService;
import com.mobile.mobilebackend.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author HUANG
* @description 针对表【team】的数据库操作Service实现
* @createDate 2023-09-19 14:21:54
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




