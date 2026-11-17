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


    public void join(UserRequestDTO dto){

        String username = dto.username();
        String password = dto.password();

        UserEntity entity = new UserEntity();
        entity.setUsername(username);
        entity.setPassword(passwordEncoder.encode(password));
        entity.setRole(UserRole.USER);

        userRepository.save(entity);
    }
}
