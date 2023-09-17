package com.mobile.mobilebackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void Test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("newInt", "1");
        Object a = valueOperations.get("anInt");
        System.out.println(a);
    }
}
