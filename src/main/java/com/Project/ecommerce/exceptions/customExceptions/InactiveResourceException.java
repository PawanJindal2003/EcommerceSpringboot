package com.Project.ecommerce.exceptions.customExceptions;

public class InactiveResourceException extends RuntimeException{
    public InactiveResourceException(String message){
        super(message);
    }
}
