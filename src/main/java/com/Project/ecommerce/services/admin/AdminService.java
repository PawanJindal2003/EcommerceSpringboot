package com.Project.ecommerce.services.admin;

import com.Project.ecommerce.dto.admin.GetAllCustomersDTO;
import com.Project.ecommerce.repositories.user.CustomerRepository;
import com.Project.ecommerce.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private CustomerRepository customerRepository;

    @Autowired
    public AdminService(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
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
}
