package com.Project.ecommerce.dto.admin;

import com.Project.ecommerce.entities.address.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAllSellersDTO {
    private String id;
    private String name;
    private String email;
    private Boolean isActive;
    private String companyName;
    private Address companyAddress;
    private String companyContact;
}
