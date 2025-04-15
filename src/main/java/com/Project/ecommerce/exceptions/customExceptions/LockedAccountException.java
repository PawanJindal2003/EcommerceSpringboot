package com.Project.ecommerce.exceptions.customExceptions;

public class LockedAccountException extends RuntimeException{
    public LockedAccountException(String message){
        super(message);
    }
}
