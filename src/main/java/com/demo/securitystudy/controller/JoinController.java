package com.demo.securitystudy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.securitystudy.domain.user.dto.UserRequestDTO;
import com.demo.securitystudy.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class JoinController {
    private final UserService userService;


    @PostMapping("/join")
    public String join(@RequestBody UserRequestDTO dto){
        userService.join(dto);
        return "success";
    }

}
