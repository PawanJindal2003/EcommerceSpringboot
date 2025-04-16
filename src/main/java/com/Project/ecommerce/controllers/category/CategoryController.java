package com.Project.ecommerce.controllers.category;

import com.Project.ecommerce.co.category.categoryMetaDataField.AddCategoryMetaDataFieldCO;
import com.Project.ecommerce.dto.response.SuccessResponse;
import com.Project.ecommerce.entities.category.Category;
import com.Project.ecommerce.entities.category.CategoryMetaDataField;
import com.Project.ecommerce.services.category.CategoryService;
import com.Project.ecommerce.utils.ResponseUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private CategoryService categoryService;
    private ResponseUtil responseUtil;

    public CategoryController(CategoryService categoryService, ResponseUtil responseUtil){
        this.categoryService = categoryService;
        this.responseUtil = responseUtil;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-categoryMetaDataField")
    public ResponseEntity<SuccessResponse> addCategoryMetaDataField(@RequestBody AddCategoryMetaDataFieldCO addCategoryMetaDataFieldCO){
        List<String> responseData = categoryService.addCategoryMetaDataField(addCategoryMetaDataFieldCO.getValue());
        return new ResponseEntity<>(responseUtil.successWithDataAndMessage(List.of(responseData.get(0)), HttpStatus.CREATED, responseData.get(1)), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-categoryMetaDataField")
    public ResponseEntity<SuccessResponse> getAllCategoryMetaDataField(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(defaultValue = "id") String sortField){
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));
        Page<CategoryMetaDataField> categoryMetaDataFields= categoryService.getAllCategoryMetaDataField(pageable);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, categoryMetaDataFields.getContent()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-category")
    public ResponseEntity<SuccessResponse> addCategory(@RequestParam(required = false) String parentCategoryId, @RequestParam String name){
        String responseMessage = null;
        if(parentCategoryId == null){
            responseMessage = categoryService.addCategory(name);
        }
        else{
            responseMessage = categoryService.addSubCategory(parentCategoryId, name);
        }
        return new ResponseEntity<>(responseUtil.success(HttpStatus.CREATED,responseMessage), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/category")
    public ResponseEntity<SuccessResponse> addCategory(@RequestParam(required = false) String id){
        List<Category> categories = categoryService.getCategory(id);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, categories), HttpStatus.OK);
    }
}
