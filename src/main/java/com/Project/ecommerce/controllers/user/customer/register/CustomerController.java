package com.Project.ecommerce.controllers.user;

import com.Project.ecommerce.co.registration.CustomerCO;
import com.Project.ecommerce.repositories.user.UserRepository;
import com.Project.ecommerce.services.user.CustomerService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth/customer")
@RestController
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private UserRepository userRepository;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerCO customerCO) throws MessagingException {
        String response = customerService.registerCustomer(customerCO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PutMapping("/activate")
    public ResponseEntity<String> activateCustomer(@RequestParam("token") String token) throws MessagingException {
        return customerService.activateCustomer(token);
    }
    @PostMapping("/resend-activation-link")
    public ResponseEntity<String> resendActivationEmail(@RequestParam String email) throws MessagingException {
        String response = customerService.resendActivationEmail(email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
