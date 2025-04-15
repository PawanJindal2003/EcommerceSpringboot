 package com.Project.ecommerce.controllers.user.login;

import com.Project.ecommerce.co.login.UserCO;
import com.Project.ecommerce.dto.response.SuccessResponse;
import com.Project.ecommerce.services.user.login.UserLoginService;
import com.Project.ecommerce.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

 @RequestMapping("/api/auth")
@RestController
public class UserLoginController {
    private UserLoginService userLoginService;
    private ResponseUtil responseUtil;

    @Autowired
    public UserLoginController(UserLoginService userLoginService, ResponseUtil responseUtil) {
        this.userLoginService = userLoginService;
        this.responseUtil = responseUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse> loginUser(@Valid @RequestBody UserCO userCO, HttpServletResponse response) {
        List<String> responseData = userLoginService.loginUser(userCO, response);
        return new ResponseEntity<>(responseUtil.successWithDataAndMessage(List.of(responseData.get(1)), HttpStatus.OK, responseData.get(0)), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String responseData = userLoginService.logoutUser(request, response);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseData), HttpStatus.OK);
    }
}
