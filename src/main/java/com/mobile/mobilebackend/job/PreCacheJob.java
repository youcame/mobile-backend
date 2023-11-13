package com.mobile.mobilebackend.job;

import com.mobile.mobilebackend.mapper.UserMapper;
import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class PreCacheJob {

    @Resource
    UserService userService;

    @Scheduled(cron = "0 0 4 * * *")
    public void doPreCacheMatch(){
        System.out.println(1);
        List<User> list = userService.list();
        for (User user : list) {
            if(!"[]".equals(user.getTags())){
                userService.matchUser(5,user);
            }
        }
    }


}
