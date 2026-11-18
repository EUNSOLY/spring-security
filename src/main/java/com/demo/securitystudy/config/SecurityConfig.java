package com.demo.securitystudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;

import com.demo.securitystudy.security.filter.JwtFilter;
import com.demo.securitystudy.security.filter.LoginFilter;
import com.demo.securitystudy.security.handler.LoginSuccessHandler;
import com.demo.securitystudy.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final LoginSuccessHandler loginSuccessHandler;
    private final JwtUtil jwtUtil;

    // 시큐리티 설정 Bean 등록
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){

        http
            // CSRF 필터 비활성화 (JWT를 Authorization 헤더로 보내면 브라우저가 자동 첨부하지 않아 CSRF 공격 불가)
            .csrf(AbstractHttpConfigurer::disable)
            // 기본 로그인 설정 비활성화
            .formLogin(AbstractHttpConfigurer::disable)
            // 커스텀 필터 추가 UsernamePasswordAuthenticationFilter 앞에
            .addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration),loginSuccessHandler), UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(new JwtFilter(jwtUtil), SecurityContextHolderFilter.class)
            // 모든 경로 접근 허용 (requestMatchers("/**").permitAll() 도 가능)
            // .authorizeHttpRequests(auth-> auth.anyRequest().permitAll())
            // 경로에 따른 접근권한(인가) 설정
            .authorizeHttpRequests(auth-> auth
                .requestMatchers("/api/v1").permitAll()
                .requestMatchers("/api/v1/").permitAll()
                .requestMatchers("/api/v1/user").hasRole("USER")
                .anyRequest().hasRole("ADMIN")
            )
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

    // 원래 자동 등록이지만 사용하기위해 Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }
}
