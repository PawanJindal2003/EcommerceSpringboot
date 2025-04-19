package com.Project.ecommerce.exceptions.customExceptions;

public class InvalidMetadataValueException extends RuntimeException{
    public InvalidMetadataValueException(String message){
        super(message);
    }
}
