package com.Project.ecommerce.controllers.user.forgetPassword;

import com.Project.ecommerce.co.forgetPassword.ForgetPasswordCO;
import com.Project.ecommerce.co.forgetPassword.ResetPasswordCO;
import com.Project.ecommerce.services.user.forgetPassword.ForgetPasswordService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/auth")
@RestController
public class ForgetPasswordController {
    private ForgetPasswordService forgetPasswordService;
    @Autowired
    public ForgetPasswordController(ForgetPasswordService forgetPasswordService){
        this.forgetPasswordService = forgetPasswordService;
    }

    @PostMapping("/forget-password")
    public ResponseEntity<String> forgetPassword(@Valid @RequestBody ForgetPasswordCO forgetPasswordCO) throws MessagingException {
        return forgetPasswordService.sendResetPasswordMail(forgetPasswordCO.getEmail());
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordCO resetPasswordCO){
        return forgetPasswordService.resetPassword(resetPasswordCO);
    }
}