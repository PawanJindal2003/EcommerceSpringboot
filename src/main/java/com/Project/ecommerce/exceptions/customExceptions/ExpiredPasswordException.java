package com.Project.ecommerce.exceptions.customExceptions;

public class ExpiredPasswordException extends RuntimeException{
    public ExpiredPasswordException(String message){
        super(message);
    }
}