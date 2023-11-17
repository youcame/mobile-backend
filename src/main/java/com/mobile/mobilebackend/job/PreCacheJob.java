package com.mobile.mobilebackend.job;

import com.mobile.mobilebackend.model.domain.User;
import com.mobile.mobilebackend.service.UserService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class PreCacheJob {

    @Resource
    UserService userService;
    @Resource
    RedissonClient redissonClient;

    @Scheduled(cron = "0 0 4 * * *")
    public void doPreCacheMatch(){
        RLock lock = redissonClient.getLock("mobile:precache:match:lock");
        List<User> list = userService.list();
        try {
            if(lock.tryLock(0,30000L, TimeUnit.MILLISECONDS)) {
                for (User user : list) {
                    if (!"[]".equals(user.getTags())) {
                        userService.matchUser(5, user);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }


}
