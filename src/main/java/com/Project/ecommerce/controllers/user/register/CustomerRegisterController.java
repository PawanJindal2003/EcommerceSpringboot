package com.Project.ecommerce.controllers.user.register;

import com.Project.ecommerce.co.registration.CustomerCO;
import com.Project.ecommerce.services.user.register.customer.CustomerRegisterService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth/customer")
@RestController
public class CustomerRegisterController {
    private CustomerRegisterService customerRegisterService;

    @Autowired
    public CustomerRegisterController(CustomerRegisterService customerRegisterService) {
        this.customerRegisterService = customerRegisterService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerCO customerCO) throws MessagingException {
        String response = customerRegisterService.registerCustomer(customerCO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/activate")
    public ResponseEntity<String> activateCustomer(@RequestParam("token") String token) throws MessagingException {
        return customerRegisterService.activateCustomer(token);
    }

    @PostMapping("/resend-activation-link")
    public ResponseEntity<String> resendActivationEmail(@RequestParam
                                                            @NotBlank(message = "Email is required")
                                                            @Email(message = "Invalid email format")
                                                            @Size(min = 6, max = 256, message = "Email must be between 6 and 256 characters") String email) throws MessagingException {
        String response = customerRegisterService.resendActivationEmail(email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
