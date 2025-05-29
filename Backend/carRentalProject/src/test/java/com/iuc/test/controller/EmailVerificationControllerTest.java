package com.iuc.test.controller;


import com.iuc.controller.EmailVerificationController;
import com.iuc.service.EmailVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)// Testlerin geçmesi için security katmanını devre dışı bıraktık
public class EmailVerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailVerificationService verificationService;

    private final String VALID_TOKEN = "validToken";
    private final String INVALID_TOKEN = "invalidToken";

    @BeforeEach
    void setUp() {
        when(verificationService.verifyToken(VALID_TOKEN)).thenReturn(true);
        when(verificationService.verifyToken(INVALID_TOKEN)).thenReturn(false);
    }

    @Test
    void shouldReturn200WhenTokenIsValid() throws Exception {
        mockMvc.perform(get("/api/auth/verify")
                        .param("token", VALID_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string("Email başarıyla doğrulandı."));
    }

    @Test
    void shouldReturn400WhenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/api/auth/verify")
                        .param("token", INVALID_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Geçersiz ya da süresi dolmuş token."));
    }
}
