package com.Project.ecommerce.exceptions.customExceptions;

public class DuplicateProductVariationException extends RuntimeException{
    public DuplicateProductVariationException(String message){
        super(message);
    }
}
