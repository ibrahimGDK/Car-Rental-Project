package com.iuc.test.service;

import com.iuc.entities.User;
import com.iuc.entities.VerificationToken;
import com.iuc.repository.UserRepository;
import com.iuc.repository.VerificationTokenRepository;
import com.iuc.service.EmailService;
import com.iuc.service.EmailVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmailVerificationServiceTest {

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<VerificationToken> tokenCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendVerificationEmail() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        // Act
        emailVerificationService.sendVerificationEmail(user);

        // Assert
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());
        verify(emailService, times(1)).sendEmail(eq("test@example.com"), anyString(), contains("http://localhost:8000/api/auth/verify?token="));

        VerificationToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken);
        assertEquals(user, savedToken.getUser());
    }

    @Test
    void testVerifyToken_validToken_shouldReturnTrue() {
        // Arrange
        String tokenStr = "valid-token";
        User user = new User();
        user.setEmailVerified(false);

        VerificationToken token = new VerificationToken(user);
        token.setToken(tokenStr);

        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));

        // Act
        boolean result = emailVerificationService.verifyToken(tokenStr);

        // Assert
        assertTrue(result);
        assertTrue(user.getEmailVerified());

        verify(userRepository, times(1)).save(user);
        verify(tokenRepository, times(1)).delete(token);
    }

    @Test
    void testVerifyToken_invalidToken_shouldReturnFalse() {
        // Arrange
        String tokenStr = "invalid-token";
        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.empty());

        // Act
        boolean result = emailVerificationService.verifyToken(tokenStr);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }
}

