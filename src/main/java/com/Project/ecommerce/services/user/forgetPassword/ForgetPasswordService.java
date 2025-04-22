package com.Project.ecommerce.services.user.forgetPassword;

import com.Project.ecommerce.co.forgetPassword.ResetPasswordCO;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.ConfirmPasswordMismatchException;
import com.Project.ecommerce.exceptions.customExceptions.InactiveResourceException;
import com.Project.ecommerce.exceptions.customExceptions.LockedAccountException;
import com.Project.ecommerce.exceptions.customExceptions.ResourceNotFoundException;
import com.Project.ecommerce.repositories.user.UserRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ForgetPasswordService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ForgetPasswordEmailService forgetPasswordEmailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MessageSource messageSource;

    public String sendResetPasswordMail(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale())
                ));

        //throwing error response if an inactive user trying to forget password
        if (!user.getIsActive()) {
            throw new InactiveResourceException(messageSource.getMessage("user.inactive", null, LocaleContextHolder.getLocale()));
        }

        //throwing error response if a locked user trying to forget password
        if (user.getIsLocked()) {
            throw new LockedAccountException(messageSource.getMessage("user.locked", null, LocaleContextHolder.getLocale()));
        }

        //delete resetPasswordToken if already present in db
        jwtService.findForgetPasswordToken(user.getEmail()).ifPresent(token -> jwtService.deleteForgetPasswordToken(user.getEmail()));

        String resetPasswordToken = jwtService.generateCustomToken(user.getEmail(), 1000L * 60 * 15);
        jwtService.storeForgetPasswordToken(resetPasswordToken, user.getEmail());

        forgetPasswordEmailService.sendResetPasswordEmail(user.getEmail(), resetPasswordToken);

        return messageSource.getMessage("password.reset.link.sent", null, LocaleContextHolder.getLocale());
    }

    public String resetPassword(ResetPasswordCO resetPasswordCO) {
        //invalid token
        //expired token
        //validate token
        //token should be deleted
        //update password
        try {
            String forgetPasswordToken = resetPasswordCO.getForgetPasswordToken();
            String email = jwtService.extractEmail(forgetPasswordToken);

            if (!resetPasswordCO.getPassword().equals(resetPasswordCO.getConfirmPassword())) {
                throw new ConfirmPasswordMismatchException(messageSource.getMessage("password.confirm.mismatch", null, LocaleContextHolder.getLocale()));
            }

            jwtService.isTokenValid(forgetPasswordToken, email);
            jwtService.deleteForgetPasswordToken(email);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale())
                    ));

            user.setPassword(bCryptPasswordEncoder.encode(resetPasswordCO.getPassword()));
            user.setPasswordUpdateDate(Date.from(Instant.now()));
            userRepository.save(user);

            forgetPasswordEmailService.sendSuccessResetPasswordEmail(email);

            return messageSource.getMessage("password.reset.success", null, LocaleContextHolder.getLocale());

        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException(
                    messageSource.getMessage("auth.invalid.credentials", null, LocaleContextHolder.getLocale())
            );
        } catch (InternalAuthenticationServiceException e) {
            throw new InternalAuthenticationServiceException(
                    messageSource.getMessage("auth.invalid.credentials", null, LocaleContextHolder.getLocale())
            );
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
