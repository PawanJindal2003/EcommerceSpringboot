package com.Project.ecommerce.controllers.admin;

import com.Project.ecommerce.co.admin.GetIdCO;
import com.Project.ecommerce.dto.admin.GetAllCustomersDTO;
import com.Project.ecommerce.dto.admin.GetAllSellersDTO;
import com.Project.ecommerce.services.admin.AdminService;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        List<GetAllCustomersDTO> allCustomers = null;
        if(email == null){
            allCustomers = adminService.getAllCustomers(pageable);
        }
        else{
            allCustomers = adminService.getAllCustomersByEmail(email, pageable);
        }
        return new ResponseEntity<>(allCustomers, HttpStatus.OK);
    }

    @PreAuthorize("getRole('ADMIN')")
    @GetMapping("/all-sellers")
    public ResponseEntity<List<GetAllSellersDTO>> getAllSellers(@RequestParam(required = false, defaultValue = "0") int page,
                                                                  @RequestParam(required = false, defaultValue = "10") int size,
                                                                  @RequestParam(required = false) String email){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        List<GetAllSellersDTO> allSellers = null;
        if(email == null){
            allSellers = adminService.getAllSellers(pageable);
        }
        else{
            allSellers = adminService.getAllSellersByEmail(email, pageable);
        }
        return new ResponseEntity<>(allSellers, HttpStatus.OK);
    }

    //common for customer and seller
    @PreAuthorize("getRole('ADMIN')")
    @PatchMapping("/activate/user")
    public ResponseEntity<String> activateUser(@RequestBody GetIdCO getIdCO) throws MessagingException {
        String responseMessage = adminService.activateDeactivateUser(getIdCO.getId(), true);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
    @PreAuthorize("getRole('ADMIN')")
    @PatchMapping("/deactivate/user")
    public ResponseEntity<String> deactivateUser(@RequestBody GetIdCO getIdCO) throws MessagingException {
        String responseMessage = adminService.activateDeactivateUser(getIdCO.getId(), false);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
