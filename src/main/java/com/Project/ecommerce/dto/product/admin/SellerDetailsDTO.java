package com.Project.ecommerce.dto.product.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerDetailsDTO {
    private String sellerId;
    private String name;
    private String companyName;
    private String companyContact;
    private String GST;
    private Boolean isActiveSeller;
}
