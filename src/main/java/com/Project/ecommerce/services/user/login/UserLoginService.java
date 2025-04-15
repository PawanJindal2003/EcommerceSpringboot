package com.Project.ecommerce.services.user.login;

import com.Project.ecommerce.co.login.UserCO;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.InactiveUserException;
import com.Project.ecommerce.exceptions.customExceptions.UserNotFoundException;
import com.Project.ecommerce.repositories.Jwt.RefreshTokenRepository;
import com.Project.ecommerce.repositories.user.UserRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class UserLoginService {
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @Autowired
    public UserLoginService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public List<String> loginUser(@Valid @RequestBody UserCO userCO, HttpServletResponse response) {
        User user = userRepository.findByEmail(userCO.getEmail()).orElseThrow(()->new UserNotFoundException("Email not found"));
        if (!user.getIsActive()) {
            throw new InactiveUserException("Please activate your account");
        }

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userCO.getEmail(), userCO.getPassword())
            );
        }
        catch (BadCredentialsException e){
            multipleLoginAttempts(userCO.getEmail());
            throw e;
        }

        String accessToken = jwtService.generateCustomToken(userCO.getEmail(), 1000L * 5);

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
        return List.of("Login successful", accessToken);
    }

    public void multipleLoginAttempts(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Email not found"));
        int attempts = user.getInvalidAttemptCount() + 1;
        user.setInvalidAttemptCount(attempts);

        if (attempts >= 3) {
            user.setIsLocked(true);
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

        return "Logged out successfully";
    }
}
