package com.Project.ecommerce.services.user.forgetPassword;

import com.Project.ecommerce.co.forgetPassword.ResetPasswordCO;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.ConfirmPasswordMismatchException;
import com.Project.ecommerce.exceptions.customExceptions.UserNotFoundException;
import com.Project.ecommerce.repositories.user.UserRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class ForgetPasswordService {
    private UserRepository userRepository;
    private JwtService jwtService;
    private ForgetPasswordEmailService forgetPasswordEmailService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public ForgetPasswordService(UserRepository userRepository, JwtService jwtService, ForgetPasswordEmailService forgetPasswordEmailService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.forgetPasswordEmailService = forgetPasswordEmailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ResponseEntity<String> sendResetPasswordMail(String email) throws MessagingException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Email not found"));

        //throwing error response if an inactive user trying to forget password
        if(!userRepository.findByEmail(email).get().getIsActive()){
            return new ResponseEntity<>("Please activate your account", HttpStatus.BAD_REQUEST);
        }

        //throwing error response if a locked user trying to forget password
        if(userRepository.findByEmail(email).get().getIsLocked()){
            return new ResponseEntity<>("Locked account !!! Please ask admin to unlock your account", HttpStatus.BAD_REQUEST);
        }

        //delete resetPasswordToken if already present in db
        if (jwtService.findForgetPasswordToken(user.getEmail()).isPresent()) {
            jwtService.deleteForgetPasswordToken(user.getEmail());
        }
        String resetPasswordToken = jwtService.generateCustomToken(user.getEmail(), 1000L * 60 * 15);
        jwtService.storeForgetPasswordToken(resetPasswordToken, user.getEmail());

        forgetPasswordEmailService.sendResetPasswordEmail(user.getEmail(), resetPasswordToken);

        return new ResponseEntity<>("A link to reset password has been sent to your email", HttpStatus.OK);
    }

    public ResponseEntity<String> resetPassword(ResetPasswordCO resetPasswordCO) {
        //invalid token
        //expired token
        //validate token
        //token should be deleted
        //update password
        try {
            String forgetPasswordToken = resetPasswordCO.getForgetPasswordToken();
            String email = jwtService.extractEmail(forgetPasswordToken);

            if(!resetPasswordCO.getPassword().equals(resetPasswordCO.getConfirmPassword())){
                throw new ConfirmPasswordMismatchException("Password and confirm password should be same");
            }

            jwtService.isTokenValid(resetPasswordCO.getForgetPasswordToken(), email);

            jwtService.deleteForgetPasswordToken(email);

            User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Email not found"));

            user.setPassword(bCryptPasswordEncoder.encode(resetPasswordCO.getPassword()));
            user.setPasswordUpdateDate(Date.from(Instant.now()));
            userRepository.save(user);
            forgetPasswordEmailService.sendSuccessResetPasswordEmail(email);
            return new ResponseEntity<>("Your password has been reset successfully", HttpStatus.OK);
        } catch (BadCredentialsException ex) {
            return new ResponseEntity<>("Either email or password incorrect", HttpStatus.NOT_FOUND);
        } catch (InternalAuthenticationServiceException e) {
            return new ResponseEntity<>("Ether email or password incorrect", HttpStatus.NOT_FOUND);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
