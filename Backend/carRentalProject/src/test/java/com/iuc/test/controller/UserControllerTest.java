package com.iuc.test.controller;

import com.iuc.controller.UserController;
import com.iuc.dto.UserDTO;
import com.iuc.dto.request.AdminUserUpdateRequest;
import com.iuc.dto.request.UpdatePasswordRequest;
import com.iuc.dto.request.UserUpdateRequest;
import com.iuc.dto.response.ResponseMessage;
import com.iuc.dto.response.SfResponse;
import com.iuc.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() {
        List<UserDTO> userList = Arrays.asList(new UserDTO(), new UserDTO());
        when(userService.getAllUsers()).thenReturn(userList);

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById() {
        UserDTO mockUser = new UserDTO();
        when(userService.getUserById(1L)).thenReturn(mockUser);

        ResponseEntity<UserDTO> response = userController.getUserById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUser, response.getBody());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePassword() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();

        ResponseEntity<SfResponse> response = userController.updatePassword(request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(ResponseMessage.PASSWORD_CHANGED_RESPONSE_MESSAGE, response.getBody().getMessage());
        verify(userService, times(1)).updatePassword(request);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser() {
        UserUpdateRequest request = new UserUpdateRequest();

        ResponseEntity<SfResponse> response = userController.updateUser(request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(ResponseMessage.USER_UPDATE_RESPONSE_MESSAGE, response.getBody().getMessage());
        verify(userService, times(1)).updateUser(request);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUserAuth() {
        AdminUserUpdateRequest request = new AdminUserUpdateRequest();

        ResponseEntity<SfResponse> response = userController.updateUserAuth(5L, request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(ResponseMessage.USER_UPDATE_RESPONSE_MESSAGE, response.getBody().getMessage());
        verify(userService, times(1)).updateUserAuth(5L, request);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser() {
        ResponseEntity<SfResponse> response = userController.deleteUser(2L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(ResponseMessage.USER_DELETE_RESPONSE_MESSAGE, response.getBody().getMessage());
        verify(userService, times(1)).removeUserById(2L);
    }
}

