package com.Project.ecommerce.controllers.user.register;

import com.Project.ecommerce.co.registration.CustomerCO;
import com.Project.ecommerce.dto.response.SuccessResponse;
import com.Project.ecommerce.services.user.register.customer.CustomerRegisterService;
import com.Project.ecommerce.utils.ResponseUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth/customer")
@RestController
public class CustomerRegisterController {
    private CustomerRegisterService customerRegisterService;
    private ResponseUtil responseUtil;

    @Autowired
    public CustomerRegisterController(CustomerRegisterService customerRegisterService, ResponseUtil responseUtil) {
        this.customerRegisterService = customerRegisterService;
        this.responseUtil = responseUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> registerCustomer(@Valid @RequestBody CustomerCO customerCO) throws MessagingException {
        String response = customerRegisterService.registerCustomer(customerCO);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.CREATED, response), HttpStatus.CREATED);
    }

    @PutMapping("/activate")
    public ResponseEntity<SuccessResponse> activateCustomer(@RequestParam("token") String token) throws MessagingException {
        String response = customerRegisterService.activateCustomer(token);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.CREATED, response), HttpStatus.CREATED);
    }

    @PostMapping("/resend-activation-link")
    public ResponseEntity<SuccessResponse> resendActivationEmail(@RequestParam
                                                            @NotBlank(message = "Email is required")
                                                            @Email(message = "Invalid email format")
                                                            @Size(min = 6, max = 256, message = "Email must be between 6 and 256 characters") String email) throws MessagingException {
        String response = customerRegisterService.resendActivationEmail(email);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.CREATED, response), HttpStatus.CREATED);
    }
}
