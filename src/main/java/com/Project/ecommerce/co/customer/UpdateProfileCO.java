package com.Project.ecommerce.co.customer;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileCO {
    @Size(min = 3, max = 20, message = "First name must be of length between 3-50")
    private String firstName;
    @Size(min = 3, max = 20, message = "Middle name must be of length between 3-50")
    private String middleName;
    @Size(min = 3, max = 20, message = "Last name must be of length between 3-50")
    private String lastName;

    @Size(min = 10, max = 20, message = "Please enter a valid contact number")
    @Pattern(
            regexp = "^\\+[1-9]{1}[0-9]{0,3}[-\\s]?[1-9]{1}[0-9]{6,11}$",
            message = "Please enter a valid phone number with country code, e.g., +91-7210003XXX"
    )
    private String customerContact;
}
