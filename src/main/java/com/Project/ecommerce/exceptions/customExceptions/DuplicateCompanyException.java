package com.Project.ecommerce.exceptions.customExceptions;

public class DuplicateCompanyException extends RuntimeException{
    public DuplicateCompanyException(String message){
        super(message);
    }
}
