package com.jhpark.simple_chat_user.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jhpark.simple_chat_user.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    boolean existsByUsername(String userName);
    
}