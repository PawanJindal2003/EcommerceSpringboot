package com.Project.ecommerce.exceptions.customExceptions;

public class InvalidIdException extends RuntimeException{
    public InvalidIdException(String message){
        super(message);
    }
}
