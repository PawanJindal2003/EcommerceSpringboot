package com.Project.ecommerce.exceptions.customExceptions;

public class DuplicateCategoryMetaDataFieldException extends RuntimeException{
    public DuplicateCategoryMetaDataFieldException(String message){
        super(message);
    }
}
