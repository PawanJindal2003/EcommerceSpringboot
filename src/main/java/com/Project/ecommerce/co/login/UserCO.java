package com.Project.ecommerce.co.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCO {
    @Email(message = "This is invalid email format, please enter correct email")
    String email;
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,15}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character")
    private String password;
}
