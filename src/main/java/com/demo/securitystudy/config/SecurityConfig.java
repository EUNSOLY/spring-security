package com.demo.securitystudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    // 시큐리티 필터 구획을 내가 커스텀하기 위한 Bean 등록
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){

        // 수 많은 필터 중 CSRF 필터는 보안관련된 것 일단 현재는 disable(미사용)
        http.csrf(AbstractHttpConfigurer::disable)

        ;

        return http.build();
    }


    // 비밀번호 암호화용 Bean
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
