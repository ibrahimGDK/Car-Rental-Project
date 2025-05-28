package com.iuc.test.service;

import com.iuc.service.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    void testSendEmail() {
        // Mock JavaMailSender
        JavaMailSender mailSender = mock(JavaMailSender.class);

        // EmailService'i gerçek yerine mockla gönderici ile oluştur
        EmailService emailService = new EmailService(mailSender);

        // Test verileri
        String toEmail = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // Metodu çağır
        emailService.sendEmail(toEmail, subject, body);

        // Argument yakalayıcı ile SimpleMailMessage yakala
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // mailSender.send() metodunun çağrıldığını ve parametresini yakala
        verify(mailSender, times(1)).send(messageCaptor.capture());

        // Yakalanan SimpleMailMessage objesini al
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        // Doğrulamalar
        assertEquals(toEmail, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }
}
