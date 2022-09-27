package com.tejacodes.springsecurityclient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatchException(PasswordMismatchException exc)
    {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exc.getMessage());
        return new ResponseEntity(errorResponse,HttpStatus.BAD_REQUEST);
    }
}
