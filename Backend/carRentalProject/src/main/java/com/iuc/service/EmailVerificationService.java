package com.iuc.service;

import com.iuc.entities.User;
import com.iuc.entities.VerificationToken;
import com.iuc.repository.UserRepository;
import com.iuc.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailVerificationService {

    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public EmailVerificationService(VerificationTokenRepository tokenRepository, EmailService emailService,
                                    UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void sendVerificationEmail(User user) {
        VerificationToken token = new VerificationToken(user);
        tokenRepository.save(token);

        String verificationUrl = "http://localhost:8000/api/auth/verify?token=" + token.getToken();
        String subject = "Email Doğrulama";
        String body = "Lütfen emailinizi doğrulamak için linke tıklayın:\n" + verificationUrl;

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    public boolean verifyToken(String tokenStr) {
        return tokenRepository.findByToken(tokenStr)
                .map(token -> {
                    User user = token.getUser();
                    user.setEmailVerified(true);
                    userRepository.save(user);        // <--- GÜNCELLEME BURADA
                    tokenRepository.delete(token);
                    return true;
                }).orElse(false);
    }

}
