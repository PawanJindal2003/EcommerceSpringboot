package com.Project.ecommerce.security.jwt;

import com.Project.ecommerce.entities.jwt.ActivationToken;
import com.Project.ecommerce.entities.jwt.ForgetPasswordToken;
import com.Project.ecommerce.entities.jwt.RefreshToken;
import com.Project.ecommerce.repositories.Jwt.ActivationTokenRepository;
import com.Project.ecommerce.repositories.Jwt.ForgetPasswordTokenRepository;
import com.Project.ecommerce.repositories.Jwt.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
public class JwtService {
    private ActivationTokenRepository activationTokenRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private ForgetPasswordTokenRepository forgetPasswordTokenRepository;

    @Autowired
    public JwtService(ActivationTokenRepository activationTokenRepository, RefreshTokenRepository refreshTokenRepository, ForgetPasswordTokenRepository forgetPasswordTokenRepository){
        this.activationTokenRepository = activationTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.forgetPasswordTokenRepository = forgetPasswordTokenRepository;
    }

    // Secret Key for signing the JWT. It should be kept private.
    private static final String SECRET = "TmV3U2VjcmV0S2V5Rm9ySldUU2lnbmluZ1B1cnBvc2VzMTIzNDU2Nzgjbhgtyret";

    // Generates a JWT token for the given email.
    public String generateToken(String email) {
        // Prepare claims for the token
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 3)) //
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    //access token
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean isAccessTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //refresh token
    public String generateRefreshToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(Duration.ofHours(60 * 60 * 24))))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public void storeRefreshToken(String refreshToken, String email){
        RefreshToken newRefreshToken = new RefreshToken(refreshToken, email, Instant.now());
        refreshTokenRepository.save(newRefreshToken);
    }
    public void deleteRefreshToken(String email){
        refreshTokenRepository.deleteByEmail(email);
    }

    //forgetPasswordToken
    public String generateForgetPasswordToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public void storeForgetPasswordToken(String forgetPasswordToken, String email){
        ForgetPasswordToken newForgetPasswordToken = new ForgetPasswordToken(forgetPasswordToken, email, Instant.now());
        forgetPasswordTokenRepository.save(newForgetPasswordToken);
    }
    public Optional<ForgetPasswordToken> findForgetPasswordToken(String email){
        return forgetPasswordTokenRepository.findByEmail(email);
    }
    public void deleteForgetPasswordToken(String email){
        forgetPasswordTokenRepository.deleteByEmail(email);
    }
    public boolean isForgetPasswordTokenValid(String forgetPasswordToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET.getBytes())
                    .build()
                    .parseClaimsJws(forgetPasswordToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void storeToken(String token, String email){
        ActivationToken jwtToken = new ActivationToken(token, email, Instant.now());
        activationTokenRepository.save(jwtToken);
    }

    public void deleteToken(String email){
        activationTokenRepository.deleteByEmail(email);
    }

    // Creates a signing key from the base64 encoded secret.
    //returns a Key object for signing the JWT.
    private Key getSignKey() {
        // Decode the base64 encoded secret key and return a Key object
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractEmail(String token) {
        // Extract and return the subject claim from the token
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        // Extract and return the expiration claim from the token
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        // Extract the specified claim using the provided function
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    //Extracts all claims from the JWT token.
    //return-> Claims object containing all claims.
    private Claims extractAllClaims(String token) {
        // Parse and return all claims from the token
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build().parseClaimsJws(token).getBody();
    }

    //Checks if the JWT token is expired.
    //return-> True if the token is expired, false otherwise.
    public Boolean isTokenExpired(String token) {
        // Check if the token's expiration time is before the current time
        return extractExpiration(token).before(new Date());
    }

    //Validates the JWT token against the UserDetails.
    //return-> True if the token is valid, false otherwise.

    public Boolean isTokenValid(String token, String email) {
        // Extract email from token
        final String extractedEmail = extractEmail(token);

        // Ensure token is not expired
        return (extractedEmail != null && !isTokenExpired(token));
    }

    public Boolean ifTokenPresent(String token){
        // Validate token structure and signature (will throw if invalid or expired)
        // without this 200 ok, as service code not able to sense these exceptions
        Jwts.parser()
                .setSigningKey(SECRET) // Replace with your actual key
                .parseClaimsJws(token); // Will throw MalformedJwtException, SignatureException, etc.

        return activationTokenRepository.findByJwtToken(token).isPresent();
    }

}