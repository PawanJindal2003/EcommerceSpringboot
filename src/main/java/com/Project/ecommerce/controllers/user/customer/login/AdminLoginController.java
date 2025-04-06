package com.Project.ecommerce.controllers.user.customer.login;

import com.Project.ecommerce.co.login.UserCO;
import com.Project.ecommerce.dto.login.UserDTO;
import com.Project.ecommerce.security.jwt.JwtService;
import com.Project.ecommerce.security.redis.RedisTokenService;
import com.Project.ecommerce.services.user.login.UserLoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth/admin")
@RestController
public class AdminLoginController {
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private RedisTokenService redisService;
    private UserLoginService userLoginService;

    @Autowired
    public AdminLoginController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginCustomer(@Valid @RequestBody UserCO userCO, HttpServletResponse response) {
        return userLoginService.loginCustomer(userCO, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutCustomer(HttpServletRequest request, HttpServletResponse response) {
        return userLoginService.logoutCustomer(request, response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<UserDTO> refreshToken(HttpServletRequest request, HttpServletResponse response){
        return userLoginService.refreshAccessToken(request, response);
    }
}
