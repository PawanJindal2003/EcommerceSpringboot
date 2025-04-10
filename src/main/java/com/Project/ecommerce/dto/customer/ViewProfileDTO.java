package com.Project.ecommerce.dto.customer;

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
    private String customerContact;
//    private String image
}
