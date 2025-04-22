package com.Project.ecommerce.dto.seller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewProfileDTO {
    private String id;
    private String name;
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

    private String profilePicUrl;
}
