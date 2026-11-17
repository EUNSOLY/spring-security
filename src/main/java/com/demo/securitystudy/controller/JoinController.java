package com.demo.securitystudy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.demo.securitystudy.domain.user.dto.UserRequestDTO;
import com.demo.securitystudy.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class JoinController {
    private final UserService userService;


    // 회원가입 데이터 처리 메소드
    @PostMapping("/join")
    public String join(UserRequestDTO dto){
        userService.join(dto);

        return "redirect:/";
    }

    // 회원가입 페이지 제공 메소드
    @GetMapping("/join")
    public String joinPage(){
        return "join";
    }
}
