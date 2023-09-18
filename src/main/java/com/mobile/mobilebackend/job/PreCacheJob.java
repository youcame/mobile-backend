package com.mobile.mobilebackend.job;

import com.mobile.mobilebackend.mapper.UserMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PreCacheJob {

    @Resource
    private UserMapper userMapper;
    //每天执行
    @Scheduled(cron = "0 0 0 * * *")
    public void doPreCacheRecommend(){
        System.out.println(1);
    }


}
