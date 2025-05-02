package com.iuc.service;

import com.iuc.dto.request.RegisterRequest;
import com.iuc.entities.Role;
import com.iuc.entities.User;
import com.iuc.entities.VerificationToken;
import com.iuc.entities.enums.RoleType;
import com.iuc.exception.ConflictException;
import com.iuc.exception.ResourceNotFoundException;
import com.iuc.exception.message.ErrorMessage;
import com.iuc.repository.UserRepository;
import com.iuc.repository.VerificationTokenRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final EmailVerificationService emailVerificationService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    public UserService(UserRepository userRepository,RoleService roleService,@Lazy PasswordEncoder passwordEncoder,
                       EmailVerificationService emailVerificationService,EmailService emailService,VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.emailVerificationService = emailVerificationService;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }
    public User getUserByEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new ResourceNotFoundException(
                        String.format(ErrorMessage.USER_NOT_FOUND_EXCEPTION, email)));
        return user;

    }


    public void saveUser(RegisterRequest registerRequest) {
        //!!! DTO dan gelen email sistemde daha önce var mı ???
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw  new ConflictException(
                    String.format(ErrorMessage.EMAIL_ALREADY_EXIST_MESSAGE,
                            registerRequest.getEmail())
            );
        }

        // !!! yeni kullanıcın rol bilgisini default olarak customer atıyorum
        Role role = roleService.findByType(RoleType.ROLE_CUSTOMER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        //!!! Db ye gitmeden önce şifre encode edilecek
        String encodedPassword= passwordEncoder.encode(registerRequest.getPassword());

        //!!! yeni kullanıcının gerekli bilgilerini setleyip DB ye gönderiyoruz
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setAddress(registerRequest.getAddress());
        user.setZipCode(registerRequest.getZipCode());
        user.setRoles(roles);
        user.setEmailVerified(false); // Email henüz doğrulanmadı


        User savedUser = userRepository.save(user);
        // Şimdi VerificationToken'ı oluşturuyoruz
        String token = generateVerificationToken(savedUser);  // Token üretme fonksiyonunu eklemeniz gerekecek
        VerificationToken verificationToken = new VerificationToken(savedUser, token, Instant.now().plusSeconds(3600));

        verificationTokenRepository.save(verificationToken);


        // E-posta gönderme işlemi
        sendVerificationEmail(savedUser, token);  // Bu fonksiyonu daha önce yazdınız
    }

    private String generateVerificationToken(User user) {
        // Token üretme işlemi
        return UUID.randomUUID().toString();  // Burada UUID kullanarak basit bir token ürettik
    }

    private void sendVerificationEmail(User user, String token) {
        String verificationUrl = "http://localhost:8000/api/auth/verify?token=" + token;
        String subject = "Email Doğrulaması";
        String body = "Lütfen hesabınızı doğrulamak için aşağıdaki bağlantıyı tıklayın: \n" + verificationUrl;

        emailService.sendEmail(user.getEmail(), subject, body);
    }

}
