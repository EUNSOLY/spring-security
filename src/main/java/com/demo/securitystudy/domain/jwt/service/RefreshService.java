package com.demo.securitystudy.domain.jwt.service;

import org.springframework.stereotype.Service;

import com.demo.securitystudy.domain.jwt.entity.RefreshEntity;
import com.demo.securitystudy.domain.jwt.repository.RefreshRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshService {
    private final RefreshRepository refreshRepository;

    public void saveRefreshToken(String username, String refreshToken){
        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refreshToken);

        refreshRepository.save(refreshEntity);
    }
}
