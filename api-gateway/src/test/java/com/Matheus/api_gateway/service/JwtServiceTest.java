package com.Matheus.api_gateway.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtServiceTest {

    private JwtService jwtService;
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        signingKey = Jwts.SIG.HS256.key().build();
        String secret = Encoders.BASE64.encode(signingKey.getEncoded());

        ReflectionTestUtils.setField(jwtService, "secret", secret);
    }

    private String generateToken(String subject, String role, Date expiration, SecretKey key) {

        var builder = Jwts.builder()
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis() - 1000))
                .expiration(expiration);

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.signWith(key, Jwts.SIG.HS256).compact();
    }

    @Test
    @Order(1)
    @DisplayName("Should return true when token has a valid subject and expiration")
    void isTokenValid_ReturnsTrue_WhenTokenIsValid() {
        String token = generateToken(
                "matheus",
                "ROLE_USER",
                new Date(System.currentTimeMillis() + 60_000),
                signingKey);

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    @Order(2)
    @DisplayName("Should return false when token is expired")
    void isTokenValid_ReturnsFalse_WhenTokenIsExpired() {
        String token = generateToken(
                "matheus",
                "ROLE_USER",
                new Date(System.currentTimeMillis() - 60_000),
                signingKey);

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    @Order(3)
    @DisplayName("Should return false when token is malformed")
    void isTokenValid_ReturnsFalse_WhenTokenIsMalformed() {
        assertFalse(jwtService.isTokenValid("token-invalido"));
    }

    @Test
    @Order(4)
    @DisplayName("Should return false when token is signed with a different key")
    void isTokenValid_ReturnsFalse_WhenTokenSignedWithDifferentKey() {
        SecretKey anotherKey = Jwts.SIG.HS256.key().build();

        String token = generateToken(
                "matheus",
                "ROLE_USER",
                new Date(System.currentTimeMillis() + 60_000),
                anotherKey);

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    @Order(5)
    @DisplayName("Should extract the correct role from a valid token")
    void extractRole_ReturnsRole_WhenTokenHasRoleClaim() {
        String token = generateToken(
                "matheus",
                "ROLE_ADMIN",
                new Date(System.currentTimeMillis() + 60_000),
                signingKey);

        assertEquals("ROLE_ADMIN", jwtService.extractRole(token));
    }

    @Test
    @Order(6)
    @DisplayName("Should return null when the token does not contain a role claim")
    void extractRole_ReturnsNull_WhenTokenHasNoRoleClaim() {
        String token = generateToken(
                "matheus",
                null,
                new Date(System.currentTimeMillis() + 60_000),
                signingKey);

        assertNull(jwtService.extractRole(token));
    }
}