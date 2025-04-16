package com.Project.ecommerce.exceptions.customExceptions;

public class DuplicateRootCategoryException extends RuntimeException{
    public DuplicateRootCategoryException(String message){
        super(message);
    }
}
