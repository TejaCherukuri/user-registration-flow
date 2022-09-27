package com.tejacodes.springsecurityclient.service.impl;

import com.tejacodes.springsecurityclient.dto.PasswordDto;
import com.tejacodes.springsecurityclient.dto.UserDto;
import com.tejacodes.springsecurityclient.entity.PasswordResetToken;
import com.tejacodes.springsecurityclient.entity.User;
import com.tejacodes.springsecurityclient.entity.VerificationToken;
import com.tejacodes.springsecurityclient.exception.PasswordMismatchException;
import com.tejacodes.springsecurityclient.repository.PasswordResetTokenRepository;
import com.tejacodes.springsecurityclient.repository.UserRepository;
import com.tejacodes.springsecurityclient.repository.VerificationTokenRepository;
import com.tejacodes.springsecurityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public User registerUser(UserDto userDto) throws PasswordMismatchException{

        if(userDto.getPassword() != null && userDto.getPassword().equals(userDto.getConfirmPassword())) {
            User user = User.builder()
                    .email(userDto.getEmail())
                    .firstName(userDto.getFirstName())
                    .lastName(userDto.getLastName())
                    .password(passwordEncoder.encode(userDto.getPassword()))
                    .build();
            userRepository.save(user);
            return user;
        }
        else
            throw new PasswordMismatchException("Your password and confirmPassword didn't match.");
    }

    @Override
    public void saveVerificationTokenForUser(User user, String token) {

        VerificationToken verificationToken = new VerificationToken(token,user);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if(verificationToken == null)
            return "invalid";

        Calendar calendar = Calendar.getInstance();
        if(verificationToken.getExpirationTime().getTime() -
            calendar.getTime().getTime() <= 0) {
            return "expired";
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {

        final int TOKEN_EXPIRATION_TIME = 10;
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(new Date().getTime());
        calender.add(Calendar.MINUTE, TOKEN_EXPIRATION_TIME);

        Date newExpirationTime = new Date(calender.getTime().getTime());
        String newToken = UUID.randomUUID().toString();

        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(newToken);
        verificationToken.setExpirationTime(newExpirationTime);

        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    @Override
    public void resendVerificationEmail(User user, String token, String applicationUrl) {

        String url = applicationUrl + "/verifyRegistration?token=" + token;
        log.info("Click here to verify your email --> " +url);
    }

    @Override
    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }

    @Override
    public void savePasswordResetTokenForUser(User user, String token) {

        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public void sendPasswordResetEmail(User user, String token, String applicationUrl) {

        String url = applicationUrl + "/savePassword?token=" +token;
        log.info("Click here to reset your password --> " +url);
    }

    @Override
    public String validatePasswordResetToken(String token) {

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if(passwordResetToken == null)
            return "invalid";

        Calendar calendar = Calendar.getInstance();
        if(passwordResetToken.getExpirationTime().getTime() -
            calendar.getTime().getTime() <= 0)
            return "expired";

        return "valid";
    }

    @Override
    public void saveNewPassword(String token, PasswordDto passwordDto) throws PasswordMismatchException {

        if(passwordDto.getPassword().equals(passwordDto.getConfirmPassword())) {
            PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
            if (passwordResetToken != null) {
                User user = passwordResetToken.getUser();
                user.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
                userRepository.save(user);
            }
        }
        else
            throw new PasswordMismatchException("Password and ConfirmPassword didn't match");
    }

    @Override
    public boolean checkIfOldPasswordIsValid(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public void changePassword(User user, String newPassword) {

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


}
