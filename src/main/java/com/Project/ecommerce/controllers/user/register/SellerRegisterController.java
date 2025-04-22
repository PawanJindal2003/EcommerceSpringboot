package com.Project.ecommerce.controllers.user.register;

import com.Project.ecommerce.co.registration.SellerCO;
import com.Project.ecommerce.dto.response.SuccessResponse;
import com.Project.ecommerce.services.user.register.seller.SellerRegisterService;
import com.Project.ecommerce.utils.ResponseUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth/seller")
@RestController
@RequiredArgsConstructor
public class SellerRegisterController {
    private final SellerRegisterService sellerRegisterService;
    private final ResponseUtil responseUtil;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> registerSeller(@Valid @RequestBody SellerCO sellerCO) throws MessagingException {
        String response = sellerRegisterService.registerSeller(sellerCO);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.CREATED, response), HttpStatus.CREATED);
    }

}