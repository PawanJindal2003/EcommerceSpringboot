package com.Project.ecommerce.exceptions.customExceptions;

public class DuplicateMetadataFieldValuesException extends RuntimeException{
    public DuplicateMetadataFieldValuesException(String message){
        super(message);
    }
}
