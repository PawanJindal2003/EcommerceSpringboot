package com.Project.ecommerce.co.forgetPassword;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordCO {
    private String forgetPasswordToken;
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,15}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character")

    private String password;
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,15}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character")

    private String confirmPassword;
}
