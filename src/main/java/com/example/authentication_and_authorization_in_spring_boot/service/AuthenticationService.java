package com.example.authentication_and_authorization_in_spring_boot.service;

import com.example.authentication_and_authorization_in_spring_boot.dal.UserDao;
import com.example.authentication_and_authorization_in_spring_boot.entity.concretes.User;
import com.example.authentication_and_authorization_in_spring_boot.entity.concretes.dto.AuthenticationRequest;
import com.example.authentication_and_authorization_in_spring_boot.entity.concretes.dto.AuthenticationResponse;
import com.example.authentication_and_authorization_in_spring_boot.entity.concretes.dto.RegisterRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userDao.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, false);

        addRefreshTokenToCookie(response, refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse login(AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userDao.findByEmail(request.getEmail())
                .orElseThrow();

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, request.isRememberMe());

        addRefreshTokenToCookie(response, refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh-token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        if(jwtService.extractClaim(refreshToken, claims -> (boolean) claims.get("rememberMe"))) {
            refreshTokenCookie.setMaxAge(
                    (int) (jwtService.refreshExpiration / 1000));
        }
        response.addCookie(refreshTokenCookie);
    }

    public AuthenticationResponse refresh(String refreshToken, HttpServletResponse response) {
        String userEmail = jwtService.extractUsername(refreshToken);

        if(userEmail != null ) {
            User user = this.userDao.findByEmail(userEmail)
                    .orElseThrow();

            if (jwtService.isTokenValid(refreshToken, user)) {
                String accessToken = jwtService.generateToken(user);

                addRefreshTokenToCookie(response, refreshToken);

                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            } else {
                throw new RuntimeException("Refresh token is not valid.");
            }
        } else{
            throw new UsernameNotFoundException("User not found");
        }

    }
}
