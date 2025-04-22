package com.Project.ecommerce.services.cutomer;

import com.Project.ecommerce.co.customer.UpdateProfileCO;
import com.Project.ecommerce.co.user.UpdateAddressCO;
import com.Project.ecommerce.co.user.UpdatePasswordCO;
import com.Project.ecommerce.dto.customer.ViewAddressDTO;
import com.Project.ecommerce.dto.customer.ViewProfileDTO;
import com.Project.ecommerce.entities.address.Address;
import com.Project.ecommerce.entities.user.Customer;
import com.Project.ecommerce.exceptions.customExceptions.ConfirmPasswordMismatchException;
import com.Project.ecommerce.exceptions.customExceptions.ResourceNotFoundException;
import com.Project.ecommerce.repositories.user.AddressRepository;
import com.Project.ecommerce.repositories.user.CustomerRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import com.Project.ecommerce.utils.ImageUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final JwtService jwtService;
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ImageUtil imageUtil;
    private final MessageSource messageSource;
    private final AddressRepository addressRepository;

    public ViewProfileDTO viewProfile(HttpServletRequest request) {
        String accessToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();
                }
            }
        }
        String email = jwtService.extractEmail(accessToken);
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("email.not.found", null, request.getLocale())));

        ViewProfileDTO viewProfileDTO = new ViewProfileDTO();

        viewProfileDTO.setId(customer.getId());
        viewProfileDTO.setFirstName(customer.getFirstName());
        viewProfileDTO.setMiddleName(customer.getMiddleName());
        viewProfileDTO.setLastName(customer.getLastName());
        viewProfileDTO.setIsActive(customer.getIsActive());
        viewProfileDTO.setCustomerContact(customer.getCustomerContact());
        viewProfileDTO.setProfilePicUrl(imageUtil.getImage(customer.getId()));

        return viewProfileDTO;
    }

    public List<ViewAddressDTO> getAllAddresses(HttpServletRequest request) {
        String accessToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();
                }
            }
        }

        String email = jwtService.extractEmail(accessToken);
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("email.not.found", null, request.getLocale())));

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

        return addressesDTO;
    }

    public String updateProfile(HttpServletRequest request, UpdateProfileCO updateProfileCO, MultipartFile multipartFile) {
        String accessToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        String email = jwtService.extractEmail(accessToken);
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("customer.not.found", null, request.getLocale())));

        if(updateProfileCO != null) {
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
        return messageSource.getMessage("customer.profile.updated", null, request.getLocale());
    }

    public String updatePassword(HttpServletRequest request, UpdatePasswordCO updatePasswordCO) {
        // check if password and confirm password are not different
        if (!updatePasswordCO.getPassword().equals(updatePasswordCO.getConfirmPassword())) {
            throw new ConfirmPasswordMismatchException(messageSource.getMessage("password.confirm.mismatch", null, request.getLocale()));
        }
        String accessToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        String email = jwtService.extractEmail(accessToken);
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        //updating password
        customer.setPassword(bCryptPasswordEncoder.encode(updatePasswordCO.getPassword()));
        //updating passwordUpdateTime
        customer.setPasswordUpdateDate(Date.from(Instant.now()));

        customerRepository.save(customer);

        return messageSource.getMessage("customer.password.updated", null, request.getLocale());
    }

    public String addAddress(HttpServletRequest request, Address enteredNewAddress) {
        String accessToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        String email = jwtService.extractEmail(accessToken);
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

        return messageSource.getMessage("customer.address.added", null, request.getLocale());
    }

    public String deleteAddress(Principal principal, String addressId) {
        String email = principal.getName();
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // check id provided is valid
        addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address not found"));

        //check if provided address is customer's address
        addressRepository.findByUserId(customer.getId()).orElseThrow(()->new ResourceNotFoundException("Wrong addressId provided"));

        addressRepository.deleteById(addressId);

        return messageSource.getMessage("customer.address.deleted", null, LocaleContextHolder.getLocale());
    }

    public String updateAddress(HttpServletRequest request, UpdateAddressCO updateAddressCO) {
        String accessToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        String email = jwtService.extractEmail(accessToken);
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        List<Address> addresses = customer.getAddresses();
        String id = updateAddressCO.getId();

        for (Address address : addresses) {
            if (address.getId().equals(id)) {
                Optional.ofNullable(updateAddressCO.getAddressLine()).ifPresent(address::setAddressLine);
                Optional.ofNullable(updateAddressCO.getCity()).ifPresent(address::setCity);
                Optional.ofNullable(updateAddressCO.getState()).ifPresent(address::setState);
                Optional.ofNullable(updateAddressCO.getCountry()).ifPresent(address::setCountry);
                Optional.ofNullable(updateAddressCO.getZipCode()).ifPresent(address::setZipCode);

                customerRepository.save(customer);
                return messageSource.getMessage("customer.address.updated", null, request.getLocale());
            }
        }
        return messageSource.getMessage("customer.address.not.found", null, request.getLocale());
    }
}
