package com.Project.ecommerce.co.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCO {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(min = 6, max = 256, message = "Email must be between 6 and 256 characters")
    String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).*$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character"
    )
    private String password;
}
