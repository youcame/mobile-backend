package com.mobile.mobilebackend.service;

import com.mobile.mobilebackend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class UserServiceTest {

    @Resource
    UserService userService;

    @Test
    void searchUserByTags() {
        List<String> list = Arrays.asList("java","python");
        System.out.println(list.toString());
        List<User> list1 = userService.searchUserByTags(['java']);
    }
}