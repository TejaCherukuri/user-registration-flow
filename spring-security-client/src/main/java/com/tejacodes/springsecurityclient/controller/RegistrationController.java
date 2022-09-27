package com.tejacodes.springsecurityclient.controller;

import com.tejacodes.springsecurityclient.dto.PasswordDto;
import com.tejacodes.springsecurityclient.dto.UserDto;
import com.tejacodes.springsecurityclient.entity.User;
import com.tejacodes.springsecurityclient.entity.VerificationToken;
import com.tejacodes.springsecurityclient.event.RegistrationCompleteEvent;
import com.tejacodes.springsecurityclient.exception.PasswordMismatchException;
import com.tejacodes.springsecurityclient.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserDto userDto, HttpServletRequest request)
            throws PasswordMismatchException {

        User user = userService.registerUser(userDto);
        publisher.publishEvent(new RegistrationCompleteEvent(user,applicationUrl(request)));
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String validateVerificationToken(@RequestParam("token") String token) {

        String result = userService.validateVerificationToken(token);

        if(result.equalsIgnoreCase("valid"))
            return "User is Verified";
        else
            return "Bad User";
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request) {

        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);

        User user = verificationToken.getUser();
        String token = verificationToken.getToken();
        userService.resendVerificationEmail(user, token, applicationUrl(request));

        return "Verification email resent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordDto passwordDto, HttpServletRequest request) {

        User user = userService.findUserByEmail(passwordDto.getEmail());
        if(user != null)
        {
            String token = UUID.randomUUID().toString();
            userService.savePasswordResetTokenForUser(user, token);
            userService.sendPasswordResetEmail(user, token, applicationUrl(request));
            return "Reset Password link sent to email";
        }
        return "Bad User email";
    }

    @PostMapping("/savePassword")
    public String saveNewPassword(@RequestParam("token") String token, @RequestBody PasswordDto passwordDto)
            throws PasswordMismatchException {

        String result = userService.validatePasswordResetToken(token);
        if(result.equalsIgnoreCase("valid")) {
            userService.saveNewPassword(token, passwordDto);
            return "Password Reset Successful";
        }
        else
            return "Bad User Token";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordDto passwordDto)
    {
        User user = userService.findUserByEmail(passwordDto.getEmail());
        if(user != null)
        {
           if(userService.checkIfOldPasswordIsValid(user, passwordDto.getOldPassword())) {
               userService.changePassword(user, passwordDto.getNewPassword());
               return "Password change successful";
           }
           else
               return "Invalid Old Password";
        }
        else
            return "Invalid User";
    }

    private String applicationUrl(HttpServletRequest request) {

        String applicationUrl = "http://" + request.getServerName() + ":" +
                request.getServerPort() + request.getContextPath();
        return applicationUrl;
    }
}
