package com.MatheusHBn.auth_service.service;

import com.MatheusHBn.auth_service.domain.User;
import com.MatheusHBn.auth_service.dto.AuthResponse;
import com.MatheusHBn.auth_service.dto.LoginRequest;
import com.MatheusHBn.auth_service.dto.LoginResponse;
import com.MatheusHBn.auth_service.dto.RegisterRequest;
import com.MatheusHBn.auth_service.exception.EmailAlreadyExistsException;
import com.MatheusHBn.auth_service.exception.UserAlreadyExistsException;
import com.MatheusHBn.auth_service.mapper.UserMapper;
import com.MatheusHBn.auth_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            log.warn("Registration failed: reason=username_already_exists");
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration failed: reason=email_already_exists");
            throw new EmailAlreadyExistsException("Email already exists");
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreatedAt(LocalDateTime.now());
        var savedUser = userRepository.save(user);

        log.info("Registration successful: userId={}", savedUser.getId());

        return userMapper.toAuthResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication;

        try {
             authentication = authenticationManager.authenticate
                     (new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (AuthenticationException exception) {
            log.warn("Authentication failed: reason=invalid_credentials");
            throw exception;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByUsername(Objects.requireNonNull(userDetails).getUsername()).orElseThrow();
        String token = jwtService.generateToken(userDetails);

        log.info("Authentication successful: userId={}", user.getId());

        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(), token);
    }
}
