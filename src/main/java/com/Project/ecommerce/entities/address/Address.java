package com.Project.ecommerce.entities.address;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "City cannot be blank")
    private String city;


    @NotBlank(message = "State cannot be blank")
    private String state;


    @NotBlank(message = "Country cannot be blank")
    private String country;

    @NotBlank(message = "Address cannot be blank")
    private String addressLine;

    @NotBlank(message = "Zipcode cannot be blank")
    private String zipCode;

    private String label;
}
