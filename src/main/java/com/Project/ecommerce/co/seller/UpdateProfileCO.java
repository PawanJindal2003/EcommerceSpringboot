package com.Project.ecommerce.co.seller;

import com.Project.ecommerce.entities.address.Address;
import jakarta.annotation.Nullable;
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

    private Boolean isActive;

    @Size(min = 10, max = 20, message = "Please enter a valid contact number")
    @Pattern(
            regexp = "^\\+[1-9]{1}[0-9]{0,3}[-\\s]?[1-9]{1}[0-9]{6,11}$",
            message = "Please enter a valid phone number with country code, e.g., +91-7210003XXX"
    )
    private String companyContact;

    private String companyName;

    @Size(min = 15, max = 15, message = "GST number must contain exact 15 characters")
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Invalid GST number format")
    private String GST;
}
