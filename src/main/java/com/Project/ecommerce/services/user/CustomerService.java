package com.Project.ecommerce.services.user;

import com.Project.ecommerce.co.CustomerCO;
import com.Project.ecommerce.entities.user.Customer;
import com.Project.ecommerce.entities.user.Role;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.ConfirmPasswordMismatch;
import com.Project.ecommerce.exceptions.customExceptions.EmailAlreadyExistsException;
import com.Project.ecommerce.exceptions.customExceptions.UserNotFoundException;
import com.Project.ecommerce.repositories.user.RoleRepository;
import com.Project.ecommerce.repositories.user.CustomerRepository;
import com.Project.ecommerce.repositories.user.UserRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private CustomerRepository customerRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private EmailService emailService;
    private JwtService jwtService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository, RoleRepository roleRepository, EmailService emailService, JwtService jwtService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public String registerCustomer(CustomerCO customerCO) throws MessagingException {
        if (customerRepository.findByEmail(customerCO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists, please enter a new email");
        }
        if (!customerCO.getPassword().equals(customerCO.getConfirmPassword())) {
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
        jwtService.storeToken(generatedToken, customer.getEmail());
        emailService.sendActivationEmail(customer.getEmail(), generatedToken);
        return "User registered, please activate your account through email sent on registered email ID";
    }

    public ResponseEntity<String> activateCustomer(String token) throws MessagingException {
        try {
            // if user is trying to activate from active and latest token
            if (jwtService.ifTokenPresent(token)) {
                //validating the user
                String extractedEmail = jwtService.extractEmail(token);
                User customer = userRepository.findByEmail(extractedEmail)
                        .orElseThrow(() -> new UserNotFoundException("Cannot find your details, please register properly"));
                //activating the user
                customer.setIsActive(true);
                userRepository.save(customer);
                //deleting useless token from db
                jwtService.deleteToken(extractedEmail);

                return new ResponseEntity<>("Account activated successfully!", HttpStatus.CREATED);
            }
            // // if user is trying to activate from active and old token
            throw new EmailAlreadyExistsException("Please try to activate with latest email sent");
        } catch (ExpiredJwtException e) {
            // delete expired token
            jwtService.deleteToken(e.getClaims().getSubject());

            // resend activation email
            resendActivationEmail(e.getClaims().getSubject());

            String message = "Your activation link has expired. We've sent a new one to your email.";
            return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
        }
        // Doing nothing when token is invalid, throwing proper error message, catching exception from isTokenPresent
    }

    public String resendActivationEmail(String email) throws MessagingException {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Cannot find your details, please register properly"));

        jwtService.deleteToken(email);
        String newToken = jwtService.generateToken(customer.getEmail());
        jwtService.storeToken(newToken, email);
        emailService.sendActivationEmail(customer.getEmail(), newToken);

        return "A new activation link has been sent to your email.";
    }

    public String deleteAllUsers() {
        userRepository.deleteAll();
        return "all users deleted";
    }

    public String deleteAllCustomers() {
        customerRepository.deleteAll();
        return "all customers deleted";
    }
}