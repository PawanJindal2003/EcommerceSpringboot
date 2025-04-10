package com.Project.ecommerce.dto.seller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAddressDTO {
    private String city;
    private String state;
    private String country;
    private String addressLine;
    private String zipCode;
}
