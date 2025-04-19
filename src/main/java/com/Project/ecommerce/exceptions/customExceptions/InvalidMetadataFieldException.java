package com.Project.ecommerce.exceptions.customExceptions;

public class InvalidMetadataFieldException extends RuntimeException{
    public InvalidMetadataFieldException(String message){
        super(message);
    }
}
