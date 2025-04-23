package com.Project.ecommerce.services.user.login;

import com.Project.ecommerce.co.login.UserCO;
import com.Project.ecommerce.entities.jwt.BlacklistedAccessToken;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.ExpiredPasswordException;
import com.Project.ecommerce.exceptions.customExceptions.InactiveResourceException;
import com.Project.ecommerce.exceptions.customExceptions.ResourceNotFoundException;
import com.Project.ecommerce.repositories.Jwt.BlacklistedAccessTokenRepository;
import com.Project.ecommerce.repositories.user.UserRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLoginService {
    private static final Logger logger = LoggerFactory.getLogger(UserLoginService.class);
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MessageSource messageSource;
    private final BlacklistedAccessTokenRepository blacklistedAccessTokenRepository;

    public List<String> loginUser(@Valid @RequestBody UserCO userCO, HttpServletResponse response) {
        logger.info("Login attempt for user with email: {}", userCO.getEmail());
        User user = userRepository.findByEmail(userCO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale())));

        if(user.getPasswordUpdateDate().before(Date.from(Instant.now().minus(60, ChronoUnit.DAYS)))){
            logger.warn("Password expired for user with email: {}", userCO.getEmail());
            throw new ExpiredPasswordException(messageSource.getMessage("user.account.expired", null, LocaleContextHolder.getLocale()));
        }
        if (!user.getIsActive()) {
            logger.warn("User account is inactive for email: {}", userCO.getEmail());
            throw new InactiveResourceException(messageSource.getMessage("user.inactive", null, LocaleContextHolder.getLocale()));
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userCO.getEmail(), userCO.getPassword())
            );
        }
        catch (BadCredentialsException e){
            logger.warn("Failed login attempt for user with email: {}", userCO.getEmail());
            multipleLoginAttempts(userCO.getEmail());
            throw e;
        }

        String accessToken = jwtService.generateCustomToken(userCO.getEmail(), 1000L * 60 * 15);

        //deleting previous refresh token while logging in
        jwtService.deleteRefreshToken(userCO.getEmail());
        String refreshToken = jwtService.generateCustomToken(userCO.getEmail(), 1000L * 60 * 60 * 24);
        //saving the newly generated token
        jwtService.storeRefreshToken(refreshToken, userCO.getEmail());

        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);//for https
        accessCookie.setPath("/");
        response.addCookie(accessCookie);

        user.setInvalidAttemptCount(0);
        userRepository.save(user);
        logger.info("User login successful for email: {}", userCO.getEmail());


        return List.of(messageSource.getMessage("user.login.success", null, LocaleContextHolder.getLocale()), accessToken);
    }

    public void multipleLoginAttempts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale())));

        int attempts = user.getInvalidAttemptCount() + 1;
        user.setInvalidAttemptCount(attempts);

        if (attempts >= 3) {
            user.setIsLocked(true);
            logger.warn("User account locked due to multiple failed login attempts for email: {}", email);
        }

        if(!user.getRole().getAuthority().equals("ADMIN")){
            userRepository.save(user);
        }
    }

    public String logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if(accessToken!=null){
            //add this token in blacklisted tokens
            BlacklistedAccessToken blacklistedAccessToken = new BlacklistedAccessToken(accessToken, Instant.now().plus(15, ChronoUnit.MINUTES));
            blacklistedAccessTokenRepository.save(blacklistedAccessToken);

            //deleting refresh token while user logouts
            String email = jwtService.extractEmail(accessToken);
            jwtService.deleteRefreshToken(email);

            //clearing the cookie
            Cookie cookie = new Cookie("accessToken", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            logger.info("User logged out successfully for email: {}", email);
        }
        else{
            logger.warn("No access token found in cookies during logout");
        }

        return messageSource.getMessage("user.logout.success", null, LocaleContextHolder.getLocale());
    }
}
