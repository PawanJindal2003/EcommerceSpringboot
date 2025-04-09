package com.Project.ecommerce.exceptions.customExceptions;

public class DuplicateGSTException extends RuntimeException{
    public DuplicateGSTException(String message){
        super(message);
    }
}
