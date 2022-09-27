package com.tejacodes.springsecurityclient.service;

import com.tejacodes.springsecurityclient.dto.PasswordDto;
import com.tejacodes.springsecurityclient.dto.UserDto;
import com.tejacodes.springsecurityclient.entity.User;
import com.tejacodes.springsecurityclient.entity.VerificationToken;
import com.tejacodes.springsecurityclient.exception.PasswordMismatchException;

public interface UserService {
    User registerUser(UserDto userDto) throws PasswordMismatchException;

    void saveVerificationTokenForUser(User user, String token);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    void resendVerificationEmail(User user, String token, String applicationUrl);

    User findUserByEmail(String email);

    void savePasswordResetTokenForUser(User user, String token);

    void sendPasswordResetEmail(User user, String token, String applicationUrl);

    String validatePasswordResetToken(String token);

    void saveNewPassword(String token, PasswordDto passwordDto) throws PasswordMismatchException;

    void changePassword(User user, String newPassword);

    boolean checkIfOldPasswordIsValid(User user, String oldPassword);
}
