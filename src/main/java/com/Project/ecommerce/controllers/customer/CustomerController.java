package com.Project.ecommerce.controllers.customer;

import com.Project.ecommerce.co.customer.UpdateProfileCO;
import com.Project.ecommerce.co.user.UpdateAddressCO;
import com.Project.ecommerce.co.user.UpdatePasswordCO;
import com.Project.ecommerce.dto.customer.ViewAddressDTO;
import com.Project.ecommerce.dto.customer.ViewProfileDTO;
import com.Project.ecommerce.entities.address.Address;
import com.Project.ecommerce.services.cutomer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PreAuthorize("getRole('CUSTOMER')")
    @PutMapping("/update-profile")
    public ResponseEntity<String> updateProfile(HttpServletRequest request, @Valid @RequestBody UpdateProfileCO updateProfile){
        String responseMessage = customerService.updateProfile(request, updateProfile);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PreAuthorize("getRole('CUSTOMER')")
    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(HttpServletRequest request, @Valid @RequestBody UpdatePasswordCO updatePasswordCO){
        String responseMessage = customerService.updatePassword(request, updatePasswordCO);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PreAuthorize("getRole('CUSTOMER')")
    @PostMapping("/add-address")
    public ResponseEntity<String> addAddress(HttpServletRequest request, @Valid @RequestBody Address address){
        String responseMessage = customerService.addAddress(request, address);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PreAuthorize("getRole('CUSTOMER')")
    @DeleteMapping("/delete-address")
    public ResponseEntity<String> deleteAddress(HttpServletRequest request, @Valid @RequestBody UpdateAddressCO updateAddressCO){
        String responseMessage = customerService.deleteAddress(request, updateAddressCO);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PreAuthorize("getRole('CUSTOMER')")
    @PutMapping("/update-address")
    public ResponseEntity<String> updateAddress(HttpServletRequest request,@Valid @RequestBody UpdateAddressCO updateAddressCO){
        String responseMessage = customerService.updateAddress(request, updateAddressCO);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
