package com.Project.ecommerce.controllers.user.register;

import com.Project.ecommerce.co.registration.SellerCO;
import com.Project.ecommerce.repositories.user.UserRepository;
import com.Project.ecommerce.services.user.register.seller.SellerRegisterService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth/seller")
@RestController
public class SellerController {
    private SellerRegisterService sellerRegisterService;
    private UserRepository userRepository;

    @Autowired
    public SellerController(SellerRegisterService sellerRegisterService, UserRepository userRepository){
        this.sellerRegisterService = sellerRegisterService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerSeller(@Valid @RequestBody SellerCO sellerCO) throws MessagingException {
        String response = sellerRegisterService.registerSeller(sellerCO);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/sellers")
    public void deleteSellers(){
        userRepository.deleteAll();
    }

}