package com.Project.ecommerce.services.admin;

import com.Project.ecommerce.dto.admin.GetAllCustomersDTO;
import com.Project.ecommerce.dto.admin.GetAllSellersDTO;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.ResourceNotFoundException;
import com.Project.ecommerce.repositories.user.CustomerRepository;
import com.Project.ecommerce.repositories.user.SellerRepository;
import com.Project.ecommerce.repositories.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final AdminEmailService adminEmailService;
    private final MessageSource messageSource;

    public List<GetAllCustomersDTO> getAllCustomers(Pageable pageable) {
        logger.info("Fetching all customers with pagination: {}", pageable);
        return customerRepository.findAll(pageable).stream().map(customer -> {
            GetAllCustomersDTO customerDTO = new GetAllCustomersDTO();
            customerDTO.setId(customer.getId());
            if (customer.getMiddleName() != null) {
                customerDTO.setName(customer.getFirstName() + " " + customer.getMiddleName() + " " + customer.getLastName());
            } else {
                customerDTO.setName(customer.getFirstName() + " " + customer.getLastName());
            }
            customerDTO.setEmail(customer.getEmail());
            customerDTO.setIsActive(customer.getIsActive());
            return customerDTO;
        }).collect(Collectors.toList());
    }

    public List<GetAllCustomersDTO> getAllCustomersByEmail(String email, Pageable pageable) {
        logger.info("Fetching customers with email: {} and pagination: {}", email, pageable);

        return customerRepository.findAllByEmail(email, pageable).stream().map(customer -> {
            GetAllCustomersDTO customerDTO = new GetAllCustomersDTO();
            customerDTO.setId(customer.getId());
            if (customer.getMiddleName() != null) {
                customerDTO.setName(customer.getFirstName() + " " + customer.getMiddleName() + " " + customer.getLastName());
            } else {
                customerDTO.setName(customer.getFirstName() + " " + customer.getLastName());
            }
            customerDTO.setEmail(customer.getEmail());
            customerDTO.setIsActive(customer.getIsActive());
            return customerDTO;
        }).collect(Collectors.toList());
    }

    public List<GetAllSellersDTO> getAllSellers(Pageable pageable) {
        logger.info("Fetching all sellers with pagination: {}", pageable);
        return sellerRepository.findAll(pageable).stream().map(seller -> {
            GetAllSellersDTO sellersDTO = new GetAllSellersDTO();
            sellersDTO.setId(seller.getId());
            if (seller.getMiddleName() != null) {
                sellersDTO.setName(seller.getFirstName() + " " + seller.getMiddleName() + " " + seller.getLastName());
            } else {
                sellersDTO.setName(seller.getFirstName() + " " + seller.getLastName());
            }
            sellersDTO.setEmail(seller.getEmail());
            sellersDTO.setIsActive(seller.getIsActive());
            sellersDTO.setCompanyName(seller.getCompanyName());
            sellersDTO.setCompanyAddress(seller.getAddresses().get(0));
            sellersDTO.setCompanyContact(seller.getCompanyContact());
            return sellersDTO;
        }).collect(Collectors.toList());
    }

    public List<GetAllSellersDTO> getAllSellersByEmail(String email, Pageable pageable) {
        logger.info("Fetching sellers with email: {} and pagination: {}", email, pageable);
        return sellerRepository.findAllByEmail(email, pageable).stream().map(seller -> {
            GetAllSellersDTO sellersDTO = new GetAllSellersDTO();
            sellersDTO.setId(seller.getId());
            if (seller.getMiddleName() != null) {
                sellersDTO.setName(seller.getFirstName() + " " + seller.getMiddleName() + " " + seller.getLastName());
            } else {
                sellersDTO.setName(seller.getFirstName() + " " + seller.getLastName());
            }
            sellersDTO.setEmail(seller.getEmail());
            sellersDTO.setIsActive(seller.getIsActive());
            sellersDTO.setCompanyName(seller.getCompanyName());
            sellersDTO.setCompanyAddress(seller.getAddresses().get(0));
            sellersDTO.setCompanyContact(seller.getCompanyContact());
            return sellersDTO;
        }).collect(Collectors.toList());
    }

    public String activateDeactivateUser(String userId, Boolean action) throws MethodArgumentTypeMismatchException, MessagingException {
        logger.info("Received request to {} user with ID: {}", action ? "activate" : "deactivate", userId);

        // id validation, in global exception handler
        try {
            UUID uuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            return messageSource.getMessage("user.invalid.id", null, LocaleContextHolder.getLocale());
        }
        //user not found
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale())));

        //if action = true, activate user
        if (action) {
            //if user is deactivated
            if (!user.getIsActive()) {
                user.setIsActive(true);
                //save in db
                userRepository.save(user);
                //trigger email
                adminEmailService.sendActivationEmail(user.getEmail());
                logger.info("User with ID {} activated successfully.", userId);
                return messageSource.getMessage("user.account.activated", null, LocaleContextHolder.getLocale());
            } else {
                logger.debug("User with ID {} is already active.", userId);
                return messageSource.getMessage("user.account.already.activated", null, LocaleContextHolder.getLocale());
            }
        }
        //if action = false, deactivate user
        else {
            //if user is activated
            if (user.getIsActive()) {
                user.setIsActive(false);
                //save in db
                userRepository.save(user);
                //trigger email
                adminEmailService.sendDeactivationEmail(user.getEmail());
                logger.info("User with ID {} deactivated successfully.", userId);
                return messageSource.getMessage("user.account.deactivated", null, LocaleContextHolder.getLocale());
            } else {
                logger.debug("User with ID {} is already deactivated.", userId);
                return messageSource.getMessage("user.account.already.deactivated", null, LocaleContextHolder.getLocale());
            }
        }
    }
}
