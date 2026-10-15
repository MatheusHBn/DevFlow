package com.MatheusHBn.auth_service.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    private static final String SECRET = Base64.getEncoder().encodeToString(
            "0123456789abcdef0123456789abcdef".getBytes()
    );

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);

        userDetails = User.withUsername("matheus")
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Should generate a valid JWT token for the given user details")
    void generateToken_ReturnsValidToken_WhenUserDetailsAreProvided() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals("matheus", jwtService.extractUsername(token));
    }

    @Test
    @Order(2)
    @DisplayName("Should extract the correct username from a valid token")
    void extractUsername_ReturnsUsername_WhenTokenIsValid() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertEquals("matheus", username);
    }

    @Test
    @Order(3)
    @DisplayName("Should extract a future expiration date from a valid token")
    void extractExpiration_ReturnsFutureDate_WhenTokenIsValid() {
        String token = jwtService.generateToken(userDetails);

        Date expiration = jwtService.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    @Order(4)
    @DisplayName("Should return true when the token is valid and matches the user")
    void isTokenValid_ReturnsTrue_WhenTokenMatchesUserAndIsNotExpired() {
        String token = jwtService.generateToken(userDetails);

        boolean valid = jwtService.isTokenValid(token, userDetails);

        assertTrue(valid);
    }

    @Test
    @Order(5)
    @DisplayName("Should return false when the token's username does not match the user details")
    void isTokenValid_ReturnsFalse_WhenUsernameDoesNotMatch() {
        String token = jwtService.generateToken(userDetails);

        UserDetails anotherUser = User.withUsername("another-user")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        boolean valid = jwtService.isTokenValid(token, anotherUser);

        assertFalse(valid);
    }

    @Test
    @Order(6)
    @DisplayName("Should throw JwtException when attempting to read a tampered token")
    void extractUsername_ThrowsJwtException_WhenTokenIsTampered() {
        String token = jwtService.generateToken(userDetails);
        String tamperedToken = token.substring(0, token.length() - 1) + (token.endsWith("a") ? "b" : "a");

        assertThrows(JwtException.class, () -> jwtService.extractUsername(tamperedToken));
    }

    @Test
    @Order(7)
    @DisplayName("Should throw ExpiredJwtException when the token has expired")
    void extractUsername_ThrowsExpiredJwtException_WhenTokenIsExpired() {
        String expiredToken = jwtService.generateTokenInvalid(userDetails, new Date(System.currentTimeMillis() - 10_000));

        assertThrows(ExpiredJwtException.class, () -> jwtService.extractUsername(expiredToken));
    }
}