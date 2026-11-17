package com.demo.securitystudy.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.demo.securitystudy.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long accessTokenExpireTime;

    public JwtUtil(JwtProperties jwtProperties){
        key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
        accessTokenExpireTime = jwtProperties.accessTokenExpireTime();
    }


    // JWT 토큰생성
    public String createAccessToken(String username, String role){

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessTokenExpireTime);

        return Jwts.builder()
            .subject(username)                   // 토큰 주인 (sub): 누구의 토큰인지
            .claim("role", role)          // 커스텀 클레임: 권한 정보
            .claim("tokenType", "ACCESS")  // 커스텀 클레임: 토큰 종류 (ACCESS / REFRESH 구분용)
            .issuedAt(now)                       // 발급 시간 (iat)
            .expiration(expireDate)              // 만료 시간 (exp): 지나면 검증 실패
            .signWith(key)                       // 비밀키로 서명 (위조 방지)
            .compact();                          // 최종 JWT 문자열(header.payload.signature)로 만들기
    }

    // JWT 토큰 검증 후 payload(Claims) 꺼내기
    // Claims: JWT payload를 담은 객체 (sub, exp, iat + 직접 넣은 role, tokenType 등을 Map처럼 꺼내 씀)
    public Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)              // 발급할 때 쓴 비밀키로 서명 검증 설정
            .build()                      // 파서 생성
            .parseSignedClaims(token)     // 토큰 파싱 + 서명/만료 검증 (실패 시 예외 발생)
            .getPayload();                // 검증 통과한 payload(Claims) 반환
    }
}
