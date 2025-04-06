package com.Project.ecommerce.security.config;


import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;
    @Autowired
    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
//        if (!user.get().getIsActive()){
//            throw new DisabledException("Account is not active");
//        }
//        if(user.get().getIsLocked()){
//            throw new LockedException("Account is locked, due to multiple incorrect login attempts");
//        }
        return user.map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("UserName not found: " + email));
    }

}
