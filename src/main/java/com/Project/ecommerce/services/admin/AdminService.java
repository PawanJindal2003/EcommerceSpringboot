package com.Project.ecommerce.services.admin;

import com.Project.ecommerce.dto.admin.GetAllCustomersDTO;
import com.Project.ecommerce.dto.admin.GetAllSellersDTO;
import com.Project.ecommerce.entities.address.Address;
import com.Project.ecommerce.repositories.user.CustomerRepository;
import com.Project.ecommerce.repositories.user.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private CustomerRepository customerRepository;
    private SellerRepository sellerRepository;

    @Autowired
    public AdminService(CustomerRepository customerRepository, SellerRepository sellerRepository) {
        this.customerRepository = customerRepository;
        this.sellerRepository = sellerRepository;
    }

    public List<GetAllCustomersDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).stream().map(customer -> {
            GetAllCustomersDTO customerDTO = new GetAllCustomersDTO();
            customerDTO.setId(customer.getID());
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
            customerDTO.setId(customer.getID());
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
            sellersDTO.setId(seller.getID());
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
            sellersDTO.setId(seller.getID());
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
}
