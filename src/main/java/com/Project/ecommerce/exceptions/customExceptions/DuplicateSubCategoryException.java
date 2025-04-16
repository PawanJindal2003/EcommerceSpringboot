package com.Project.ecommerce.exceptions.customExceptions;

public class DuplicateSubCategoryException extends RuntimeException{
    public DuplicateSubCategoryException(String message){
        super(message);
    }
}
