package com.demo.securitystudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.demo.securitystudy.config.properties.RememberMeProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final RememberMeProperties rememberMeProperties;

    // 시큐리티 필터 구획을 내가 커스텀하기 위한 Bean 등록
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        http
            // 수 많은 필터 중 CSRF 필터는 보안관련된 것
            .csrf(csrf-> csrf
                    .ignoringRequestMatchers("/logout")
                )

            // 로그인 필터 설정
            .formLogin(login-> login
                .loginProcessingUrl("/login") // 로그인 form이 아이디/비번을 보낼 POST 주소 (이 요청은 Spring Security 필터가 가로채서 인증 처리함(컨트롤없어도)
                .loginPage("/login") // 로그인 화면을 보여줄 주소(GET) 로그인 안한 사용자가 보호된 페이지로 들어오면 해당 경로로 보내짐
            )

            // 인가 필터 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()                                // 메인: 누구나 접근
                .requestMatchers("/join").permitAll()                            // 회원가입: 누구나 접근
                .requestMatchers("/login").permitAll()                           // 로그인 페이지: 누구나 접근
                .requestMatchers("/users").hasAnyRole("USER", "ADMIN")    // USER 또는 ADMIN 권한이면 접근
                .requestMatchers("/admin").hasRole("ADMIN")                     // ADMIN 권한만 접근
                .anyRequest().denyAll()                                           // 위에 없는 경로는 전부 접근 불가
            )

            // remember me 설정
            .rememberMe(me-> me
                    .key(rememberMeProperties.key())            // 쿠키 위조 방지용 서명 키 (서버만 알고 있어야 함)
                    .rememberMeParameter("remember-me")         // 로그인 form 체크박스의 name 값
                    .tokenValiditySeconds(14 * 24 * 60 * 60)    // 쿠키 유지 기간 (2주)
            )



        ;

        return http.build();
    }

    // 비밀번호 암호화용 Bean
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    // 크롬 개발자도구 자동 요청(/.well-known/**)과 그로 인한 404 /error는 시큐리티 필터 제외
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 크롬 개발자도구 자동 요청 + 그로 인한 /error는 시큐리티 필터를 안 거치게
        return web -> web.ignoring()
            .requestMatchers("/.well-known/**", "/error");
    }
}
