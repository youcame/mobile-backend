package com.mobile.mobilebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.mobile.mobilebackend.mapper")
@SpringBootApplication
public class MobileBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobileBackendApplication.class, args);
    }

}
