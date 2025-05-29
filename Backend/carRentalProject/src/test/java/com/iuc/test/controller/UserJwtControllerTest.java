package com.iuc.test.controller;

import com.iuc.controller.UserJwtController;
import com.iuc.dto.request.LoginRequest;
import com.iuc.dto.request.RegisterRequest;
import com.iuc.dto.response.LoginResponse;
import com.iuc.dto.response.ResponseMessage;
import com.iuc.dto.response.SfResponse;
import com.iuc.security.Jwt.JwtUtils;
import com.iuc.service.UserService;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserJwtControllerTest {

    @Autowired
    private UserJwtController userJwtController;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test: registerUser()
    @Test
    void testRegisterUser() {
        RegisterRequest registerRequest = new RegisterRequest();
        doNothing().when(userService).saveUser(registerRequest);

        ResponseEntity<SfResponse> response = userJwtController.registerUser(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(ResponseMessage.REGISTER_RESPONSE_MESSAGE, response.getBody().getMessage());
        verify(userService, times(1)).saveUser(registerRequest);
    }

    // ✅ Test: authenticate()
    @Test
    void testAuthenticate() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        // Mock authentication
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        UserDetails mockUserDetails =
                new User("test@example.com", "password", Collections.emptyList());

        when(authenticationManager.authenticate(token)).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        when(jwtUtils.generateJwtToken(mockUserDetails)).thenReturn("mock-jwt-token");

        ResponseEntity<LoginResponse> response = userJwtController.authenticate(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("mock-jwt-token", response.getBody().getToken());

        verify(authenticationManager, times(1)).authenticate(token);
        verify(jwtUtils, times(1)).generateJwtToken(mockUserDetails);
    }
}

