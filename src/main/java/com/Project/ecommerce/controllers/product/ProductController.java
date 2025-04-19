package com.Project.ecommerce.controllers.product;

import com.Project.ecommerce.co.product.AddProductCO;
import com.Project.ecommerce.co.product.AddProductVariationCO;
import com.Project.ecommerce.dto.response.SuccessResponse;
import com.Project.ecommerce.services.product.seller.ProductService;
import com.Project.ecommerce.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    ObjectMapper objectMapper = new ObjectMapper();
    private ProductService productService;
    private ResponseUtil responseUtil;

    public ProductController(ProductService productService, ResponseUtil responseUtil){
        this.productService = productService;
        this.responseUtil = responseUtil;
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/add-product")
    public ResponseEntity<SuccessResponse> addProduct(Principal principal, @RequestBody @Valid AddProductCO addProductCO) throws MessagingException {
        String responseMessage = productService.addProduct(principal, addProductCO);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.CREATED, responseMessage), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping(value = "/add-product-variation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse> addProductVariation(
            @ModelAttribute @Valid AddProductVariationCO addProductVariationCO,
            @RequestPart("primaryImage") MultipartFile primaryImage,
            @RequestPart(required = false)List<MultipartFile> secondaryImages) throws IOException {
        String responseMessage = productService.addProductVariation(addProductVariationCO, primaryImage, secondaryImages);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.CREATED, responseMessage), HttpStatus.CREATED);
    }
}
