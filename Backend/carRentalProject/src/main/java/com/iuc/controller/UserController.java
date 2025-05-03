package com.iuc.controller;

import com.iuc.dto.UserDTO;
import com.iuc.dto.request.AdminUserUpdateRequest;
import com.iuc.dto.request.UpdatePasswordRequest;
import com.iuc.dto.request.UserUpdateRequest;
import com.iuc.dto.response.ResponseMessage;
import com.iuc.dto.response.SfResponse;
import com.iuc.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // !!! getAllUser
    @GetMapping("/auth/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> allUsers = userService.getAllUsers();

        return ResponseEntity.ok(allUsers);
    }

    // !!! GetUserById
    @GetMapping("/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    // !!! Update Password
    @PatchMapping("/auth")//putmapping yapsaydık kullanıcının diğer bilgileri nullanırdı. bu yüzden parçalı anlamına gelen @PatchMapping yaptık
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<SfResponse> updatePassword(@Valid
                                                     @RequestBody UpdatePasswordRequest updatePasswordRequest){//clientdan eski ve yeni şifre almak için oluşturduğumuzdto class
        userService.updatePassword(updatePasswordRequest);

        SfResponse response = new SfResponse();
        response.setMessage(ResponseMessage.PASSWORD_CHANGED_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);

    }

    // !!! UpdateUser (Giriş yapmış kullanıcı kendi profil bilgilerini günceller)
    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<SfResponse> updateUser(
            @Valid @RequestBody UserUpdateRequest userUpdateRequest) {//ön taraftan gelecek olan JSON dosyasında id, role, password gibi bilgilerin update edilememesi gerekir. Bu yüzden yeni bir DTO class oluşturduk
        userService.updateUser(userUpdateRequest);

        SfResponse response = new SfResponse();
        response.setMessage(ResponseMessage.USER_UPDATE_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);

    }

    //!!! Admin herhangi bir kulllanıcıyı update etsin
    @PutMapping("/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> updateUserAuth(@PathVariable Long id,
                                                     @Valid @RequestBody AdminUserUpdateRequest adminUserUpdateRequest) {
        userService.updateUserAuth(id, adminUserUpdateRequest);

        SfResponse response = new SfResponse();
        response.setMessage(ResponseMessage.USER_UPDATE_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);
    }

    // !!! delete user
    @DeleteMapping("/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SfResponse> deleteUser(@PathVariable Long id){
        userService.removeUserById(id);

        SfResponse response = new SfResponse();
        response.setMessage(ResponseMessage.USER_DELETE_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return ResponseEntity.ok(response);
    }

}
