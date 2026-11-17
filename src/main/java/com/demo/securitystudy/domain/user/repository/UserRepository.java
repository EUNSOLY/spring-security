package com.demo.securitystudy.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.securitystudy.domain.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

     boolean existsByUsername(String username);
}
