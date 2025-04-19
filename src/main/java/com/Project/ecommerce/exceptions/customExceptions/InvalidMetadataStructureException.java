package com.Project.ecommerce.exceptions.customExceptions;

public class InvalidMetadataStructureException extends RuntimeException{
    public InvalidMetadataStructureException(String message){
        super(message);
    }
}
