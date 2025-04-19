package com.Project.ecommerce.exceptions.customExceptions;

public class DeletedProductException extends RuntimeException{
    public DeletedProductException(String message){
        super(message);
    }
}
