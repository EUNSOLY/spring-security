package com.demo.securitystudy.domain.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.securitystudy.domain.jwt.entity.RefreshEntity;

public interface RefreshRepository extends JpaRepository<RefreshEntity,Long> {

}
