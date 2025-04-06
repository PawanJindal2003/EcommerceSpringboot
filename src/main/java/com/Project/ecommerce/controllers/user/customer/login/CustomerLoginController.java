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
    private RedisTokenService redisService;
    private CustomerLoginService customerLoginService;

    @Autowired
    public CustomerLoginController(AuthenticationManager authenticationManager, JwtService jwtService, RedisTokenService redisService, CustomerLoginService customerLoginService){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.redisService = redisService;
        this.customerLoginService = customerLoginService;

    }

    @PostMapping("/login")
    public ResponseEntity<String> loginCustomer(@Valid @RequestBody CustomerCO customerCO,  HttpServletResponse response){
        try{
            customerLoginService.validateLogin(customerCO.getEmail(), customerCO.getPassword());
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
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutCustomer(HttpServletRequest request, HttpServletResponse response){
        String token = null;
        if(request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                if("loginToken".equals(cookie.getName())){
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token != null){
            redisService.revokeToken(token);
        }

        Cookie cookie = new Cookie("loginToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire it immediately
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out");
    }
}
