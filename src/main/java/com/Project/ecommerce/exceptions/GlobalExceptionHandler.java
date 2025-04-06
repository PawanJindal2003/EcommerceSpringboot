package com.Project.ecommerce.exceptions;

import com.Project.ecommerce.exceptions.customExceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex){
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return new ResponseEntity<>(new ErrorResponse(404, "Validation failed: ", errorMessages), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistExceptions(EmailAlreadyExistsException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(400, "Validation failed: ", errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConfirmPasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handleConfirmPasswordMismatchExceptions(ConfirmPasswordMismatchException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(400, "Validation failed: ", errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(404, "Registeration failure: ", errorMessages), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(404, "Please provide a valid jwt", errorMessages), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(404, "Please provide a valid jwt", errorMessages), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(202, "Token expired", errorMessages), HttpStatus.ACCEPTED);
    }
    @ExceptionHandler(DuplicateCompanyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCompanyException(DuplicateCompanyException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(400, "Company Name Issue", errorMessages), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(404, "Either email or password incorrect", errorMessages), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLockedException(LockedException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(403, "Account is locked due to multiple failed attempts", errorMessages), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InactiveUserException.class)
    public ResponseEntity<ErrorResponse> handleInactiveUserException(InactiveUserException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(404, "Inactive account", errorMessages), HttpStatus.BAD_REQUEST);
    }
}
