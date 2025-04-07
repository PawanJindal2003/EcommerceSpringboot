package com.Project.ecommerce.co.forgetPassword;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ForgetPasswordCO {
    @Email
    private String email;
}
