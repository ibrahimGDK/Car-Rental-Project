package com.iuc.service;

import com.iuc.dto.UserDTO;
import com.iuc.dto.request.AdminUserUpdateRequest;
import com.iuc.dto.request.RegisterRequest;
import com.iuc.dto.request.UpdatePasswordRequest;
import com.iuc.dto.request.UserUpdateRequest;
import com.iuc.entities.Role;
import com.iuc.entities.User;
import com.iuc.entities.VerificationToken;
import com.iuc.entities.enums.RoleType;
import com.iuc.exception.BadRequestException;
import com.iuc.exception.ConflictException;
import com.iuc.exception.ResourceNotFoundException;
import com.iuc.exception.message.ErrorMessage;
import com.iuc.mapper.UserMapper;
import com.iuc.repository.UserRepository;
import com.iuc.repository.VerificationTokenRepository;
import com.iuc.security.SecurityUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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
    private final UserMapper userMapper;
    private final ReservationService reservationService;
    public UserService(UserRepository userRepository,RoleService roleService,@Lazy PasswordEncoder passwordEncoder,
                       EmailVerificationService emailVerificationService,EmailService emailService,VerificationTokenRepository verificationTokenRepository,
                       UserMapper userMapper,ReservationService reservationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.emailVerificationService = emailVerificationService;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userMapper = userMapper;
        this.reservationService = reservationService;

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

    public List<UserDTO> getAllUsers() {
        List<User> users =  userRepository.findAll();
        List<UserDTO> userDTOs = userMapper.map(users);
        return userDTOs;
    }

    public UserDTO getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(
                        String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION, id)));

        return userMapper.userToUserDTO(user);
    }

    //UPDATE PASSWORD
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest) {

        User user = getCurrentUser();

        // !!! builtIn ???
        if(user.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
        // !!! Forma girilen OldPassword doğru mu
        if(!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorMessage.PASSWORD_NOT_MATCHED_MESSAGE);
        }//matches : yeni şifreyi encode edip eski enceded şifre ile karşılaştırır

        // !!! yeni gelen şifreyi encode edilecek
        String hashedPassword =passwordEncoder.encode(updatePasswordRequest.getNewPassword());
        user.setPassword(hashedPassword);

        userRepository.save(user);
    }

    public User getCurrentUser(){
        String email =  SecurityUtils.getCurrentUserLogin().orElseThrow(()->
                new ResourceNotFoundException(ErrorMessage.PRINCIPAL_FOUND_MESSAGE));
        User user =  getUserByEmail(email);

        return user;

    }
    @Transactional
//Arka arkaya birkaç query çalışacaksa bunu koymalıyız. mesela ilk 3 query çalıştı ancak 4. çalışmazsa ilk üçünüde iptal eder
    public void updateUser(UserUpdateRequest userUpdateRequest) {
        User user = getCurrentUser();
        // !!! builtIn ???
        if(user.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
        // !!! email kontrol
        boolean emailExist = userRepository.existsByEmail(userUpdateRequest.getEmail());
        if(emailExist && !userUpdateRequest.getEmail().equals(user.getEmail())) {
            throw new ConflictException(
                    String.format(ErrorMessage.EMAIL_ALREADY_EXIST_MESSAGE,userUpdateRequest.getEmail()));
        }

        userRepository.update(user.getId(),
                userUpdateRequest.getFirstName(),
                userUpdateRequest.getLastName(),
                userUpdateRequest.getPhoneNumber(),
                userUpdateRequest.getEmail(),
                userUpdateRequest.getAddress(),
                userUpdateRequest.getZipCode());
    }


    public void updateUserAuth(Long id, AdminUserUpdateRequest adminUserUpdateRequest) {
        User user = getById(id);
        //!!!built-in
        if(user.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
        //!!! email kontrolu
        boolean emailExist = userRepository.existsByEmail(adminUserUpdateRequest.getEmail());
        if(emailExist && !adminUserUpdateRequest.getEmail().equals(user.getEmail())) {
            throw new ConflictException(
                    String.format(ErrorMessage.EMAIL_ALREADY_EXIST_MESSAGE,adminUserUpdateRequest.getEmail()));
        }
        //!!! passsword kontrol
        if(adminUserUpdateRequest.getPassword()==null) {
            adminUserUpdateRequest.setPassword(user.getPassword());
        } else {
            String encodedPassword =
                    passwordEncoder.encode(adminUserUpdateRequest.getPassword());
            adminUserUpdateRequest.setPassword(encodedPassword);
        }
        //!!! Role
        Set<String> userStrRoles = adminUserUpdateRequest.getRoles();//Customer ya da Administrator değerini string olarak userStrRoles'e atadım

        Set<Role> roles = convertRoles(userStrRoles);//String yapısında ki rolleri DB'de kayıtlı olduğu gibi ROLE_ADMIN ROLE_CUSTOMER şekline çevirdik

        user.setFirstName(adminUserUpdateRequest.getFirstName());
        user.setLastName(adminUserUpdateRequest.getLastName());
        user.setEmail(adminUserUpdateRequest.getEmail());
        user.setPassword(adminUserUpdateRequest.getPassword());
        user.setPhoneNumber(adminUserUpdateRequest.getPhoneNumber());
        user.setAddress(adminUserUpdateRequest.getAddress());
        user.setZipCode(adminUserUpdateRequest.getZipCode());
        user.setBuiltIn(adminUserUpdateRequest.getBuiltIn());
        user.setRoles(roles);

        userRepository.save(user);
    }
    public User getById(Long id){
        User user = userRepository.findUserById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_EXCEPTION)));
        return user;
    }

    private Set<Role> convertRoles(Set<String> pRoles){ // pRoles={"Customer","Administrator"}
        Set<Role> roles = new HashSet<>();

        if(pRoles==null){//client tarafından roller boş gelirse ROLE_CUSTOMER olarak default değer girer
            Role userRole = roleService.findByType(RoleType.ROLE_CUSTOMER);// Entity ler arası işlem yapıyorsam best practice  service.den service'e
            roles.add(userRole);
        } else {
            pRoles.forEach(roleStr->{
                if(roleStr.equals(RoleType.ROLE_ADMIN.getName())){
                    Role adminRole = roleService.findByType(RoleType.ROLE_ADMIN);
                    roles.add(adminRole);
                } else {
                    Role userRole = roleService.findByType(RoleType.ROLE_CUSTOMER);
                    roles.add(userRole);
                }
            });
        }
        return roles;
    }
    public void removeUserById(Long id) {
        User user = getById(id);

        //!!!built-in
        if(user.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
// !!! reservasyon kontrol
        boolean exist =  reservationService.existByUser(user);
        if(exist) {
            throw  new BadRequestException(ErrorMessage.USER_CANT_BE_DELETED_MESSAGE);
        }
        userRepository.deleteById(id);

    }


}
