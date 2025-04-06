package com.Project.ecommerce.services.user.customer.login;

import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerLoginService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public CustomerLoginService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void validateLogin(String email, String encodedPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
            new UsernameNotFoundException("User not found with this email"));

        if(!bCryptPasswordEncoder.matches(encodedPassword, user.getPassword())){
            throw new BadCredentialsException("Incorrect password");
        }

        if (user.getIsExpired()) {
            throw new CredentialsExpiredException("Password is expired");
        }
    }
}
