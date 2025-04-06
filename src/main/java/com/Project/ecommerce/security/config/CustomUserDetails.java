package com.Project.ecommerce.security.config;

import com.Project.ecommerce.entities.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;
    String userName = null;
    String password = null;
    Set<SimpleGrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        userName = user.getEmail();
        password = user.getPassword();
        authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().getAuthority()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
