package com.Project.ecommerce.co.registration;

import com.Project.ecommerce.entities.user.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerCO {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(min = 6, max = 254, message = "Email must be between 6 and 254 characters")
    private String email;

    @NotBlank(message = "Contact number is required")
    @Size(min = 10, max = 20, message = "Phone number must be between 10 and 20 characters")
    @Pattern(
            regexp = "^\\+[1-9]{1}[0-9]{0,3}[-\\s]?[1-9]{1}[0-9]{6,11}$",
            message = "Please enter a valid phone number with country code, e.g., +91-7210003XXX"
    )
    private String customerContact;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 15, message = "Confirm password must be between 8 and 15 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).*$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character"
    )
    private String password;

    @NotBlank(message = "Confirm password is required")
    @Size(min = 8, max = 15, message = "Confirm password must be between 8 and 15 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).*$",
            message = "Confirm password must contain at least one lowercase letter, one uppercase letter, one number, and one special character"
    )
    private String confirmPassword;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 3, max = 15, message = "First name must be between 3 and 15 characters")
    private String firstName;

    @Size(max = 15, message = "Middle name must not exceed 15 characters")
    private String middleName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 3, max = 15, message = "Last name must be between 3 and 15 characters")
    private String lastName;

    private Role role;
}
