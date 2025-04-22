package com.Project.ecommerce.controllers.customer;

import com.Project.ecommerce.co.customer.UpdateProfileCO;
import com.Project.ecommerce.co.user.UpdateAddressCO;
import com.Project.ecommerce.co.user.UpdatePasswordCO;
import com.Project.ecommerce.dto.response.SuccessResponse;
import com.Project.ecommerce.dto.customer.ViewAddressDTO;
import com.Project.ecommerce.dto.customer.ViewProfileDTO;
import com.Project.ecommerce.entities.address.Address;
import com.Project.ecommerce.services.cutomer.CustomerService;
import com.Project.ecommerce.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final ResponseUtil responseUtil;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse> viewProfile(Principal principal){
        ViewProfileDTO viewProfileDTO = customerService.viewProfile(principal);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, List.of(viewProfileDTO)), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/addresses")
    public ResponseEntity<SuccessResponse> viewAddresses(Principal principal){
        List<ViewAddressDTO> addresses = customerService.getAllAddresses(principal);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, addresses), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping(value = "/update-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse> updateProfile(Principal principal,
                                                @RequestPart(value = "profilePic", required = false) MultipartFile multipartFile,
                                                @Valid UpdateProfileCO updateProfile){
        String responseMessage = customerService.updateProfile(principal, updateProfile, multipartFile);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/update-password")
    public ResponseEntity<SuccessResponse> updatePassword(Principal principal, @Valid @RequestBody UpdatePasswordCO updatePasswordCO){
        String responseMessage = customerService.updatePassword(principal, updatePasswordCO);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/add-address")
    public ResponseEntity<SuccessResponse> addAddress(Principal principal, @Valid @RequestBody Address address){
        String responseMessage = customerService.addAddress(principal, address);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/delete-address/{addressId}")
    public ResponseEntity<SuccessResponse> deleteAddress(Principal principal, @PathVariable String addressId){
        String responseMessage = customerService.deleteAddress(principal, addressId);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/update-address/{addressId}")
    public ResponseEntity<SuccessResponse> updateAddress(Principal principal, @PathVariable String addressId, @Valid @RequestBody UpdateAddressCO updateAddressCO){
        String responseMessage = customerService.updateAddress(principal, addressId, updateAddressCO);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }
}