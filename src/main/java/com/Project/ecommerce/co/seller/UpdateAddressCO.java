package com.Project.ecommerce.co.seller;

import com.Project.ecommerce.entities.address.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAddressCO {
    private String city;
    private String state;
    private String country;
    private String addressLine;
    private String zipCode;
}
