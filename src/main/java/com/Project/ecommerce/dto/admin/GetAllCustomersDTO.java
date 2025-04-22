package com.Project.ecommerce.dto.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetAllCustomersDTO {
    private String id;
    private String name;
    private String email;
    private Boolean isActive;
}
