package com.Project.ecommerce.controllers.seller;

import com.Project.ecommerce.co.user.UpdateAddressCO;
import com.Project.ecommerce.co.user.UpdatePasswordCO;
import com.Project.ecommerce.co.seller.UpdateProfileCO;
import com.Project.ecommerce.dto.response.SuccessResponse;
import com.Project.ecommerce.dto.seller.ViewProfileDTO;
import com.Project.ecommerce.services.seller.SellerService;
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
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;
    private final ResponseUtil responseUtil;

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse> viewProfile(HttpServletRequest request){
        ViewProfileDTO viewProfileDTO = sellerService.viewProfile(request);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, List.of(viewProfileDTO)), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping(value = "/update-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse> updateProfile(HttpServletRequest request,
                                                @Valid UpdateProfileCO updateProfileCO,
                                                @RequestPart(value = "profilePic", required = false) MultipartFile multipartFile){
        String responseMessage = sellerService.updateProfile(request, updateProfileCO, multipartFile);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/update-password")
    public ResponseEntity<SuccessResponse> updatePassword(HttpServletRequest request, @Valid @RequestBody UpdatePasswordCO updatePasswordCO){
        String responseMessage = sellerService.updatePassword(request, updatePasswordCO);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/update-address/{addressId}")
    public ResponseEntity<SuccessResponse> updateAddress(Principal principal, @Valid @PathVariable String addressId, @Valid @RequestBody UpdateAddressCO updateAddressCO){
        String responseMessage = sellerService.updateAddress(principal, addressId , updateAddressCO);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }
}
