package com.Project.ecommerce.controllers.user.forgetPassword;

import com.Project.ecommerce.co.forgetPassword.ForgetPasswordCO;
import com.Project.ecommerce.co.forgetPassword.ResetPasswordCO;
import com.Project.ecommerce.dto.response.SuccessResponse;
import com.Project.ecommerce.services.user.forgetPassword.ForgetPasswordService;
import com.Project.ecommerce.utils.ResponseUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/auth")
@RestController
@RequiredArgsConstructor
public class ForgetPasswordController {
    private final ForgetPasswordService forgetPasswordService;
    private final ResponseUtil responseUtil;

    @PostMapping("/forget-password")
    public ResponseEntity<SuccessResponse> forgetPassword(@Valid @RequestBody ForgetPasswordCO forgetPasswordCO) throws MessagingException {
        String response = forgetPasswordService.sendResetPasswordMail(forgetPasswordCO.getEmail());
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, response), HttpStatus.OK);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<SuccessResponse> resetPassword(@Valid @RequestBody ResetPasswordCO resetPasswordCO){
        String response =  forgetPasswordService.resetPassword(resetPasswordCO);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, response), HttpStatus.OK);
    }
}