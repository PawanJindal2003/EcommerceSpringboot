package com.Project.ecommerce.exceptions;

import com.Project.ecommerce.dto.response.ErrorResponse;
import com.Project.ecommerce.exceptions.customExceptions.*;
import com.Project.ecommerce.utils.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseUtil responseUtil;

    @Autowired
    public GlobalExceptionHandler(ResponseUtil responseUtil){
        this.responseUtil = responseUtil;
    }
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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.NOT_FOUND, errorMessages), HttpStatus.NOT_FOUND);
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
    @ExceptionHandler(DuplicateCompanyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCompanyException(DuplicateCompanyException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(InactiveUserException.class)
    public ResponseEntity<ErrorResponse> handleInactiveUserException(InactiveUserException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.FORBIDDEN, errorMessages), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateGSTException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateGSTException(DuplicateGSTException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(DuplicateCategoryMetaDataFieldException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCategoryMetaDataFieldException(DuplicateCategoryMetaDataFieldException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateRootCategoryException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateRootCategoryException(DuplicateRootCategoryException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateSubCategoryException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateSubCategoryException(DuplicateSubCategoryException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryAssignedToProductException.class)
    public ResponseEntity<ErrorResponse> handleCategoryAssignedToProductException(CategoryAssignedToProductException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateMetadataFieldValuesException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateMetadataFieldValuesException(DuplicateMetadataFieldValuesException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidIdException(InvalidIdException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.NOT_FOUND, errorMessages), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FieldNotAssociatedException.class)
    public ResponseEntity<ErrorResponse> handleFieldNotAssociatedException(FieldNotAssociatedException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.NOT_FOUND, errorMessages), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAddressNotFoundException(AddressNotFoundException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.NOT_FOUND, errorMessages), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NonLeafCategoryException.class)
    public ResponseEntity<ErrorResponse> handleNonLeafCategoryException(NonLeafCategoryException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InactiveProductException.class)
    public ResponseEntity<ErrorResponse> handleInactiveProductException(InactiveProductException ex){
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

    @ExceptionHandler(DuplicateProductVariationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProductVariationException(DuplicateProductVariationException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidMetadataStructureException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMetadataStructureException(InvalidMetadataStructureException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidMetadataFieldException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMetadataFieldException(InvalidMetadataFieldException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidMetadataValueException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMetadataValueException(InvalidMetadataValueException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedImageTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedImageTypeException(UnsupportedImageTypeException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex){
        List<String> errorMessages = List.of(ex.getMessage());
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.NOT_FOUND, errorMessages), HttpStatus.NOT_FOUND);
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
        return new ResponseEntity<>(responseUtil.fail(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
    }
}
