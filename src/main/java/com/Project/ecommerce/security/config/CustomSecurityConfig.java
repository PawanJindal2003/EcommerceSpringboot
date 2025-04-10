package com.Project.ecommerce.security.config;

import com.Project.ecommerce.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class CustomSecurityConfig {
    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults()) // Apply CORS
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/forget-password/**",
                                "/api/auth/reset-password/**",
                                "/api/auth/login",
                                "/api/auth/admin/refresh-token",
                                "/api/auth/customer/refresh-token",
                                "/api/auth/seller/refresh-token",
                                "/api/auth/customer/register",
                                "/api/auth/customer/activate/**",
                                "/api/auth/customer/resend-activation-link",
                                "/api/auth/seller/register",
                                "/api/auth/seller/activate/**",
                                "/api/auth/seller/resend-activation-link/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/auth/logout",
                                "/api/admin/all-customers/**",
                                "/api/admin/all-sellers/**",
                                "/api/admin/activate/user/**",
                                "/api/admin/deactivate/user/**",
                                "/api/seller/me",
                                "/api/seller/update-profile",
                                "/api/seller/update-address/**",
                                "/api/seller/update-password",
                                "/api/customer/me",
                                "/api/customer/addresses"
                        ).authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Set session management to stateless
                .authenticationProvider(authenticationProvider()) // Register the authentication provider
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Add the JWT filter before processing the request
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authenticationProvider;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
