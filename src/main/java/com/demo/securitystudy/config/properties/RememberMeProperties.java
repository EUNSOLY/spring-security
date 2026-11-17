package com.demo.securitystudy.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

// application.yaml의 security.rememberme.* 값을 담는 설정 클래스
@ConfigurationProperties(prefix = "security.rememberme")
public record RememberMeProperties(String key) { }

