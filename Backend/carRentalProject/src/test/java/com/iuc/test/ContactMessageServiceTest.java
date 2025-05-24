package com.iuc.test;

import com.iuc.entities.ContactMessage;
import com.iuc.exception.ResourceNotFoundException;
import com.iuc.repository.ContactMessageRepository;
import com.iuc.service.ContactMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContactMessageServiceTest {

    @Mock
    private ContactMessageRepository contactMessageRepository;

    @InjectMocks
    private ContactMessageService contactMessageService;

    private ContactMessage sampleMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleMessage = new ContactMessage();
        sampleMessage.setName("John Doe");
        sampleMessage.setEmail("john@example.com");
        sampleMessage.setSubject("Inquiry");
        sampleMessage.setBody("I have a question.");

        // id özel olduğu için reflection ile set ediyoruz
        ReflectionTestUtils.setField(sampleMessage, "id", 1L);
    }

    @Test
    void saveMessage_ShouldCallRepositorySave() {
        contactMessageService.saveMessage(sampleMessage);
        verify(contactMessageRepository, times(1)).save(sampleMessage);
    }

    @Test
    void getAll_ShouldReturnListOfMessages() {
        List<ContactMessage> messages = List.of(sampleMessage);
        when(contactMessageRepository.findAll()).thenReturn(messages);

        List<ContactMessage> result = contactMessageService.getAll();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    void getAll_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ContactMessage> page = new PageImpl<>(List.of(sampleMessage));

        when(contactMessageRepository.findAll(pageable)).thenReturn(page);

        Page<ContactMessage> result = contactMessageService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getContactMessage_ValidId_ShouldReturnMessage() {
        when(contactMessageRepository.findById(1L)).thenReturn(Optional.of(sampleMessage));

        ContactMessage result = contactMessageService.getContactMessage(1L);

        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void getContactMessage_InvalidId_ShouldThrowException() {
        when(contactMessageRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            contactMessageService.getContactMessage(2L);
        });
    }

    @Test
    void deleteContactMessage_ValidId_ShouldCallDelete() {
        when(contactMessageRepository.findById(1L)).thenReturn(Optional.of(sampleMessage));

        contactMessageService.deleteContactMessage(1L);

        verify(contactMessageRepository, times(1)).delete(sampleMessage);
    }

    @Test
    void updateContactMessage_ShouldUpdateAndSave() {
        ContactMessage updatedMessage = new ContactMessage();
        updatedMessage.setName("Jane Doe");
        updatedMessage.setEmail("jane@example.com");
        updatedMessage.setSubject("Support");
        updatedMessage.setBody("Help needed");

        when(contactMessageRepository.findById(1L)).thenReturn(Optional.of(sampleMessage));

        contactMessageService.updateContactMessage(1L, updatedMessage);

        assertEquals("Jane Doe", sampleMessage.getName());
        assertEquals("jane@example.com", sampleMessage.getEmail());
        assertEquals("Support", sampleMessage.getSubject());
        assertEquals("Help needed", sampleMessage.getBody());

        verify(contactMessageRepository).save(sampleMessage);
    }
}

