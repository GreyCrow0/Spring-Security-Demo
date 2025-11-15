package com.cygnet;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class AppTests {

    @Resource
    private PasswordEncoder  passwordEncoder;

    @Test
    void contextLoads() {
        System.out.printf(passwordEncoder.encode("123456"));
    }

}
