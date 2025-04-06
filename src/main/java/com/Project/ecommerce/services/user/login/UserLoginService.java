package com.Project.ecommerce.services.user.customer.login;

import com.Project.ecommerce.co.login.UserCO;
import com.Project.ecommerce.dto.login.UserDTO;
import com.Project.ecommerce.entities.jwt.RefreshToken;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.InactiveUserException;
import com.Project.ecommerce.exceptions.customExceptions.UserNotFoundException;
import com.Project.ecommerce.repositories.Jwt.RefreshTokenRepository;
import com.Project.ecommerce.repositories.user.UserRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import com.Project.ecommerce.security.redis.RedisTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class UserLoginService {
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private RedisTokenService redisService;
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public UserLoginService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService, RedisTokenService redisService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.redisService = redisService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public ResponseEntity<UserDTO> loginCustomer(@Valid @RequestBody UserCO userCO, HttpServletResponse response) {
        try {
            if(!userRepository.findByEmail(userCO.getEmail()).get().getIsActive()){
                throw new InactiveUserException("Please activate your account");
            }
//            customerLoginService.validateLogin(customerCO.getEmail(), customerCO.getPassword());
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userCO.getEmail(), userCO.getPassword())
            );

            String accessToken = jwtService.generateAccessToken(userCO.getEmail());

            //deleting previous refresh token while logging in
            jwtService.deleteRefreshToken(userCO.getEmail());
            String refreshToken = jwtService.generateRefreshToken(userCO.getEmail());
            //saving the newly generated token
            jwtService.storeRefreshToken(refreshToken, userCO.getEmail());

            Cookie accessCookie = new Cookie("loginToken", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(true);//for https
            accessCookie.setPath("/");
            accessCookie.setMaxAge(60 * 15);
            response.addCookie(accessCookie);

            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);//for https
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(refreshCookie);

            UserDTO dto = new UserDTO(accessToken, refreshToken,"Login successful");
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (BadCredentialsException ex) {
            multipleLoginAttempts(userCO.getEmail());
            return new ResponseEntity<>(new UserDTO(null, null,"Either email or password incorrect"), HttpStatus.NOT_FOUND);
        }catch (InternalAuthenticationServiceException e) {
            return new ResponseEntity<>(new UserDTO(null, null,"Ether email or password incorrect"), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<UserDTO> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;

        // Example: Get from cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null) {
            return new ResponseEntity<>(new UserDTO(null, null, "Refresh token missing"), HttpStatus.UNAUTHORIZED);
        }

        String email = jwtService.extractEmail(refreshToken);
        if (!jwtService.isTokenValid(refreshToken, email)) {
            return new ResponseEntity<>(new UserDTO(null, null, "Invalid or expired refresh token"), HttpStatus.UNAUTHORIZED);
        }

        Optional<RefreshToken> storedToken = refreshTokenRepository.findByEmail(userRepository.findByEmail(email).get().getEmail());
        if (storedToken.isEmpty() || !storedToken.get().getToken().equals(refreshToken)) {
            return new ResponseEntity<>(new UserDTO(null, null, "Refresh token mismatch"), HttpStatus.UNAUTHORIZED);
        }

        String newAccessToken = jwtService.generateAccessToken(email);
        UserDTO dto = new UserDTO(newAccessToken, refreshToken, "Token refreshed successfully");
        return ResponseEntity.ok(dto);
    }


    public void multipleLoginAttempts(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("Email not found"));
        int attempts = user.getInvalidAttemptCount() + 1;
        user.setInvalidAttemptCount(attempts);

        if(attempts>=3){
            user.setIsLocked(true);
        }

        userRepository.save(user);
    }

    public ResponseEntity<String> logoutCustomer(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("loginToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        // revoking access token from redis if present
        if (accessToken != null) {
            redisService.revokeToken(accessToken);
        }

        //deleting refresh token while user logouts
        String email = jwtService.extractEmail(accessToken);
        jwtService.deleteRefreshToken(email);

        //clearing the cookie
        Cookie cookie = new Cookie("loginToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out");
    }
}
