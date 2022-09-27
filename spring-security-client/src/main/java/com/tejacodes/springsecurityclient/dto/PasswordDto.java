package com.tejacodes.springsecurityclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDto {

    // used to get reset password link to email and also while changing password
    private String email;

    //used for resetting password
    private String password;
    private String confirmPassword;

    //used for changing passsword
    private String oldPassword;
    private String newPassword;
}
