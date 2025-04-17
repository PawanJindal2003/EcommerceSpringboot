package com.Project.ecommerce.exceptions.customExceptions;

public class AddressNotFoundException extends RuntimeException{
    public AddressNotFoundException(String message){
        super(message);
    }
}