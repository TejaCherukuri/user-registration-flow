package com.tejacodes.springsecurityclient.event.listener;

import com.tejacodes.springsecurityclient.entity.User;
import com.tejacodes.springsecurityclient.event.RegistrationCompleteEvent;
import com.tejacodes.springsecurityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        // Create the verification token for the user with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(user, token);

        // Send verification mail to the user
        String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;
        log.info("Click here to verify your email --> " +url);
        // Resend link if the token is expired
        String newUrl = event.getApplicationUrl() + "/resendVerifyToken?token=" + token;
        log.info("Click here to receive new verification email link --> " +newUrl);
    }
}
