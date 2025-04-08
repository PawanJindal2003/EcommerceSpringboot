package com.Project.ecommerce.co.registration;

import com.Project.ecommerce.entities.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class SellerCO {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(min = 6, max = 256, message = "Email must be between 6 and 256 characters")
    private String email;

    @NotBlank(message = "Message is required")
    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters")
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

    @NotBlank(message = "GST number cannot be blank")
    @Size(min = 15, max = 15, message = "GST number must contain exact 15 characters")
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Invalid GST number format")
    private String GST;

    @NotBlank(message = "Company name cannot be blank")
    private String companyName;

    @NotBlank(message = "Company address cannot be blank")
    private String companyAddress;

    @NotBlank(message = "Contact number cannot be blank")
    @Size(min = 10, max = 20, message = "Please enter a valid contact number")
    @Pattern(
            regexp = "^\\+[1-9]{1}[0-9]{0,3}[-\\s]?[1-9]{1}[0-9]{6,11}$",
            message = "Please enter a valid phone number with country code, e.g., +91-7210003XXX"
    )
    private String companyContact;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 3, max = 15, message = "First name must be between 3 and 15 characters")
    private String firstName;

    @Size(min = 3, max = 15, message = "Middle name must be between 3 and 15 characters")
    private String middleName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 3, max = 15, message = "Last name must be between 3 and 15 characters")
    private String lastName;

    private Role role;
}
