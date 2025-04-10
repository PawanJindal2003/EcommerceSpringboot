package com.Project.ecommerce.dto.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewAddressDTO {
    private String city;
    private String state;
    private String country;
    private String addressLine;
    private String zipCode;
    private String label;
}
