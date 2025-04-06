package com.Project.ecommerce.co.registration;

import com.Project.ecommerce.entities.user.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
public class CustomerCO {
    @Email(message = "This is invalid email format, please enter correct email")
    private String email;

    @Length(min = 13, max = 16, message = "Phone number must include country code and be between 13 to 16 characters")
    @Pattern(regexp = "^\\+[1-9]\\d{0,3}[-\\s]?\\d{10}$", message = "Please enter country code, e.g., +91-7210003XXX")
    private String customerContact;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,15}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character")
    private String password;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,15}$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character")
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
