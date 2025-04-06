package com.Project.ecommerce.exceptions.customExceptions;

public class ConfirmPasswordMismatchException extends RuntimeException{
    public ConfirmPasswordMismatchException(String message){
        super(message);
    }
}
