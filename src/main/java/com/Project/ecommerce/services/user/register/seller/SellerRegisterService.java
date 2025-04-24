package com.Project.ecommerce.services.user.register.seller;

import com.Project.ecommerce.co.registration.SellerCO;
import com.Project.ecommerce.entities.user.Role;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.exceptions.customExceptions.ConfirmPasswordMismatchException;
import com.Project.ecommerce.exceptions.customExceptions.DuplicateResourceException;
import com.Project.ecommerce.exceptions.customExceptions.EmailAlreadyExistsException;
import com.Project.ecommerce.exceptions.customExceptions.ExcessAddressesException;
import com.Project.ecommerce.repositories.user.RoleRepository;
import com.Project.ecommerce.repositories.user.SellerRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerRegisterService {
    private static final Logger logger = LoggerFactory.getLogger(SellerRegisterService.class);
    private final SellerRepository sellerRepository;
    private final RoleRepository roleRepository;
    private final SellerEmailService sellerEmailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MessageSource messageSource;

    public String registerSeller(SellerCO sellerCO) throws MessagingException {
        logger.info("Received registration request for seller with email: {}", sellerCO.getEmail());
        if (sellerRepository.findByEmail(sellerCO.getEmail()).isPresent()) {
            logger.warn("Attempt to register seller with already existing email: {}", sellerCO.getEmail());
            throw new EmailAlreadyExistsException(messageSource.getMessage("seller.email.already.exists", null, LocaleContextHolder.getLocale()));
        }
        if (!sellerCO.getPassword().equals(sellerCO.getConfirmPassword())) {
            logger.warn("Password mismatch for email: {}", sellerCO.getEmail());
            throw new ConfirmPasswordMismatchException(messageSource.getMessage("seller.confirm.password.mismatch", null, LocaleContextHolder.getLocale()));
        }
        //not printing "enter a unique GST", security issue
        if(sellerRepository.findByGST(sellerCO.getGST()).isPresent()){
            logger.warn("Attempt to register seller with duplicate GST: {}", sellerCO.getGST());
            throw new DuplicateResourceException(messageSource.getMessage("seller.invalid.gst", null, LocaleContextHolder.getLocale()));
        }
        if(sellerRepository.findByCompanyName(sellerCO.getCompanyName()).isPresent()){
            logger.warn("Attempt to register seller with duplicate company name: {}", sellerCO.getCompanyName());
            throw new DuplicateResourceException(messageSource.getMessage("seller.duplicate.company.name", null, LocaleContextHolder.getLocale()));
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
        if(sellerCO.getAddresses().size()>1){
            throw new ExcessAddressesException("A seller can have only one address");
        }
        seller.setAddresses(sellerCO.getAddresses());
        seller.setRole(role);

        sellerRepository.save(seller);

        logger.info("Successfully registered seller with email: {}", sellerCO.getEmail());

        sellerEmailService.sendActivationEmail(seller.getEmail());
        logger.info("Activation email sent to seller with email: {}", sellerCO.getEmail());

        return messageSource.getMessage("seller.register.attempt", null, LocaleContextHolder.getLocale());
    }
}