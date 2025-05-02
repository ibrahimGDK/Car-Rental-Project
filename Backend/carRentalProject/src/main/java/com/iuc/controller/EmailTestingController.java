package com.iuc.controller;

import com.iuc.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailTestingController {

    private final EmailService emailService;

    public EmailTestingController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send-test-email")
    public String sendTestEmail(@RequestParam String to) {
        String subject = "Email Doğrulaması";
        String body = "Lütfen hesabınızı doğrulamak için aşağıdaki bağlantıyı tıklayın:";
        emailService.sendEmail(to, subject, body);
        return "E-posta gönderildi: " + to;
    }



}
