package com.Project.ecommerce.controllers.user.login;

import com.Project.ecommerce.co.login.UserCO;
import com.Project.ecommerce.dto.login.UserDTO;
import com.Project.ecommerce.services.user.login.UserLoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth/admin")
@RestController
public class AdminLoginController {
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
}
