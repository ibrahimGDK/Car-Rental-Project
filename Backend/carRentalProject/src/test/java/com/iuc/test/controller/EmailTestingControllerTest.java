package com.iuc.test.controller;



import com.iuc.controller.EmailTestingController;
import com.iuc.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailTestingController.class)
public class EmailTestingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void testSendTestEmail() throws Exception {
        mockMvc.perform(get("/api/email/send-test-email")
                        .param("to", "test@example.com"))
                .andExpect(status().isOk());
    }

}
