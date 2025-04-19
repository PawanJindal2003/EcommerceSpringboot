package com.Project.ecommerce.exceptions.customExceptions;

public class UnsupportedImageTypeException extends RuntimeException{
    public UnsupportedImageTypeException(String message){
        super(message);
    }
}
