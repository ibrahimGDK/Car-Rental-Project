package com.iuc.controller;

import com.iuc.dto.request.LoginRequest;
import com.iuc.dto.request.RegisterRequest;
import com.iuc.dto.response.LoginResponse;
import com.iuc.dto.response.ResponseMessage;
import com.iuc.dto.response.SfResponse;
import com.iuc.security.Jwt.*;
import com.iuc.service.*;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.*;
//Bu classdaki metotlar özel önem isteyen metotlar. İlerde değişiklik yapmak gerekirse kolaylık sağlaması için normal userController classında yazmadık
@RestController
public class UserJwtController {
    // !!! Bu class'da sadece Login ve Register işlemleri yapılacak
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // !!! Register
    @PostMapping("/register")
    public ResponseEntity<SfResponse> registerUser(@Valid
                                                   @RequestBody RegisterRequest registerRequest) {
        userService.saveUser(registerRequest);

        SfResponse response = new SfResponse();
        response.setMessage(ResponseMessage.REGISTER_RESPONSE_MESSAGE);
        response.setSuccess(true);

        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    // !!! Login

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid
                                                      @RequestBody LoginRequest loginRequest) {
        //  authentication Manager' a göndermek için zarfladık
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                        loginRequest.getPassword());

        //authentication Manager --> kullanıcı valide edildi
        Authentication authentication =// görünürde yok ancak authentication Manager üzerinden security nin service katmanına gittik
                authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        // !!! Kullanıcı bu aşamada valide edildi ve Token üretimine geçiliyor
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();//getPrincipal(): Anlık olarak login olan kullanıcının bilgisini bize gönderir
        String jwtToken = jwtUtils.generateJwtToken(userDetails);
        // !!! JWT token client tarafına gönderiliyor
        LoginResponse loginResponse = new LoginResponse(jwtToken);


        return new ResponseEntity<>(loginResponse,HttpStatus.OK);

    }
}
