package com.Project.ecommerce.services.user;

import com.Project.ecommerce.co.CustomerCO;
import com.Project.ecommerce.entities.user.Customer;
import com.Project.ecommerce.entities.user.Role;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.ConfirmPasswordMismatch;
import com.Project.ecommerce.exceptions.customExceptions.EmailAlreadyExistsException;
import com.Project.ecommerce.repositories.user.RoleRepository;
import com.Project.ecommerce.repositories.user.CustomerRepository;
import com.Project.ecommerce.repositories.user.UserRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SignatureException;
import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public CustomerService(CustomerRepository customerRepository, RoleRepository roleRepository){
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
    }

    public String registerCustomer(CustomerCO customerCO) throws MessagingException {
        if(customerRepository.findByEmail(customerCO.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("Email already exists, please enter a new email");
        }
        if(!customerCO.getPassword().equals(customerCO.getConfirmPassword())){
            throw new ConfirmPasswordMismatch("Confirm password does not match with password, please enter correct confirm password");
        }

        Role role = roleRepository.findByAuthority("Customer");
        Customer customer = new Customer();
        customer.setFirstName(customerCO.getFirstName());
        customer.setMiddleName(customerCO.getMiddleName());
        customer.setLastName(customerCO.getLastName());

        customer.setEmail(customerCO.getEmail());
        customer.setPassword(bCryptPasswordEncoder.encode(customerCO.getPassword()));
        customer.setCustomerContact(customerCO.getCustomerContact());
        customer.setRole(role);

        customerRepository.save(customer);
        //token generation and saving in db
        String generatedToken = jwtService.generateToken(customer.getEmail());
        jwtService.storeToken(customer.getEmail());
        emailService.sendActivationEmail(customer.getEmail(), generatedToken);
        return "Registration successful. Please check your email to activate your account.";
    }

    public String activateCustomer(String token) throws MessagingException {
        try {
            String extractedEmail = validateAndExtractEmail(token);

            User customer = userRepository.findByEmail(extractedEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find email"));

            customer.setIsActive(true);
            userRepository.save(customer);
            //deleting useless token from db
            jwtService.deleteToken(extractedEmail);

            return "Account activated successfully!";
        }
        catch (ExpiredJwtException e) {
            jwtService.deleteToken(e.getClaims().getSubject());
            return resendActivationEmail(e.getClaims().getSubject());
        }
    }

    private String validateAndExtractEmail(String token) {
        String extractedEmail = jwtService.extractEmail(token);

        User customer = userRepository.findByEmail(extractedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find email"));

        if (!jwtService.isTokenValid(token, customer.getEmail())) {
            throw new IllegalArgumentException("Invalid token, account activation failed.");
        }

        return extractedEmail;
    }


    public String resendActivationEmail(String extractedEmail) throws MessagingException {
        User customer = userRepository.findByEmail(extractedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find email"));

        String newToken = jwtService.generateToken(customer.getEmail());
        emailService.sendActivationEmail(customer.getEmail(), newToken);

        return "Token expired. A new activation link has been sent to your email.";
    }
}
