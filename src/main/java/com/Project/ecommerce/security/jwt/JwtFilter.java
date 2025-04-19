package com.Project.ecommerce.security.jwt;

import com.Project.ecommerce.entities.jwt.RefreshToken;
import com.Project.ecommerce.repositories.Jwt.RefreshTokenRepository;
import com.Project.ecommerce.security.config.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final List<String> NON_PUBLIC_URLS = List.of(
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
            "/api/customer/addresses",
            "/api/customer/update-profile",
            "/api/customer/update-password",
            "/api/customer/add-address",
            "/api/customer/delete-address/**",
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
            "/api/category/customer/categories/**",
            "/api/product/add-product",
            "/api/product/add-product-variation");
    private JwtService jwtService;
    private CustomUserDetailsService customUserDetailsService;
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public JwtFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService, RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String token = null;
        String email = null;
        //retrieving cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        final AntPathMatcher pathMatcher = new AntPathMatcher();
        boolean isSecuredEndpoint = NON_PUBLIC_URLS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
        if (!isSecuredEndpoint) {
            filterChain.doFilter(request, response);
            return;
        } else if (token == null) {
            sendErrorResponse(response, List.of("Authentication Failed: JWT Missing"), HttpStatus.UNAUTHORIZED);
            return;
        }

        try {
            if(SecurityContextHolder.getContext().getAuthentication() == null){
                email = jwtService.extractEmail(token);
                if(email!=null){
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                    if(jwtService.isTokenValid(token, email)){
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        }
        catch (ExpiredJwtException e) {
            handleRefreshTokenIfExists(e, request, response, filterChain);
        }
        catch (SignatureException e){
            sendErrorResponse(response, List.of("JWT validation failed : Invalid signature"),HttpStatus.UNAUTHORIZED);
            return;
        }
        catch (MalformedJwtException e){
            sendErrorResponse(response, List.of("JWT validation failed : Missing part in JWT"), HttpStatus.UNAUTHORIZED);
            return;
        }
        catch (Exception e) {
            sendErrorResponse(response, List.of("Unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, List<String> errorMessages, HttpStatus httpStatus) throws IOException{
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String timeStamp = LocalDateTime.now().toString();

        String json = """
                {
                    "errorMessage" : %s,
                    "errorOccurred": %s,
                    "statusCode: %s"
                }
                """.formatted(toJsonArray(errorMessages), timeStamp, httpStatus.name());

        response.getWriter().write(json);
    }
    private String toJsonArray(List<String> list) {
        return list.stream()
                .map(msg -> "\"" + msg.replace("\"", "\\\"") + "\"")
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private void handleRefreshTokenIfExists(ExpiredJwtException e, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        String email = e.getClaims().getSubject();
        if (email != null) {
            RefreshToken refreshToken = refreshTokenRepository.findByEmail(email).orElseThrow(()->new RuntimeException("Refresh Token not found"));

            try{
                if (refreshToken != null && jwtService.isTokenValid(refreshToken.getToken(), refreshToken.getEmail())) {
                    String newAccessToken = jwtService.generateCustomToken(email, 1000L * 60 * 15);

                    //send in cookie
                    Cookie newCookie = new Cookie("accessToken", newAccessToken);
//                    newCookie.setMaxAge(1000 * 60 * 15);
                    newCookie.setPath("/");
                    newCookie.setHttpOnly(true);
                    response.addCookie(newCookie);

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    filterChain.doFilter(request, response);
                }
            }
            catch (ExpiredJwtException ex) {
                sendErrorResponse(response, List.of("Session expired. Please login again."), HttpStatus.UNAUTHORIZED);
            } catch (ServletException ex) {
                throw new RuntimeException(ex);
            }
        }

    }
}

