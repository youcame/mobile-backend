package com.mobile.mobilebackend.script;
import java.util.Date;

import com.mobile.mobilebackend.mapper.UserMapper;
import com.mobile.mobilebackend.model.domain.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;

    @Scheduled(fixedRate = Long.MAX_VALUE, initialDelay = 2000)
    public void doInsert(){
//        final int INSERTNUMBER = 100;
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        System.out.println("good");
//        for(int i=1;i<=INSERTNUMBER;i++){
//            User user = new User();
//            user.setUsername("fakeUser");
//            user.setPassword("12345678");
//            user.setUserAccount("iceice");
//            user.setAvatarUrl("https://fastly.jsdelivr.net/npm/@vant/assets/logo.png");
//            user.setGender(0);
//            user.setPhone("123");
//            user.setEmail("123");
//            user.setUserStatus(0);
//            user.setIsDelete(0);
//            user.setUserRole(0);
//            userMapper.insert(user);
//        }
//        stopWatch.stop();
//        long totalTimeMillis = stopWatch.getTotalTimeMillis();
//        System.out.println(totalTimeMillis);
    }
}
