package com.Project.ecommerce.controllers.admin;

import com.Project.ecommerce.co.admin.GetIdCO;
import com.Project.ecommerce.dto.response.SuccessResponse;
import com.Project.ecommerce.dto.admin.GetAllCustomersDTO;
import com.Project.ecommerce.dto.admin.GetAllSellersDTO;
import com.Project.ecommerce.services.admin.AdminService;
import com.Project.ecommerce.utils.ResponseUtil;
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
    private ResponseUtil responseUtil;

    public AdminController(AdminService adminService, ResponseUtil responseUtil) {
        this.adminService = adminService;
        this.responseUtil = responseUtil;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-customers")
    public ResponseEntity<SuccessResponse> getAllCustomers(@RequestParam(required = false, defaultValue = "0") int page,
                                                           @RequestParam(required = false, defaultValue = "10") int size,
                                                           @RequestParam(required = false) String email,
                                                           @RequestParam(required = false, defaultValue = "id") String sortField,
                                                           @RequestParam(required = false, defaultValue = "ASC") String direction){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortField));
        List<GetAllCustomersDTO> allCustomers = null;
        if(email == null){
            allCustomers = adminService.getAllCustomers(pageable);
        }
        else{
            allCustomers = adminService.getAllCustomersByEmail(email, pageable);
        }
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, allCustomers), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-sellers")
    public ResponseEntity<SuccessResponse> getAllSellers(@RequestParam(required = false, defaultValue = "0") int page,
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
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, allSellers), HttpStatus.OK);
    }

    //common for customer and seller
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/activate/user")
    public ResponseEntity<SuccessResponse> activateUser(@RequestBody GetIdCO getIdCO) throws MessagingException {
        String responseMessage = adminService.activateDeactivateUser(getIdCO.getId(), true);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/deactivate/user")
    public ResponseEntity<SuccessResponse> deactivateUser(@RequestBody GetIdCO getIdCO) throws MessagingException {
        String responseMessage = adminService.activateDeactivateUser(getIdCO.getId(), false);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }
}
