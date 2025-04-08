package com.Project.ecommerce.dto.login;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
    private String accessToken;
    private String message;
    public UserDTO(String accessToken, String message){
        this.accessToken = accessToken;
        this.message = message;
    }
}
