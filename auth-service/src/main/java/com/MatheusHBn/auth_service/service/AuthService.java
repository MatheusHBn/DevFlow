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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

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
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreatedAt(LocalDateTime.now());
        var savedUser = userRepository.save(user);

        return userMapper.toAuthResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {

        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(), request.password()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        var user = userRepository.findByUsername(Objects.requireNonNull(userDetails).getUsername()).orElseThrow();

        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                token);
    }
}
