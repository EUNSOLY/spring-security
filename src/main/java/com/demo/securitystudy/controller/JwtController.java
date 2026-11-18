package com.demo.securitystudy.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.securitystudy.domain.jwt.dto.RefreshRequestDTO;
import com.demo.securitystudy.domain.jwt.service.RefreshService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class JwtController {
    private final RefreshService refreshService;

    @PostMapping("/jwt/refresh")
    public String refresh(@RequestBody RefreshRequestDTO dto){
        return refreshService.refresh(dto.refreshToken());
    }
}
