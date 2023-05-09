package com.example.authentication_and_authorization_in_spring_boot.dal;

import com.example.authentication_and_authorization_in_spring_boot.entity.concretes.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserDao  extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
