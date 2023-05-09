package com.example.authentication_and_authorization_in_spring_boot.entity.concretes.dto;

import com.example.authentication_and_authorization_in_spring_boot.entity.concretes.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}
