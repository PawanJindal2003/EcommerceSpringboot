package com.Project.ecommerce.security.jwt;

import com.Project.ecommerce.security.config.CustomUserDetailsService;
import com.Project.ecommerce.security.redis.RedisTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private ApplicationContext applicationContext;
    private RedisTokenService redisTokenService;

    @Autowired
    public JwtFilter(JwtService jwtService, ApplicationContext applicationContext, RedisTokenService redisTokenService){
        this.jwtService = jwtService;
        this.applicationContext = applicationContext;
        this.redisTokenService = redisTokenService;
    }

    // Method to lazily fetch the UserService bean from the ApplicationContext
    // This is done to avoid Circular Dependency issues
    private CustomUserDetailsService getUserService() {
        return applicationContext.getBean(CustomUserDetailsService.class);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extracting token from the request header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extracting the token from the Authorization header
            token = authHeader.substring(7);
            // Extracting username from the token
            email = jwtService.extractEmail(token);
        }

        //retrieving cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("loginToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // If username is extracted and there is no authentication in the current SecurityContext
        if (email != null && jwtService.isAccessTokenValid(token) && redisTokenService.isTokenValid(token)) {
            // Loading UserDetails by username extracted from the token
            UserDetails userDetails = getUserService().loadUserByUsername(email);

            // Validating the token with loaded UserDetails
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // Creating an authentication token using UserDetails
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // Setting authentication details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Setting the authentication token in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Proceeding with the filter chain
        filterChain.doFilter(request, response);
    }
}
