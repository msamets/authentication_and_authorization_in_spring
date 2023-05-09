package com.example.authentication_and_authorization_in_spring_boot.controller;

import com.example.authentication_and_authorization_in_spring_boot.entity.concretes.dto.AuthenticationRequest;
import com.example.authentication_and_authorization_in_spring_boot.entity.concretes.dto.AuthenticationResponse;
import com.example.authentication_and_authorization_in_spring_boot.entity.concretes.dto.RegisterRequest;
import com.example.authentication_and_authorization_in_spring_boot.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authenticationService.login(request, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @CookieValue(name = "refresh-token") String refreshToken,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authenticationService.refresh(refreshToken, response));
    }
}
