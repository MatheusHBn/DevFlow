package com.MatheusHBn.auth_service.api;

import com.MatheusHBn.auth_service.commons.FileUtils;
import com.MatheusHBn.auth_service.config.CustomAccessDeniedHandler;
import com.MatheusHBn.auth_service.config.CustomAuthenticationEntryPoint;
import com.MatheusHBn.auth_service.config.SecurityConfig;
import com.MatheusHBn.auth_service.dto.AuthResponse;
import com.MatheusHBn.auth_service.dto.LoginRequest;
import com.MatheusHBn.auth_service.dto.LoginResponse;
import com.MatheusHBn.auth_service.dto.RegisterRequest;
import com.MatheusHBn.auth_service.exception.EmailAlreadyExistsException;
import com.MatheusHBn.auth_service.exception.GlobalErrorHandler;
import com.MatheusHBn.auth_service.exception.UserAlreadyExistsException;
import com.MatheusHBn.auth_service.service.AuthService;
import com.MatheusHBn.auth_service.service.CustomUserDetailsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WebMvcTest(controllers = AuthController.class)
@Import({
        SecurityConfig.class,
        GlobalErrorHandler.class,
        FileUtils.class
})
class AuthControllerTest {

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService service;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private CustomAccessDeniedHandler accessDeniedHandler;


    @Test
    @Order(1)
    @DisplayName("Should return 201 Created when registration succeeds")
    void register_ReturnsCreated_WhenRegistrationSucceeds() throws Exception {
        var request = fileUtils.readResourceAsObject("register/register-request-200.json", RegisterRequest.class);
        var requestString = fileUtils.readResourceFile("register/register-request-200.json");

        var response = fileUtils.readResourceAsObject("register/auth-response-200.json", AuthResponse.class);

        when(service.register(request)).thenReturn(response);

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("matheus"))
                .andExpect(jsonPath("$.email").value("matheus@example.com"));

        verify(service).register(request);
    }

    @Test
    @Order(2)
    @DisplayName("Should return 200 OK and JWT when login succeeds")
    void login_ReturnsOkAndJwt_WhenLoginSucceeds() throws Exception {
        var request = fileUtils.readResourceAsObject("login/login-request-200.json", LoginRequest.class);
        var requestString = fileUtils.readResourceFile("login/login-request-200.json");

        var response = fileUtils.readResourceAsObject("login/login-response-200.json", LoginResponse.class);

        when(service.login(request)).thenReturn(response);

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("matheus"))
                .andExpect(jsonPath("$.email").value("matheus@example.com"))
                .andExpect(jsonPath("$.token").value("test.jwt.token"));

        verify(service).login(request);
    }

    @Test
    @Order(3)
    @DisplayName("Should return 400 Bad Request when registration fields are invalid")
    void register_ReturnsBadRequest_WhenFieldsAreInvalid() throws Exception {
        var requestString = fileUtils.readResourceFile("register/register-request-invalid-fields-400.json");

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isBadRequest());


        verifyNoInteractions(service);
    }

    @Test

    @Order(4)
    @DisplayName("Should return 400 Bad Request when login fields are blank")
    void login_ReturnsBadRequest_WhenFieldsAreBlank() throws Exception {
        var requestString = fileUtils.readResourceFile("login/login-request-empty-fields-400.json");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @Order(5)
    @DisplayName("Should return 409 Conflict when username already exists")
    void register_ReturnsConflict_WhenUsernameAlreadyExists() throws Exception {
        var request = fileUtils.readResourceAsObject("register/register-request-200.json", RegisterRequest.class);
        var requestString = fileUtils.readResourceFile("register/register-request-200.json");

        when(service.register(request)).thenThrow(new UserAlreadyExistsException("Username already exists"));

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    @Order(6)
    @DisplayName("Should return 409 Conflict when email already exists")
    void register_ReturnsConflict_WhenEmailAlreadyExists() throws Exception {
        var request = fileUtils.readResourceAsObject("register/register-request-200.json", RegisterRequest.class);
        var requestString = fileUtils.readResourceFile("register/register-request-200.json");

        when(service.register(request)).thenThrow(new EmailAlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

}