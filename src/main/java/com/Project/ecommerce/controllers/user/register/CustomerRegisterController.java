package com.Project.ecommerce.controllers.user.register;

import com.Project.ecommerce.co.registration.CustomerCO;
import com.Project.ecommerce.services.user.register.customer.CustomerRegisterService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth/customer")
@RestController
public class CustomerController {
    private CustomerRegisterService customerRegisterService;

    @Autowired
    public CustomerController(CustomerRegisterService customerRegisterService){
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
    public ResponseEntity<String> resendActivationEmail(@RequestParam String email) throws MessagingException {
        String response = customerRegisterService.resendActivationEmail(email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
