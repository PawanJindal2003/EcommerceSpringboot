package com.Project.ecommerce.exceptions;

import com.Project.ecommerce.dto.response.ErrorResponse;
import com.Project.ecommerce.exceptions.customExceptions.*;
import com.Project.ecommerce.utils.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ResponseUtil responseUtil;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex){
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(HandlerMethodValidationException ex){
        List<String> errorMessages = ex.getAllErrors().stream().map(MessageSourceResolvable::getDefaultMessage).toList();
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistExceptions(EmailAlreadyExistsException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConfirmPasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handleConfirmPasswordMismatchExceptions(ConfirmPasswordMismatchException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.UNAUTHORIZED, errorMessages), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.UNAUTHORIZED, errorMessages), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.UNAUTHORIZED, errorMessages), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.UNAUTHORIZED, errorMessages), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLockedException(LockedException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.LOCKED, errorMessages), HttpStatus.LOCKED);
    }

    @ExceptionHandler(LockedAccountException.class)
    public ResponseEntity<ErrorResponse> handleLockedAccountException(LockedAccountException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.LOCKED, errorMessages), HttpStatus.LOCKED);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.UNAUTHORIZED, errorMessages), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CategoryAssignedToProductException.class)
    public ResponseEntity<ErrorResponse> handleCategoryAssignedToProductException(CategoryAssignedToProductException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FieldNotAssociatedException.class)
    public ResponseEntity<ErrorResponse> handleFieldNotAssociatedException(FieldNotAssociatedException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.NOT_FOUND, errorMessages), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NonLeafCategoryException.class)
    public ResponseEntity<ErrorResponse> handleNonLeafCategoryException(NonLeafCategoryException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DeletedProductException.class)
    public ResponseEntity<ErrorResponse> handleDeletedProductException(DeletedProductException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BlankMetadataException.class)
    public ResponseEntity<ErrorResponse> handleBlankMetadataException(BlankMetadataException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedImageTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedImageTypeException(UnsupportedImageTypeException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.NOT_FOUND, errorMessages), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.UNAUTHORIZED, errorMessages), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InactiveResourceException.class)
    public ResponseEntity<ErrorResponse> handleInactiveResourceException(InactiveResourceException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidResourceException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResourceException(InvalidResourceException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.FORBIDDEN, errorMessages), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex){
        List<String> errorMessages = List.of(ex.getMessage() + " : you do not have permission to perform this action.");
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.FORBIDDEN, errorMessages), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ExpiredPasswordException.class)
    public ResponseEntity<ErrorResponse> handleExpiredPasswordException(ExpiredPasswordException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.UNAUTHORIZED, errorMessages), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(StoreImageFailureException.class)
    public ResponseEntity<ErrorResponse> handleStoreImageFailureException(StoreImageFailureException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }
}