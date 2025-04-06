package com.Project.ecommerce.exceptions.customExceptions;

public class InactiveUserException extends RuntimeException{
    public InactiveUserException(String message){
        super(message);
    }
}
