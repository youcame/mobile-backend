package com.mobile.mobilebackend.once;

import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.service.UserService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@EnableScheduling
public class InsertTest {
    @Resource
    UserService userService;

//    @Scheduled(cron = "0 52 17 * * ? *")
//    public void doPreCacheMatch(){
//        System.out.println(1);
//        List<User> list = userService.list();
//        for (User user : list) {
//            if(user.getTags()!=null){
//                userService.matchUser(5,user);
//            }
//        }
//    }
}
