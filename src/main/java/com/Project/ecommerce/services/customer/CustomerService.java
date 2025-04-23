package com.Project.ecommerce.services.customer;

import com.Project.ecommerce.co.customer.UpdateProfileCO;
import com.Project.ecommerce.co.user.UpdateAddressCO;
import com.Project.ecommerce.co.user.UpdatePasswordCO;
import com.Project.ecommerce.dto.customer.ViewAddressDTO;
import com.Project.ecommerce.dto.customer.ViewProfileDTO;
import com.Project.ecommerce.entities.address.Address;
import com.Project.ecommerce.entities.user.Customer;
import com.Project.ecommerce.exceptions.customExceptions.ConfirmPasswordMismatchException;
import com.Project.ecommerce.exceptions.customExceptions.DuplicateResourceException;
import com.Project.ecommerce.exceptions.customExceptions.ResourceNotFoundException;
import com.Project.ecommerce.exceptions.customExceptions.UnauthorizedAccessException;
import com.Project.ecommerce.repositories.user.AddressRepository;
import com.Project.ecommerce.repositories.user.CustomerRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import com.Project.ecommerce.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private final JwtService jwtService;
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ImageUtil imageUtil;
    private final MessageSource messageSource;
    private final AddressRepository addressRepository;

    public ViewProfileDTO viewProfile(Principal principal) {
        logger.info("Fetching profile for customer with email: {}", principal.getName());

        String email = principal.getName();
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("email.not.found", null, LocaleContextHolder.getLocale())));
        logger.info("Customer profile found for email: {}", email);

        ViewProfileDTO viewProfileDTO = new ViewProfileDTO();

        viewProfileDTO.setId(customer.getId());
        if (customer.getMiddleName() == null) {
            viewProfileDTO.setName(customer.getFirstName() + " " + customer.getLastName());
        } else {
            viewProfileDTO.setName(customer.getFirstName() + " " + customer.getMiddleName() + " " + customer.getLastName());
        }
        viewProfileDTO.setIsActive(customer.getIsActive());
        viewProfileDTO.setCustomerContact(customer.getCustomerContact());
        viewProfileDTO.setProfilePicUrl(imageUtil.getImage(customer.getId()));
        logger.info("Profile successfully fetched for customer with email: {}", email);
        return viewProfileDTO;
    }

    public List<ViewAddressDTO> getAllAddresses(Principal principal) {
        String email = principal.getName();
        logger.info("Fetching addresses for customer with email: {}", email);

        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("email.not.found", null, LocaleContextHolder.getLocale())));

        List<ViewAddressDTO> addressesDTO = new ArrayList<>();

        for (Address address : customer.getAddresses()) {
            ViewAddressDTO addressDTO = new ViewAddressDTO();

            addressDTO.setAddressLine(address.getAddressLine());
            addressDTO.setCity(address.getCity());
            addressDTO.setState(address.getState());
            addressDTO.setCountry(address.getCountry());
            addressDTO.setZipCode(address.getZipCode());
            addressDTO.setLabel(address.getLabel());

            addressesDTO.add(addressDTO);
        }
        logger.info("Successfully fetched {} addresses for customer with email: {}", addressesDTO.size(), email);
        return addressesDTO;
    }

    public String updateProfile(Principal principal, UpdateProfileCO updateProfileCO, MultipartFile multipartFile) {
        String email = principal.getName();
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("customer.not.found", null, LocaleContextHolder.getLocale())));
        logger.info("Attempting to update profile for customer with email: {}", email);

        if (updateProfileCO != null) {
            Optional.ofNullable(updateProfileCO.getFirstName()).ifPresent(customer::setFirstName);
            Optional.ofNullable(updateProfileCO.getMiddleName()).ifPresent(customer::setMiddleName);
            Optional.ofNullable(updateProfileCO.getLastName()).ifPresent(customer::setLastName);
            Optional.ofNullable(updateProfileCO.getCustomerContact()).ifPresent(customer::setCustomerContact);
        }
        // Save image if provided
        if (multipartFile != null && !multipartFile.isEmpty()) {
            imageUtil.saveUserImage(multipartFile, customer);
        }
        customerRepository.save(customer);
        logger.info("Profile updated successfully for customer with email: {}", email);

        return messageSource.getMessage("customer.profile.updated", null, LocaleContextHolder.getLocale());
    }

    public String updatePassword(Principal principal, UpdatePasswordCO updatePasswordCO) {
        String email = principal.getName();
        logger.info("Attempting to update password for customer with email: {}", email);
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        // check if password and confirm password are not different
        logger.info("Customer found for email: {}", email);
        if (bCryptPasswordEncoder.matches(updatePasswordCO.getPassword(), customer.getPassword())) {
            throw new DuplicateResourceException("Please enter a different password from current password");
        }
        if (!updatePasswordCO.getPassword().equals(updatePasswordCO.getConfirmPassword())) {
            throw new ConfirmPasswordMismatchException(messageSource.getMessage("password.confirm.mismatch", null, LocaleContextHolder.getLocale()));
        }

        //updating password
        customer.setPassword(bCryptPasswordEncoder.encode(updatePasswordCO.getPassword()));
        //updating passwordUpdateTime
        customer.setPasswordUpdateDate(Date.from(Instant.now()));

        customerRepository.save(customer);
        logger.info("Password updated successfully for customer with email: {}", email);
        return messageSource.getMessage("customer.password.updated", null, LocaleContextHolder.getLocale());
    }

    public String addAddress(Principal principal, Address enteredNewAddress) {
        String email = principal.getName();
        logger.info("Attempting to add new address for customer with email: {}", email);

        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Address newAddress = new Address();
        newAddress.setAddressLine(enteredNewAddress.getAddressLine());
        newAddress.setCity(enteredNewAddress.getCity());
        newAddress.setState(enteredNewAddress.getState());
        newAddress.setCountry(enteredNewAddress.getCountry());
        newAddress.setZipCode(enteredNewAddress.getZipCode());
        newAddress.setLabel(enteredNewAddress.getLabel());

        List<Address> addresses = customer.getAddresses();
        addresses.add(newAddress);

        customer.setAddresses(addresses);

        customerRepository.save(customer);
        logger.info("New address successfully added for customer with email: {}", email);

        return messageSource.getMessage("customer.address.added", null, LocaleContextHolder.getLocale());
    }

    public String deleteAddress(Principal principal, String addressId) {
        String email = principal.getName();
        logger.info("Attempting to delete address with ID: {} for customer with email: {}", addressId, email);

        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // check id provided is valid
        addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        //check if provided address is customer's address
        addressRepository.findByUserId(customer.getId()).orElseThrow(() -> new ResourceNotFoundException("Wrong addressId provided"));

        addressRepository.deleteById(addressId);
        logger.info("Address with ID: {} has been deleted successfully for customer with email: {}", addressId, email);
        return messageSource.getMessage("customer.address.deleted", null, LocaleContextHolder.getLocale());
    }

    public String updateAddress(Principal principal, String addressId, UpdateAddressCO updateAddressCO) {
        String email = principal.getName();
        logger.info("Attempting to update address with ID: {} for customer with email: {}", addressId, email);
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        //validating address
        List<Address> addresses = customer.getAddresses();
        boolean isCustomerAddress = false;
        for (Address address : addresses) {
            if (address.getId().equals(addressId)) {
                isCustomerAddress = true;
                break;
            }
        }

        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("customer.address.not.found", null, LocaleContextHolder.getLocale())));
        if (!isCustomerAddress) {
            throw new UnauthorizedAccessException("Invalid address id, please pass logged in customer's address id");
        }

        Optional.ofNullable(updateAddressCO.getAddressLine()).ifPresent(address::setAddressLine);
        Optional.ofNullable(updateAddressCO.getCity()).ifPresent(address::setCity);
        Optional.ofNullable(updateAddressCO.getState()).ifPresent(address::setState);
        Optional.ofNullable(updateAddressCO.getCountry()).ifPresent(address::setCountry);
        Optional.ofNullable(updateAddressCO.getZipCode()).ifPresent(address::setZipCode);

        customerRepository.save(customer);
        logger.info("Address with ID: {} has been updated successfully for customer with email: {}", addressId, email);
        return messageSource.getMessage("customer.address.updated", null, LocaleContextHolder.getLocale());
    }
}