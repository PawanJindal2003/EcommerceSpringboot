package com.Project.ecommerce.exceptions.customExceptions;

public class FieldNotAssociatedException extends RuntimeException{
    public FieldNotAssociatedException(String message){
        super(message);
    }
}
