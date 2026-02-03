package org.example.music.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean  // 这个注解告诉Spring：把这个对象创建出来，放到容器中
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // 声明BCryptPasswordEncoder Bean，供容器自动装配
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}