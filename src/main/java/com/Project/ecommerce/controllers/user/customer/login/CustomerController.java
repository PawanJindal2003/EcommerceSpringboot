package com.Project.ecommerce.controllers.user.customer.login;

import com.Project.ecommerce.co.login.CustomerCO;
import com.Project.ecommerce.services.user.customer.login.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth/customer")
@RestController
public class CustomerController {
    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginCustomer(@Valid @RequestBody CustomerCO customerCO){
        return customerService.loginCustomer(customerCO);
    }
}
