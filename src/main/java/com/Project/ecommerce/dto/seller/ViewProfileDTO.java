package com.Project.ecommerce.dto.seller;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ViewProfileDTO {
    private UUID id;
    private String firstName;
    private String middleName;
    private String lastName;
    private Boolean isActive;
    private String companyContact;
    private String companyName;
    private String GST;

    //address
    private String city;
    private String state;
    private String country;
    private String addressLine;
    private String zipCode;
}
