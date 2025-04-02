package com.Project.ecommerce.exceptions.customExceptions;

public class ConfirmPasswordMismatch extends RuntimeException{
    public ConfirmPasswordMismatch(String message){
        super(message);
    }
}
