package com.mobile.mobilebackend.service;

import com.mobile.mobilebackend.model.domain.User;
import org.junit.jupiter.api.Assertions;
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
        List<String> list = Arrays.asList("东方","术力口");
        System.out.println(list.toString());
        List<User> userList = userService.searchUserByTags(list);
        Assertions.assertNotNull(userList);
    }
}