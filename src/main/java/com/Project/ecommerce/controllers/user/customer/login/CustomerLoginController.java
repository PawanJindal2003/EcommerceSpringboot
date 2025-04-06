package com.Project.ecommerce.controllers.user.customer.login;

import com.Project.ecommerce.co.login.CustomerCO;
import com.Project.ecommerce.security.jwt.JwtService;
import com.Project.ecommerce.security.redis.RedisTokenService;
import com.Project.ecommerce.services.user.customer.login.CustomerLoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth/customer")
@RestController
public class CustomerLoginController {
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private RedisTokenService redisService;
    private CustomerLoginService customerLoginService;

    @Autowired
    public CustomerLoginController(CustomerLoginService customerLoginService) {
        this.customerLoginService = customerLoginService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginCustomer(@Valid @RequestBody CustomerCO customerCO, HttpServletResponse response) {
        return customerLoginService.loginCustomer(customerCO, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutCustomer(HttpServletRequest request, HttpServletResponse response) {
        return customerLoginService.logoutCustomer(request, response);
    }
}
