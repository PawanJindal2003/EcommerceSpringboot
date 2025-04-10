package com.Project.ecommerce.controllers.seller;

import com.Project.ecommerce.co.seller.UpdateAddressCO;
import com.Project.ecommerce.co.seller.UpdatePasswordCO;
import com.Project.ecommerce.co.seller.UpdateProfileCO;
import com.Project.ecommerce.dto.seller.ViewProfileDTO;
import com.Project.ecommerce.repositories.user.SellerRepository;
import com.Project.ecommerce.services.seller.SellerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/seller")
public class SellerController {
    private SellerService sellerService;

    private SellerRepository sellerRepository;
    @Autowired
    public SellerController(SellerService sellerService, SellerRepository sellerRepository){
        this.sellerService = sellerService;
        this.sellerRepository = sellerRepository;
    }
    @PreAuthorize("getRole('SELLER')")
    @GetMapping("/me")
    public ResponseEntity<ViewProfileDTO> viewProfile(HttpServletRequest request){
        ViewProfileDTO viewProfileDTO = sellerService.viewProfile(request);
        return new ResponseEntity<>(viewProfileDTO, HttpStatus.OK);
    }

    @PreAuthorize("getRole('SELLER')")
    @PatchMapping("/update-profile")
    public ResponseEntity<String> updateProfile(HttpServletRequest request, @Valid @RequestBody UpdateProfileCO updateProfileCO){
        String responseMessage = sellerService.updateProfile(request, updateProfileCO);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PreAuthorize("getRole('SELLER')")
    @PatchMapping("/update-password")
    public ResponseEntity<String> updatePassword(HttpServletRequest request, @Valid @RequestBody UpdatePasswordCO updatePasswordCO){
        String responseMessage = sellerService.updatePassword(request, updatePasswordCO);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PreAuthorize("getRole('SELLER')")
    @PatchMapping("/update-address")
    public ResponseEntity<String> updateAddress(HttpServletRequest request, @Valid @RequestBody UUID addressId, @Valid @RequestBody UpdateAddressCO updateAddressCO){
        String responseMessage = sellerService.updateAddress(request, addressId , updateAddressCO);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
