package com.Project.ecommerce.dto.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewProfileDTO {
    private String id;
    private String name;
    private Boolean isActive;
    private String customerContact;
    private String profilePicUrl;
}
