package com.Project.ecommerce.controllers.category;

import com.Project.ecommerce.co.category.category.MetadataValueCategoryCO;
import com.Project.ecommerce.co.category.category.UpdateCategoryCO;
import com.Project.ecommerce.co.category.categoryMetaDataField.AddCategoryMetaDataFieldCO;
import com.Project.ecommerce.dto.category.admin.CategoryMetaFieldsDTO;
import com.Project.ecommerce.dto.category.admin.CategoryResponseDTO;
import com.Project.ecommerce.dto.category.customer.CustomerCategoryResponseDTO;
import com.Project.ecommerce.dto.category.customer.CustomerFilterCategoryDTO;
import com.Project.ecommerce.dto.response.SuccessResponse;
import com.Project.ecommerce.services.category.CategoryService;
import com.Project.ecommerce.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ResponseUtil responseUtil;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-categoryMetaDataField")
    public ResponseEntity<SuccessResponse> addCategoryMetaDataField(@RequestBody AddCategoryMetaDataFieldCO addCategoryMetaDataFieldCO) {
        List<String> responseData = categoryService.addCategoryMetaDataField(addCategoryMetaDataFieldCO.getValue());
        return new ResponseEntity<>(responseUtil.successWithDataAndMessage(List.of(responseData.get(0)), HttpStatus.CREATED, responseData.get(1)), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-categoryMetaDataField")
    public ResponseEntity<SuccessResponse> getAllCategoryMetaDataField(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(required = false) String name) {
        CategoryMetaFieldsDTO categoryMetaDataFields;
        if (name == null) {
            categoryMetaDataFields = categoryService.getAllCategoryMetaDataField(pageNo, pageSize, direction, sortField);
        } else {
            categoryMetaDataFields = categoryService.getAllCategoryMetaDataFieldByName(pageNo, pageSize, direction, sortField, name);
        }
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, categoryMetaDataFields), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-category")
    public ResponseEntity<SuccessResponse> addCategory(@RequestParam(required = false) String parentCategoryId, @RequestParam String name) {
        String responseMessage = null;
        if (parentCategoryId == null) {
            responseMessage = categoryService.addCategory(name);
        } else {
            responseMessage = categoryService.addSubCategory(parentCategoryId, name);
        }
        return new ResponseEntity<>(responseUtil.success(HttpStatus.CREATED, responseMessage), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{categoryId}")
    public ResponseEntity<SuccessResponse> getCategory(@PathVariable String categoryId) {
        CategoryResponseDTO category = categoryService.getCategory(categoryId);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, category), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-categories")
    public ResponseEntity<SuccessResponse> getAllCategories(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(required = false) String name) {
        List<CategoryResponseDTO> categories;
        if (name == null) {
            categories = categoryService.getAllCategories(pageNo, pageSize, direction, sortField);
        } else {
            categories = categoryService.getAllCategoriesByName(pageNo, pageSize, direction, sortField, name);
        }
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, categories), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-category")
    public ResponseEntity<SuccessResponse> updateCategory(@RequestBody UpdateCategoryCO updateCategoryCO) {
        String responseMessage = categoryService.updateCategory(updateCategoryCO);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-metadata-category")
    public ResponseEntity<SuccessResponse> addMetadataCategory(@Valid @RequestBody MetadataValueCategoryCO metadataValueCategoryCO) {
        String responseMessage = categoryService.addMetadataCategory(metadataValueCategoryCO);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.CREATED, responseMessage), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-metadata-category")
    public ResponseEntity<SuccessResponse> updateMetadataCategory(@Valid @RequestBody MetadataValueCategoryCO metadataValueCategoryCO) {
        String responseMessage = categoryService.updateMetadataCategory(metadataValueCategoryCO);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller/all-categories")
    public ResponseEntity<SuccessResponse> getAllCategories() {
        List<CategoryResponseDTO> allCategories = categoryService.getSellerCategories();
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, allCategories), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer/{categoryId}/categories")
    public ResponseEntity<SuccessResponse> getCustomerCategory(@PathVariable(required = false) String categoryId) {
        List<CustomerCategoryResponseDTO> categories;
        if (categoryId == null) {
            categories = categoryService.getCustomerRootCategories();
        } else {
            categories = categoryService.getCustomerCategories(categoryId);
        }
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, categories), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer/{categoryId}")
    public ResponseEntity<SuccessResponse> getFilteredCategories(@PathVariable(required = false) String categoryId) {
        CustomerFilterCategoryDTO filteredData = categoryService.getFilteredCategories(categoryId);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, filteredData), HttpStatus.OK);
    }
}
