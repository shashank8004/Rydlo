package com.rydlo.exception_handler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.rydlo.custom_exception.DuplicateResourceException;
import com.rydlo.custom_exception.ResourceNotFoundException;
import com.rydlo.custom_exception.UserRoleMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();

        Map<String, String> fieldErrorMap = fieldErrors.stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fieldErrorMap);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicateResourceException(DuplicateResourceException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(UserRoleMismatchException.class)
    public ResponseEntity<?> handleUserRoleMismatchException(UserRoleMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong : " + e.getMessage());
    }
}
