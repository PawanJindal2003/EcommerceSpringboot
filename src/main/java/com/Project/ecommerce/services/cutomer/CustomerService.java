package com.Project.ecommerce.services.cutomer;

import com.Project.ecommerce.co.customer.UpdateProfileCO;
import com.Project.ecommerce.co.user.UpdateAddressCO;
import com.Project.ecommerce.co.user.UpdatePasswordCO;
import com.Project.ecommerce.dto.customer.ViewAddressDTO;
import com.Project.ecommerce.dto.customer.ViewProfileDTO;
import com.Project.ecommerce.entities.address.Address;
import com.Project.ecommerce.entities.user.Customer;
import com.Project.ecommerce.exceptions.customExceptions.ConfirmPasswordMismatchException;
import com.Project.ecommerce.exceptions.customExceptions.UserNotFoundException;
import com.Project.ecommerce.repositories.user.CustomerRepository;
import com.Project.ecommerce.security.jwt.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class CustomerService {
    private JwtService jwtService;
    private CustomerRepository customerRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public CustomerService(JwtService jwtService, CustomerRepository customerRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtService = jwtService;
        this.customerRepository = customerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

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
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Email not found"));

        ViewProfileDTO viewProfileDTO = new ViewProfileDTO();

        viewProfileDTO.setId(customer.getId());
        viewProfileDTO.setFirstName(customer.getFirstName());
        viewProfileDTO.setMiddleName(customer.getMiddleName());
        viewProfileDTO.setLastName(customer.getLastName());
        viewProfileDTO.setIsActive(customer.getIsActive());
        viewProfileDTO.setCustomerContact(customer.getCustomerContact());
//        viewProfileDTO.setImage(customer.getImage);

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
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Email not found"));

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

    public String updateProfile(HttpServletRequest request, UpdateProfileCO updateProfileCO) {
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
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Customer not found"));

        Optional.ofNullable(updateProfileCO.getFirstName()).ifPresent(customer::setFirstName);
        Optional.ofNullable(updateProfileCO.getMiddleName()).ifPresent(customer::setMiddleName);
        Optional.ofNullable(updateProfileCO.getLastName()).ifPresent(customer::setLastName);
        Optional.ofNullable(updateProfileCO.getCustomerContact()).ifPresent(customer::setCustomerContact);

        customerRepository.save(customer);
        return "Profile updated successfully.";
    }

    public String updatePassword(HttpServletRequest request, UpdatePasswordCO updatePasswordCO) {
        // check if password and confirm password are not different
        if (!updatePasswordCO.getPassword().equals(updatePasswordCO.getConfirmPassword())) {
            throw new ConfirmPasswordMismatchException("Password-Confirm password mismatch");
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
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Customer not found"));

        //updating password
        customer.setPassword(bCryptPasswordEncoder.encode(updatePasswordCO.getPassword()));
        //updating passwordUpdateTime
        customer.setPasswordUpdateDate(Date.from(Instant.now()));

        customerRepository.save(customer);

        return "Your password has been successfully updated";
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
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Customer not found"));

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

        return "Address added successfully";
    }

    public String deleteAddress(HttpServletRequest request, UpdateAddressCO updateAddressCO) {
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
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Customer not found"));

        List<Address> addresses = customer.getAddresses();
        String id = updateAddressCO.getId();

        boolean removed = addresses.removeIf(address -> address.getId().equals(id));

        if (removed) {
            customerRepository.save(customer);
            return "Address deleted successfully";
        }
        return "Address not found";
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
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Customer not found"));

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
                return "Address updated successfully";
            }
        }
        return "Address not found";
    }
}
