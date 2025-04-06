package com.Project.ecommerce.services.user.customer.login;

import com.Project.ecommerce.co.login.CustomerCO;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.InactiveUserException;
import com.Project.ecommerce.exceptions.customExceptions.UserNotFoundException;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class CustomerLoginService {
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private RedisTokenService redisService;

    @Autowired
    public CustomerLoginService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService, RedisTokenService redisService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.redisService = redisService;
    }

    public ResponseEntity<String> loginCustomer(@Valid @RequestBody CustomerCO customerCO, HttpServletResponse response) {
        try {
            if(!userRepository.findByEmail(customerCO.getEmail()).get().getIsActive()){
                throw new InactiveUserException("Please activate your account");
            }
//            customerLoginService.validateLogin(customerCO.getEmail(), customerCO.getPassword());
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(customerCO.getEmail(), customerCO.getPassword())
            );

            String token = jwtService.generateAccessToken(customerCO.getEmail());

            Cookie cookie = new Cookie("loginToken", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);//for https
            cookie.setPath("/");
            cookie.setMaxAge(60 * 15);
            response.addCookie(cookie);

            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        } catch (BadCredentialsException ex) {
            multipleLoginAttempts(customerCO.getEmail());
            return new ResponseEntity<>("Either email or password incorrect", HttpStatus.NOT_FOUND);
        }catch (InternalAuthenticationServiceException e) {
            return new ResponseEntity<>("Ether email or password incorrect", HttpStatus.NOT_FOUND);
        }
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
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("loginToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            redisService.revokeToken(token);
        }

        Cookie cookie = new Cookie("loginToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire it immediately
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out");
    }
}
