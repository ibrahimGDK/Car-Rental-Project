package com.iuc.test.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iuc.controller.ContactMessageController;
import com.iuc.dto.ContactMessageDTO;
import com.iuc.dto.request.ContactMessageRequest;
import com.iuc.entities.ContactMessage;
import com.iuc.mapper.ContactMessageMapper;
import com.iuc.service.ContactMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.lang.reflect.Field;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactMessageController.class)
public class ContactMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactMessageService contactMessageService;

    @MockBean
    private ContactMessageMapper contactMessageMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private ContactMessage contactMessage;
    private ContactMessageDTO contactMessageDTO;
    private ContactMessageRequest contactMessageRequest;



    @BeforeEach
    public void setup() throws Exception {
        contactMessage = new ContactMessage();
        setId(contactMessage, 1L);
        contactMessage.setName("John");
        contactMessage.setSubject("Hello");
        contactMessage.setBody("Test body");
        contactMessage.setEmail("john@example.com");

        contactMessageDTO = new ContactMessageDTO(1L, "John", "Hello", "Test body", "john@example.com");

        contactMessageRequest = new ContactMessageRequest("John", "Hello", "Test body", "john@example.com");
    }

    private void setId(ContactMessage obj, Long id) throws Exception {
        Field field = ContactMessage.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(obj, id);
    }



    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllContactMessages() throws Exception {
        when(contactMessageService.getAll()).thenReturn(List.of(contactMessage));
        when(contactMessageMapper.map(any())).thenReturn(List.of(contactMessageDTO));

        mockMvc.perform(get("/contactmessage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetMessageWithPath() throws Exception {
        when(contactMessageService.getContactMessage(1L)).thenReturn(contactMessage);
        when(contactMessageMapper.contactMessageToDTO(contactMessage)).thenReturn(contactMessageDTO);

        mockMvc.perform(get("/contactmessage/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }



}
