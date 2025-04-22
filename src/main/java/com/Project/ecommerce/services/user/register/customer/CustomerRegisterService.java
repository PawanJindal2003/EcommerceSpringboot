package com.Project.ecommerce.services.user.register.customer;

import com.Project.ecommerce.co.registration.CustomerCO;
import com.Project.ecommerce.entities.user.Customer;
import com.Project.ecommerce.entities.user.Role;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.ConfirmPasswordMismatchException;
import com.Project.ecommerce.exceptions.customExceptions.EmailAlreadyExistsException;
import com.Project.ecommerce.exceptions.customExceptions.ResourceNotFoundException;
import com.Project.ecommerce.repositories.user.RoleRepository;
import com.Project.ecommerce.repositories.user.CustomerRepository;
import com.Project.ecommerce.repositories.user.UserRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerRegisterService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerEmailService customerEmailService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MessageSource messageSource;

    public String registerCustomer(CustomerCO customerCO) throws MessagingException {
        // validations
        if (customerRepository.findByEmail(customerCO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(messageSource.getMessage("customer.email.already.exists", null, LocaleContextHolder.getLocale()));
        }
        if (!customerCO.getPassword().equals(customerCO.getConfirmPassword())) {
            throw new ConfirmPasswordMismatchException(messageSource.getMessage("customer.confirm.password.mismatch", null, LocaleContextHolder.getLocale()));
        }

        Role role = roleRepository.findByAuthority("CUSTOMER");
        Customer customer = new Customer();
        customer.setFirstName(customerCO.getFirstName());
        customer.setMiddleName(customerCO.getMiddleName());
        customer.setLastName(customerCO.getLastName());
        customer.setEmail(customerCO.getEmail());
        customer.setPassword(bCryptPasswordEncoder.encode(customerCO.getPassword()));
        customer.setCustomerContact(customerCO.getCustomerContact());
        customer.setAddresses(customerCO.getAddresses());
        customer.setRole(role);

        customerRepository.save(customer);
        // token generation and saving in db
        String generatedToken = jwtService.generateToken(customer.getEmail());
        jwtService.storeToken(generatedToken, customer.getEmail());
        customerEmailService.sendActivationEmail(customer.getEmail(), generatedToken);

        return messageSource.getMessage("customer.register.success", null, LocaleContextHolder.getLocale());
    }

    public String activateCustomer(String token) throws MessagingException {
        try {
            // if account is already active
            if (userRepository.findByEmail(jwtService.extractEmail(token)).get().getIsActive()) {
                return messageSource.getMessage("customer.account.already.active", null, LocaleContextHolder.getLocale());
            }

            // if user is trying to activate from active and latest token
            if (jwtService.ifTokenPresent(token)) {
                // validating the user
                String extractedEmail = jwtService.extractEmail(token);
                User customer = userRepository.findByEmail(extractedEmail)
                        .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("customer.user.not.found", null, LocaleContextHolder.getLocale())));

                // activating the user
                customer.setIsActive(true);
                userRepository.save(customer);
                // deleting useless token from db
                jwtService.deleteToken(extractedEmail);

                // sending confirmation mail
                customerEmailService.sendConfirmationEmail(extractedEmail);
                return messageSource.getMessage("customer.account.activated", null, LocaleContextHolder.getLocale());
            }

            throw new EmailAlreadyExistsException(messageSource.getMessage("customer.invalid.activation.token", null, LocaleContextHolder.getLocale()));
        } catch (ExpiredJwtException e) {
            // delete expired token
            jwtService.deleteToken(e.getClaims().getSubject());

            // resend activation email
            resendActivationEmail(e.getClaims().getSubject());

            return messageSource.getMessage("customer.activation.token.expired", null, LocaleContextHolder.getLocale());
        }
    }

    public String resendActivationEmail(String email) throws MessagingException {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale())));

        jwtService.deleteToken(email);
        String newToken = jwtService.generateToken(customer.getEmail());
        jwtService.storeToken(newToken, email);
        customerEmailService.sendActivationEmail(customer.getEmail(), newToken);

        return messageSource.getMessage("customer.resend.activation.email", null, LocaleContextHolder.getLocale());
    }
}