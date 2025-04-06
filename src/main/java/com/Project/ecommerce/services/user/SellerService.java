package com.Project.ecommerce.services.user;

import com.Project.ecommerce.co.SellerCO;
import com.Project.ecommerce.entities.user.Role;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.ConfirmPasswordMismatch;
import com.Project.ecommerce.exceptions.customExceptions.EmailAlreadyExistsException;
import com.Project.ecommerce.repositories.user.RoleRepository;
import com.Project.ecommerce.repositories.user.SellerRepository;
import com.Project.ecommerce.repositories.user.UserRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SellerService {
    private UserRepository userRepository;
    private SellerRepository sellerRepository;
    private RoleRepository roleRepository;
    private JwtService jwtService;
    private EmailService emailService;

    @Autowired
    public SellerService(SellerRepository sellerRepository, RoleRepository roleRepository, JwtService jwtService) {
        this.sellerRepository = sellerRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
    }

    public User registerSeller(SellerCO sellerCO) throws MessagingException {
        if (sellerRepository.findByEmail(sellerCO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists, please enter a new email");
        }
        if (!sellerCO.getPassword().equals(sellerCO.getConfirmPassword())) {
            throw new ConfirmPasswordMismatch("Confirm password does not match with password, please enter correct confirm password");
        }

        Role role = roleRepository.findByAuthority("Seller");
        Seller seller = new Seller();
        seller.setFirstName(sellerCO.getFirstName());
        seller.setMiddleName(sellerCO.getMiddleName());
        seller.setLastName(sellerCO.getLastName());
        seller.setGST(sellerCO.getGST());
        seller.setCompanyName(sellerCO.getCompanyName());
        seller.setCompanyContact(sellerCO.getCompanyContact());
        seller.setRole(role);

        sellerRepository.save(seller);

        String generatedToken = jwtService.generateToken(seller.getEmail());
        jwtService.storeToken(generatedToken, seller.getEmail());
        emailService.sendActivationEmail(seller.getEmail(), generatedToken);

        return seller;
    }
}
