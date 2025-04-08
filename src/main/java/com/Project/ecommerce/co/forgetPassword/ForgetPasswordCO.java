package com.Project.ecommerce.co.forgetPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ForgetPasswordCO {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(min = 6, max = 256, message = "Email must be between 6 and 256 characters")
    private String email;
}
