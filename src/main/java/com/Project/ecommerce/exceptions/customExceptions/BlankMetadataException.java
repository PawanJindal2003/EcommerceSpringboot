package com.Project.ecommerce.exceptions.customExceptions;

public class BlankMetadataException extends RuntimeException{
    public BlankMetadataException(String message){
        super(message);
    }
}
