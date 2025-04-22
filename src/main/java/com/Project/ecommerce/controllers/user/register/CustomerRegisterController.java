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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth/customer")
@RestController
@RequiredArgsConstructor
public class CustomerRegisterController {
    private final CustomerRegisterService customerRegisterService;
    private final ResponseUtil responseUtil;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> registerCustomer(@Valid @RequestBody CustomerCO customerCO) throws MessagingException {
        String response = customerRegisterService.registerCustomer(customerCO);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.CREATED, response), HttpStatus.CREATED);
    }

    @PutMapping("/activate/{token}")
    public ResponseEntity<SuccessResponse> activateCustomer(@PathVariable String token) throws MessagingException {
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
