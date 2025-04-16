package com.Project.ecommerce.exceptions.customExceptions;

public class CategoryAssignedToProductException extends RuntimeException{
    public CategoryAssignedToProductException(String message){
        super(message);
    }
}