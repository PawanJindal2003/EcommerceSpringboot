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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
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
                                "/api/admin/**",
                                "/api/seller/me",
                                "/api/seller/update-profile",
                                "/api/seller/update-address/**",
                                "/api/seller/update-password",
                                "/api/customer/me",
                                "/api/customer/addresses",
                                "/api/customer/update-profile",
                                "/api/customer/update-password",
                                "/api/customer/add-address",
                                "/api/customer/delete-address/**",
                                "/api/customer/update-address",
                                "/api/customer/update-address",
                                "/api/category/add-categoryMetaDataField",
                                "/api/category/all-categoryMetaDataField/**",
                                "/api/category/add-category",
                                "/api/category/category/**",
                                "/api/category/all-categories/**",
                                "/api/category/update-category",
                                "/api/category/add-metadata-category",
                                "/api/category/update-metadata-category",
                                "/api/category/seller/all-categories",
                                "/api/category/customer/**",
                                "/api/product/add-product",
                                "/api/product/add-product-variation",
                                "/api/product/**",
                                "/api/product/customer/**",
                                "/api/product/admin/**"
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
