package com.MatheusHBn.auth_service.api;

import com.MatheusHBn.auth_service.dto.AuthResponse;
import com.MatheusHBn.auth_service.dto.LoginRequest;
import com.MatheusHBn.auth_service.dto.LoginResponse;
import com.MatheusHBn.auth_service.dto.RegisterRequest;
import com.MatheusHBn.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        var response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}
