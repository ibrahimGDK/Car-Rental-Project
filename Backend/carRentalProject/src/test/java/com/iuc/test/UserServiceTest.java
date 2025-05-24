package com.iuc.test;

import com.iuc.dto.UserDTO;
import com.iuc.dto.request.RegisterRequest;
import com.iuc.entities.Role;
import com.iuc.entities.User;
import com.iuc.entities.VerificationToken;
import com.iuc.entities.enums.RoleType;
import com.iuc.exception.ConflictException;
import com.iuc.exception.ResourceNotFoundException;
import com.iuc.mapper.UserMapper;
import com.iuc.repository.UserRepository;
import com.iuc.repository.VerificationTokenRepository;
import com.iuc.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private EmailVerificationService emailVerificationService;
    @Mock
    private EmailService emailService;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserByEmail_WhenUserExists() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByEmail("test@example.com");

        assertEquals("test@example.com", foundUser.getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetUserByEmail_WhenUserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByEmail("notfound@example.com"));
    }

    @Test
    void testSaveUser_WhenEmailNotExists() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Ali");
        request.setLastName("Yılmaz");
        request.setEmail("ali@example.com");
        request.setPassword("12345");
        request.setPhoneNumber("123456");
        request.setAddress("Istanbul");
        request.setZipCode("34000");

        Role role = new Role();
        role.setType(RoleType.ROLE_CUSTOMER);

        when(userRepository.existsByEmail("ali@example.com")).thenReturn(false);
        when(roleService.findByType(RoleType.ROLE_CUSTOMER)).thenReturn(role);
        when(passwordEncoder.encode("12345")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        userService.saveUser(request);

        verify(userRepository).save(any(User.class));
        verify(verificationTokenRepository).save(any(VerificationToken.class));
        verify(emailService).sendEmail(eq("ali@example.com"), anyString(), contains("http://localhost"));
    }

    @Test
    void testSaveUser_WhenEmailExists_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("ali@example.com");
        when(userRepository.existsByEmail("ali@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.saveUser(request));
    }

    @Test
    void testGetAllUsers_ShouldReturnMappedUserDTOs() {
        List<User> users = Arrays.asList(new User(), new User());
        List<UserDTO> userDTOs = Arrays.asList(new UserDTO(), new UserDTO());

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.map(users)).thenReturn(userDTOs);

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
        verify(userMapper).map(users);
    }

    @Test
    void testGetUserById_WhenExists() {
        User user = new User();
        user.setId(1L);
        UserDTO userDTO = new UserDTO();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userMapper).userToUserDTO(user);
    }

    @Test
    void testGetUserById_WhenNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

}

