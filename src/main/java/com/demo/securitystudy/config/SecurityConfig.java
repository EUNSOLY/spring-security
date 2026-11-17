package com.demo.securitystudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    // 시큐리티 설정 Bean 등록
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){

        http
            // CSRF 필터 비활성화 (JWT를 Authorization 헤더로 보내면 브라우저가 자동 첨부하지 않아 CSRF 공격 불가)
            .csrf(AbstractHttpConfigurer::disable)
            // 모든 경로 접근 허용 (requestMatchers("/**").permitAll() 도 가능)
            .authorizeHttpRequests(auth-> auth.anyRequest().permitAll())
            // 세션 설정 (토큰 인증이기 때문에 서버에서 기억할 필요 X)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            ;
        return http.build();
    }

    // 비밀번호 해시화 Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
