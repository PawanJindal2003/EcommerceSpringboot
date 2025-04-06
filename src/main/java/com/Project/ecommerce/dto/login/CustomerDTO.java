package com.Project.ecommerce.dto.login;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerDTO {
    private String accessToken;
    private String refreshToken;
    private String message;
    public CustomerDTO(String accessToken, String refreshToken, String message){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.message = message;
    }
}
