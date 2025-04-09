package com.Project.ecommerce.controllers.admin;

import com.Project.ecommerce.dto.admin.GetAllCustomersDTO;
import com.Project.ecommerce.services.admin.AdminService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("getRole('ADMIN')")
    @GetMapping("/all-customers")
    public ResponseEntity<List<GetAllCustomersDTO>> getAllCustomers(@RequestParam(required = false, defaultValue = "0") int page,
                                                                    @RequestParam(required = false, defaultValue = "10") int size,
                                                                    @RequestParam(required = false) String email){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "ID"));
        List<GetAllCustomersDTO> allCustomers = null;
        if(email == null){
            allCustomers = adminService.getAllCustomers(pageable);
        }
        else{
            allCustomers = adminService.getAllCustomersByEmail(email, pageable);
        }
        return new ResponseEntity<>(allCustomers, HttpStatus.OK);
    }

}
