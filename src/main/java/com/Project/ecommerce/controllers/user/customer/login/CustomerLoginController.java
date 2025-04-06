package com.Project.ecommerce.controllers.user.customer.login;

import com.Project.ecommerce.co.login.CustomerCO;
import com.Project.ecommerce.security.jwt.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @Autowired
    public CustomerLoginController(AuthenticationManager authenticationManager, JwtService jwtService){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginCustomer(@Valid @RequestBody CustomerCO customerCO,  HttpServletResponse response){
        try{
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(customerCO.getEmail(), customerCO.getPassword())
            );

            String token = jwtService.generateAccessToken(customerCO.getEmail());

            Cookie cookie = new Cookie("loginToken", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);//for https
            cookie.setPath("/");
            cookie.setMaxAge(60 * 15);
            response.addCookie(cookie);

            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }
}
