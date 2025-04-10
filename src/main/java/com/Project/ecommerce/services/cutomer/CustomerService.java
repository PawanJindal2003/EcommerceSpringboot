package com.Project.ecommerce.services.cutomer;

import com.Project.ecommerce.dto.customer.ViewAddressDTO;
import com.Project.ecommerce.dto.customer.ViewProfileDTO;
import com.Project.ecommerce.entities.address.Address;
import com.Project.ecommerce.entities.user.Customer;
import com.Project.ecommerce.exceptions.customExceptions.UserNotFoundException;
import com.Project.ecommerce.repositories.user.CustomerRepository;
import com.Project.ecommerce.security.jwt.JwtFilter;
import com.Project.ecommerce.security.jwt.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {
    private JwtService jwtService;
    private CustomerRepository customerRepository;
    @Autowired
    public CustomerService(JwtService jwtService, CustomerRepository customerRepository){
        this.jwtService = jwtService;
        this.customerRepository = customerRepository;
    }

    public ViewProfileDTO viewProfile(HttpServletRequest request){
        String accessToken = null;
        if(request.getCookies()!=null){
            for(Cookie cookie:request.getCookies()){
                if(cookie.getName().equals("accessToken")){
                    accessToken = cookie.getValue();
                }
            }
        }
        String email = jwtService.extractEmail(accessToken);
        Customer customer = customerRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("Email not found"));

        ViewProfileDTO viewProfileDTO = new ViewProfileDTO();

        viewProfileDTO.setId(customer.getID());
        viewProfileDTO.setFirstName(customer.getFirstName());
        viewProfileDTO.setMiddleName(customer.getMiddleName());
        viewProfileDTO.setLastName(customer.getLastName());
        viewProfileDTO.setIsActive(customer.getIsActive());
        viewProfileDTO.setCustomerContact(customer.getCustomerContact());
//        viewProfileDTO.setImage(customer.getImage);

        return viewProfileDTO;
    }

    public List<ViewAddressDTO> getAllAddresses(HttpServletRequest request){
        String accessToken = null;

        if(request.getCookies()!=null){
            for(Cookie cookie:request.getCookies()){
                if(cookie.getName().equals("accessToken")){
                    accessToken = cookie.getValue();
                }
            }
        }

        String email = jwtService.extractEmail(accessToken);
        Customer customer = customerRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("Email not found"));

        List<ViewAddressDTO> addressesDTO = new ArrayList<>();

        for(Address address : customer.getAddresses()){
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
}
