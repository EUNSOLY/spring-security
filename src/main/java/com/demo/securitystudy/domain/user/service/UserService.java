package com.demo.securitystudy.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.securitystudy.domain.user.dto.UserRequestDTO;
import com.demo.securitystudy.domain.user.entity.UserEntity;
import com.demo.securitystudy.domain.user.entity.UserRole;
import com.demo.securitystudy.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // 회원가입 메소드
    public void join(UserRequestDTO dto){


        String username = dto.getUsername();
        String password = dto.getPassword();

        if(userRepository.existsByUsername(username)){
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(passwordEncoder.encode(password));
        userEntity.setRole(UserRole.USER);

        userRepository.save(userEntity);
    }
}
