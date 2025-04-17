package com.Project.ecommerce.exceptions.customExceptions;

public class NonLeafCategoryException extends RuntimeException{
    public NonLeafCategoryException(String message){
        super(message);
    }
}