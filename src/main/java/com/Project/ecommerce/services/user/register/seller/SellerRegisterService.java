package com.Project.ecommerce.services.user.register.seller;

import com.Project.ecommerce.co.registration.SellerCO;
import com.Project.ecommerce.entities.user.Role;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.exceptions.customExceptions.ConfirmPasswordMismatchException;
import com.Project.ecommerce.exceptions.customExceptions.DuplicateCompanyException;
import com.Project.ecommerce.exceptions.customExceptions.DuplicateGSTException;
import com.Project.ecommerce.exceptions.customExceptions.EmailAlreadyExistsException;
import com.Project.ecommerce.repositories.user.RoleRepository;
import com.Project.ecommerce.repositories.user.SellerRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerRegisterService {
    private SellerRepository sellerRepository;
    private RoleRepository roleRepository;
    private SellerEmailService sellerEmailService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public SellerRegisterService(SellerRepository sellerRepository, RoleRepository roleRepository, SellerEmailService sellerEmailService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.sellerRepository = sellerRepository;
        this.sellerEmailService = sellerEmailService;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public String registerSeller(SellerCO sellerCO) throws MessagingException {
        if (sellerRepository.findByEmail(sellerCO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists, please enter a new email");
        }
        if (!sellerCO.getPassword().equals(sellerCO.getConfirmPassword())) {
            throw new ConfirmPasswordMismatchException("Confirm password does not match with password, please enter correct confirm password");
        }
        //not printing "enter a unique GST", security issue
        if(sellerRepository.findByGST(sellerCO.getGST()).isPresent()){
            throw new DuplicateGSTException("Invalid GST number");
        }
        if(sellerRepository.findByCompanyName(sellerCO.getCompanyName()).isPresent()){
            throw new DuplicateCompanyException("Company name already exists, please come up with a unique company name");
        }

        Role role = roleRepository.findByAuthority("Seller");
        Seller seller = new Seller();
        seller.setFirstName(sellerCO.getFirstName());
        seller.setMiddleName(sellerCO.getMiddleName());
        seller.setLastName(sellerCO.getLastName());
        seller.setGST(sellerCO.getGST());
        seller.setPassword(bCryptPasswordEncoder.encode(sellerCO.getPassword()));
        seller.setCompanyName(sellerCO.getCompanyName().toLowerCase());
        seller.setCompanyContact(sellerCO.getCompanyContact());
        seller.setEmail(sellerCO.getEmail());
        //send only one address
        seller.setAddresses(sellerCO.getAddresses());
        seller.setRole(role);

        sellerRepository.save(seller);

        sellerEmailService.sendActivationEmail(seller.getEmail());

        return "Registration completed, please wait until your account get approved";
    }
}
