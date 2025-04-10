package com.Project.ecommerce.controllers.customer;

import com.Project.ecommerce.dto.customer.ViewAddressDTO;
import com.Project.ecommerce.dto.customer.ViewProfileDTO;
import com.Project.ecommerce.services.cutomer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private CustomerService customerService;
    @Autowired
    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @PreAuthorize("getRole('CUSTOMER')")
    @GetMapping("/me")
    public ResponseEntity<ViewProfileDTO> viewProfile(HttpServletRequest request){
        ViewProfileDTO viewProfileDTO = customerService.viewProfile(request);
        return new ResponseEntity<>(viewProfileDTO, HttpStatus.OK);
    }

    @PreAuthorize("getRole('CUSTOMER')")
    @GetMapping("/addresses")
    public ResponseEntity<List<ViewAddressDTO>> viewAddresses(HttpServletRequest request){
        List<ViewAddressDTO> addresses = customerService.getAllAddresses(request);
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }
}
