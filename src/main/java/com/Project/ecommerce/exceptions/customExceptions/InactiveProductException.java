package com.Project.ecommerce.exceptions.customExceptions;

public class InactiveProductException extends RuntimeException{
    public InactiveProductException(String message){
        super(message);
    }
}
