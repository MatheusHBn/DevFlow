package com.MatheusHBn.auth_service.service;

import com.MatheusHBn.auth_service.domain.User;
import com.MatheusHBn.auth_service.dto.AuthResponse;
import com.MatheusHBn.auth_service.dto.LoginRequest;
import com.MatheusHBn.auth_service.dto.RegisterRequest;
import com.MatheusHBn.auth_service.exception.EmailAlreadyExistsException;
import com.MatheusHBn.auth_service.exception.UserAlreadyExistsException;
import com.MatheusHBn.auth_service.mapper.UserMapper;
import com.MatheusHBn.auth_service.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService service;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "matheus",
                "matheus@example.com",
                "password123");

        user = User.builder()
                .id(1L)
                .username("matheus")
                .email("matheus@example.com")
                .password("encodedPassword")
                .role("USER")
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Should register a user and return AuthResponse when successful")
    void register_ReturnsAuthResponse_WhenRequestIsValid() {
        AuthResponse expectedResponse = new AuthResponse(1L, "matheus", "matheus@example.com");

        when(userRepository.existsByUsername("matheus")).thenReturn(false);
        when(userRepository.existsByEmail("matheus@example.com")).thenReturn(false);
        when(userMapper.toEntity(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toAuthResponse(user)).thenReturn(expectedResponse);

        var result = service.register(registerRequest);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        assertEquals("encodedPassword", user.getPassword());
        assertNotNull(user.getCreatedAt());

        verify(userRepository).existsByUsername("matheus");
        verify(userRepository).existsByEmail("matheus@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(user);
        verify(userMapper).toAuthResponse(user);

        verifyNoInteractions(authenticationManager, jwtService);
    }

    @Test
    @Order(2)
    @DisplayName("Should throw UserAlreadyExistsException when username is already taken")
    void register_ThrowsUserAlreadyExistsException_WhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("matheus")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> service.register(registerRequest));

        verify(userRepository).existsByUsername("matheus");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(userMapper, passwordEncoder, jwtService);
    }

    @Test
    @Order(3)
    @DisplayName("Should throw EmailAlreadyExistsException when email is already taken")
    void register_ThrowsEmailAlreadyExistsException_WhenEmailAlreadyExists() {
        when(userRepository.existsByUsername("matheus")).thenReturn(false);
        when(userRepository.existsByEmail("matheus@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> service.register(registerRequest));

        verify(userRepository).existsByUsername("matheus");
        verify(userRepository).existsByEmail("matheus@example.com");
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(userMapper, passwordEncoder, jwtService);
    }

    @Test
    @Order(4)
    @DisplayName("Should authenticate user and return JWT token when credentials are valid")
    void login_ReturnsLoginResponseWithJwt_WhenCredentialsAreValid() {
        var request = new LoginRequest("matheus", "password123");

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername("matheus")
                .password("encodedPassword")
                .authorities("ROLE_USER")
                .build();

        var authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByUsername("matheus")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        var result = service.login(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("matheus", result.username());
        assertEquals("matheus@example.com", result.email());
        assertEquals("jwt-token", result.token());

        verify(authenticationManager).authenticate(argThat(
                token -> token.getPrincipal().equals("matheus") && token.getCredentials().equals("password123")));
        verify(userRepository).findByUsername("matheus");
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    @Order(5)
    @DisplayName("Should throw BadCredentialsException and not generate JWT when authentication fails")
    void login_ThrowsBadCredentialsException_WhenCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest("matheus", "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> service.login(request));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtService);
        verify(userRepository, never()).findByUsername(anyString());
    }
}