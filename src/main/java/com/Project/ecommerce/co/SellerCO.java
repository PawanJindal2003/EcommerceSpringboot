package com.Project.ecommerce.co;

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
    @Length(min = 9, max = 20, message = "Please enter a valid contact number")
    @Pattern(regexp = "^\\+[1-9]\\d{1,3}[-/s]?[1-9]\\d{6,14}$", message = "Please enter country code, eg +91-7210003XXX")
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
