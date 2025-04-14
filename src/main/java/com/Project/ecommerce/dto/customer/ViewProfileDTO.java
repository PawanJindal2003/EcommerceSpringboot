package com.Project.ecommerce.dto.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewProfileDTO {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private Boolean isActive;
    private String customerContact;
//    private String image
}
