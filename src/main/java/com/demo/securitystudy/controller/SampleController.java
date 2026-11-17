package com.demo.securitystudy.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SampleController {

    @GetMapping("/")
    public String index(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        log.info("로그인 유저이름 : {}", username);
        log.info("로그인 유저권한 : {}", role);
        return "index";
    }

    @GetMapping("/users")
    public String user(){
        return "user";
    }

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }

}
