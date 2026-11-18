package com.demo.securitystudy.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class SampleController {

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/user")
    public String user(){
        log.info("/user 경로 로그인 사용자 테스트 : {}, ",SecurityContextHolder.getContext().getAuthentication().getName());

        return "user";
    }

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }

}
