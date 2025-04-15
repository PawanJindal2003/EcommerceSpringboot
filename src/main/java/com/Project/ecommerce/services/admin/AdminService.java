package com.Project.ecommerce.services.admin;

import com.Project.ecommerce.dto.admin.GetAllCustomersDTO;
import com.Project.ecommerce.dto.admin.GetAllSellersDTO;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.UserNotFoundException;
import com.Project.ecommerce.repositories.user.CustomerRepository;
import com.Project.ecommerce.repositories.user.SellerRepository;
import com.Project.ecommerce.repositories.user.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private CustomerRepository customerRepository;
    private SellerRepository sellerRepository;
    private UserRepository userRepository;
    private AdminEmailService adminEmailService;
    private MessageSource messageSource;

    @Autowired
    public AdminService(CustomerRepository customerRepository, SellerRepository sellerRepository, UserRepository userRepository, AdminEmailService adminEmailService, MessageSource messageSource) {
        this.customerRepository = customerRepository;
        this.sellerRepository = sellerRepository;
        this.userRepository = userRepository;
        this.adminEmailService = adminEmailService;
        this.messageSource = messageSource;
    }

    public List<GetAllCustomersDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).stream().map(customer -> {
            GetAllCustomersDTO customerDTO = new GetAllCustomersDTO();
            customerDTO.setId(customer.getId());
            customerDTO.setFirstName(customer.getFirstName());
            customerDTO.setMiddleName(customer.getMiddleName());
            customerDTO.setLastName(customer.getLastName());
            customerDTO.setEmail(customer.getEmail());
            customerDTO.setIsActive(customer.getIsActive());
            return customerDTO;
        }).collect(Collectors.toList());
    }

    public List<GetAllCustomersDTO> getAllCustomersByEmail(String email, Pageable pageable) {
        return customerRepository.findAllByEmail(email, pageable).stream().map(customer -> {
            GetAllCustomersDTO customerDTO = new GetAllCustomersDTO();
            customerDTO.setId(customer.getId());
            customerDTO.setFirstName(customer.getFirstName());
            customerDTO.setMiddleName(customer.getMiddleName());
            customerDTO.setLastName(customer.getLastName());
            customerDTO.setEmail(customer.getEmail());
            customerDTO.setIsActive(customer.getIsActive());
            return customerDTO;
        }).collect(Collectors.toList());
    }

    public List<GetAllSellersDTO> getAllSellers(Pageable pageable) {
        return sellerRepository.findAll(pageable).stream().map(seller -> {
            GetAllSellersDTO sellersDTO = new GetAllSellersDTO();
            sellersDTO.setId(seller.getId());
            sellersDTO.setFirstName(seller.getFirstName());
            sellersDTO.setMiddleName(seller.getMiddleName());
            sellersDTO.setLastName(seller.getLastName());
            sellersDTO.setEmail(seller.getEmail());
            sellersDTO.setIsActive(seller.getIsActive());
            return sellersDTO;
        }).collect(Collectors.toList());
    }

    public List<GetAllSellersDTO> getAllSellersByEmail(String email, Pageable pageable) {
        return sellerRepository.findAllByEmail(email, pageable).stream().map(seller -> {
            GetAllSellersDTO sellersDTO = new GetAllSellersDTO();
            sellersDTO.setId(seller.getId());
            sellersDTO.setFirstName(seller.getFirstName());
            sellersDTO.setMiddleName(seller.getMiddleName());
            sellersDTO.setLastName(seller.getLastName());
            sellersDTO.setEmail(seller.getEmail());
            sellersDTO.setIsActive(seller.getIsActive());
            sellersDTO.setCompanyName(seller.getCompanyName());
            sellersDTO.setCompanyAddress(seller.getAddresses().get(0));
            sellersDTO.setCompanyContact(seller.getCompanyContact());
            return sellersDTO;
        }).collect(Collectors.toList());
    }

    public String activateDeactivateUser(String userId, Boolean action) throws MethodArgumentTypeMismatchException, MessagingException {
        // id validation, in global exception handler
        try{
            UUID uuid = UUID.fromString(userId);
        }
        catch (IllegalArgumentException e){
            return messageSource.getMessage("user.invalid.id", null, LocaleContextHolder.getLocale());
        }
        //user not found
        User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException(messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale())));

        //if action = true, activate user
        if(action){
            //if user is deactivated
            if(!user.getIsActive()){
                user.setIsActive(true);
                //save in db
                userRepository.save(user);
                //trigger email
                adminEmailService.sendActivationEmail(user.getEmail());
                return messageSource.getMessage("user.account.activated", null, LocaleContextHolder.getLocale());
            }
            else{
                return messageSource.getMessage("user.account.already.activated", null, LocaleContextHolder.getLocale());
            }
        }
        //if action = false, deactivate user
        else{
            //if user is activated
            if(user.getIsActive()){
                user.setIsActive(false);
                //save in db
                userRepository.save(user);
                //trigger email
                adminEmailService.sendDeactivationEmail(user.getEmail());
                return messageSource.getMessage("user.account.deactivated", null, LocaleContextHolder.getLocale());
            }
            else{
                return messageSource.getMessage("user.account.already.deactivated", null, LocaleContextHolder.getLocale());
            }
        }
    }
}
