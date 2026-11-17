package com.demo.securitystudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorityAuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

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
                .requestMatchers("/").permitAll()                     // 메인: 누구나 접근
                .requestMatchers("/join").permitAll()                 // 회원가입: 누구나 접근
                .requestMatchers("/login").permitAll()                // 로그인 페이지: 누구나 접근
                .requestMatchers("/users").hasAnyRole("USER")         // USER 또는 ADMIN 권한이면 접근
                .requestMatchers("/admin").access(customAuthorizationManager())
                // .requestMatchers("/admin").hasRole("ADMIN")           // ADMIN 권한만 접근
                .anyRequest().denyAll()                               // 위에 없는 경로는 전부 접근 불가
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


    // ROLE 계층 설정: 상위 ROLE은 하위 ROLE 권한을 자동으로 가짐
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl
            .withRolePrefix("ROLE_")                           // ROLE 접두사 (기본값도 "ROLE_")
            .role("ADMIN").implies("USER")       // ADMIN > USER : ADMIN은 USER 권한도 가짐
            .build();
    }

    /**
     * 커스텀 인가 로직: 직접 조건을 짜서 접근 허용 여부를 결정
     * (지역별, 플래그별 등 여러 조건으로 체크 가능)
     * @return 요청마다 인가 허용 여부를 판단하는 AuthorizationManager
     */
    private AuthorizationManager<RequestAuthorizationContext> customAuthorizationManager(){

        return (authentication, context)-> {
            Authentication auth = authentication.get();
            if (auth == null) {
                return new AuthorizationDecision(false);   // 인증 정보가 없으면 거부
            }
            // ROLE_ADMIN 권한이 있는지 확인
            boolean allowed = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

            return new AuthorizationDecision(allowed);  // true면 허용, false면 403
        };
    }


    // 크롬 개발자도구 자동 요청(/.well-known/**)과 그로 인한 404 /error는 시큐리티 필터 제외
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 크롬 개발자도구 자동 요청 + 그로 인한 /error는 시큐리티 필터를 안 거치게
        return web -> web.ignoring()
            .requestMatchers("/.well-known/**", "/error");
    }
}
