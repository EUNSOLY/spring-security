package com.demo.securitystudy.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StreamUtils;

import com.demo.securitystudy.domain.user.dto.UserRequestDTO;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

/**
 * JSON 로그인 처리 필터
 * - POST /api/v1/login 요청의 JSON body({username, password})를 읽어서 인증 시도
 * - 기본 formLogin(form-data 방식) 대신 JSON 방식으로 로그인하기 위해 직접 구현
 */
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    // 이 필터가 처리할 요청: POST /api/v1/login (다른 요청은 그냥 통과)
    private static final RequestMatcher DEFAULT_PATH_REQUEST_MATCHER =
        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/v1/login");

    // 로그인 성공 시 실행할 핸들러 (JWT 발급 등)
    private final AuthenticationSuccessHandler authenticationSuccessHandler;


    public LoginFilter(
        AuthenticationManager authenticationManager,
        AuthenticationSuccessHandler authenticationSuccessHandler
    ) {
        super(DEFAULT_PATH_REQUEST_MATCHER, authenticationManager);  // 처리할 경로 + 인증 담당 매니저 등록
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    /**
     * 실제 인증 시도
     * 1. 요청 body(JSON)에서 username, password 꺼내기
     * 2. 인증 전 토큰(unauthenticated) 만들기
     * 3. AuthenticationManager에게 검증 맡기기 (→ UserDetailsService + PasswordEncoder)
     * @return 인증 성공 시 인증 완료된 Authentication (실패 시 AuthenticationException 발생)
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {

        try {
            // 요청 body(JSON)를 문자열로 읽기
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

            // JSON → DTO 변환
            UserRequestDTO userRequestDTO = objectMapper.readValue(messageBody, UserRequestDTO.class);

            String username = userRequestDTO.username();
            String password = userRequestDTO.password();

            // 인증 전 토큰 생성 (아직 검증 안 된 상태)
            UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);

            // 요청 부가 정보(IP, 세션ID 등) 저장
            this.setDetails(request, authRequest);

            // AuthenticationManager에게 실제 검증 위임
            return this.getAuthenticationManager().authenticate(authRequest);

        } catch (IOException e) {
            throw new AuthenticationServiceException("요청 body를 읽을 수 없습니다.", e);
        }
    }

    // 인증 토큰에 요청 부가 정보(WebAuthenticationDetails: IP, 세션 ID) 넣기
    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    // 인증 성공 시 호출 → 등록한 성공 핸들러 실행 (JWT 발급 및 응답)
    @Override
    protected void successfulAuthentication(
        HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authResult) throws IOException, ServletException {
        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
    }
}
