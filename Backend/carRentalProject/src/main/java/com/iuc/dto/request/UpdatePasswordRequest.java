package com.iuc.dto.request;

import lombok.*;

import jakarta.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {

    @NotBlank(message="Please Provide Old Password")
    private String oldPassword;

    @NotBlank(message="Please Provide New Password")
    private String newPassword;
}
