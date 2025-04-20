package com.Project.ecommerce.exceptions.customExceptions;

public class InvalidResourceException extends RuntimeException{
    public InvalidResourceException(String message){
        super(message);
    }
}
