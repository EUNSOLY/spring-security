package com.demo.securitystudy.domain.jwt.service;

import org.springframework.stereotype.Service;

import com.demo.securitystudy.domain.jwt.entity.RefreshEntity;
import com.demo.securitystudy.domain.jwt.repository.RefreshRepository;
import com.demo.securitystudy.util.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshService {
    private final RefreshRepository refreshRepository;
    private final JwtUtil jwtUtil;


    public void saveRefreshToken(String username, String refreshToken){
        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refreshToken);

        refreshRepository.save(refreshEntity);
    }

    // refresh 토큰 검증
    public String refresh(String refreshToken){

        // 리프레쉬 토큰 검증
        Claims claims;
        try {
            claims = jwtUtil.getClaims(refreshToken);
        }catch (Exception e){
            throw new IllegalArgumentException("유효하지 않은 리프레쉬 토큰입니다.");
        }

        String tokenType = claims.get("tokenType", String.class);

        if(!"REFRESH".equals(tokenType)){
            throw new IllegalArgumentException("Refresh 토큰이 아닙니다.");
        }

        // 화이트 리스트 확인(DB에 있는거)
        boolean exists = refreshRepository.existsByRefresh(refreshToken);

        if(!exists){
            throw new IllegalArgumentException("로그아웃 되었거나 만료된 Reresh Token 입니다.");
        }

        // 재발급
        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        return jwtUtil.createAccessToken(username,role);
    }
}
