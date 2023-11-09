package com.mobile.mobilebackend;

import com.mobile.mobilebackend.utils.RecommandUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class MobileBackendApplicationTests {
    RecommandUtils recommandUtils = new RecommandUtils();
    @Test
    void contextLoads() {
        List<String> a1 = Arrays.asList("大一","java","game");
        List<String> a2 = Arrays.asList("python","大二","study");
        List<String> a3 = Arrays.asList("java","大一","study");
        System.out.println(recommandUtils.minTagDistance(a1,a2));
        System.out.println(recommandUtils.minTagDistance(a1,a3));
        System.out.println(recommandUtils.minTagDistance(a2,a3));
    }

}
