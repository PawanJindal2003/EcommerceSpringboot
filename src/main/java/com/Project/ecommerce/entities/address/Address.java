package com.Project.ecommerce.entities.address;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private String id;
    @PrePersist
    public void prePersist(){
        if(id == null){
            this.id = UuidCreator.getTimeOrderedEpoch().toString();
            //this.id = UuidCreator.getTimeBased().toString(); //v1 uuid
        }
    }

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
