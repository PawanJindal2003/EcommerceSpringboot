package com.Project.ecommerce.co;

import com.Project.ecommerce.entities.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerCO {
    @Email(message = "This is invalid email format, please enter correct email")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,15}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character")
    private String password;
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,15}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character")
    private String confirmPassword;

    @NotBlank
    @Pattern(regexp = "^\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}\\d{1}[Z]{1}[A-Z0-9]{1}$")
    private String GST;

    @NotBlank
    private String companyName;

    @NotBlank
    private String companyAddress;

    @NotBlank
    private String companyContact;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 3, max = 15, message = "First name must be between 3 and 15 characters")
    private String firstName;

    @Size(min = 3, max = 15, message = "Middle name must be between 3 and 15 characters")
    private String middleName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 3, max = 15, message = "Last name must be between 3 and 15 characters")
    private String lastName;

    Role role;
}
