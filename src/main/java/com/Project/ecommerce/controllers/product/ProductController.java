package com.Project.ecommerce.controllers.product;

import com.Project.ecommerce.co.product.AddProductCO;
import com.Project.ecommerce.co.product.AddProductVariationCO;
import com.Project.ecommerce.co.product.UpdateProductCO;
import com.Project.ecommerce.co.product.UpdateProductVariationCO;
import com.Project.ecommerce.dto.product.admin.AdminProductDTO;
import com.Project.ecommerce.dto.product.customer.CustomerAllProductsDTO;
import com.Project.ecommerce.dto.product.customer.CustomerProductDTO;
import com.Project.ecommerce.dto.product.seller.SellerProductDTO;
import com.Project.ecommerce.dto.product.seller.SellerProductVariationDTO;
import com.Project.ecommerce.dto.response.SuccessResponse;
import com.Project.ecommerce.services.product.ProductService;
import com.Project.ecommerce.utils.ResponseUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ResponseUtil responseUtil;

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

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping(value = "/{productId}")
    public ResponseEntity<SuccessResponse> getSellerProduct(Principal principal, @PathVariable String productId) throws IOException {
        SellerProductDTO product = productService.getSellerProduct(principal, productId);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, product), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping(value = "/product-variation/{productVariationId}")
    public ResponseEntity<SuccessResponse> getSellerProductVariation(Principal principal, @PathVariable String productVariationId) throws IOException {
        SellerProductVariationDTO productVariation = productService.getSellerProductVariation(principal, productVariationId);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, productVariation), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping(value = "/all-products")
    public ResponseEntity<SuccessResponse> getSellerAllProducts(Principal principal,
                                                                @RequestParam(required = false, defaultValue = "0") int page,
                                                                @RequestParam(required = false, defaultValue = "10") int size,
                                                                @RequestParam(required = false, defaultValue = "id") String sortField,
                                                                @RequestParam(required = false, defaultValue = "ASC") String direction,
                                                                @RequestParam(required = false) String query) throws IOException {
        List<SellerProductDTO> sellerProducts = productService.getSellerAllProducts(principal, page, size, sortField, direction, query);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, sellerProducts), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping(value = "/{productId}/all-product-variations")
    public ResponseEntity<SuccessResponse> getSellerAllProductVariations(Principal principal, @PathVariable String productId,
                                                                @RequestParam(required = false, defaultValue = "0") int page,
                                                                @RequestParam(required = false, defaultValue = "10") int size,
                                                                @RequestParam(required = false, defaultValue = "id") String sortField,
                                                                @RequestParam(required = false, defaultValue = "ASC") String direction,
                                                                @RequestParam(required = false) String query) throws IOException {
        List<SellerProductVariationDTO> sellerProductVariations = productService.getSellerAllProductVariations(principal, productId, page, size, sortField, direction, query);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, sellerProductVariations), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping(value = "/{productId}")
    public ResponseEntity<SuccessResponse> deleteSellerProduct(Principal principal, @PathVariable String productId) throws IOException {
        String responseMessage = productService.deleteSellerProduct(principal, productId);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping(value = "/update-product/{productId}")
    public ResponseEntity<SuccessResponse> updateSellerProduct(Principal principal, @PathVariable String productId, @RequestBody UpdateProductCO updateProductCO) throws IOException {
        String responseMessage = productService.updateSellerProduct(principal, productId, updateProductCO);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping(value = "/update-product-variation/{productVariationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse> addProductVariation(
            Principal principal,
            @PathVariable String productVariationId,
            @ModelAttribute @Valid UpdateProductVariationCO updateProductVariationCO,
            @RequestPart("primaryImage") MultipartFile primaryImage,
            @RequestPart(value = "secondaryImages", required = false) List<MultipartFile> secondaryImages) throws IOException {
        String responseMessage = productService.updateProductVariation(principal, productVariationId, updateProductVariationCO, primaryImage, secondaryImages);
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }

    //customer apis
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping(value = "/customer/{productId}")
    public ResponseEntity<SuccessResponse> getCustomerProduct(@PathVariable String productId) {
        CustomerProductDTO product = productService.getCustomerProduct(productId);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, product), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping(value = "/customer/all-products/{categoryId}")
    public ResponseEntity<SuccessResponse> getCustomerAllProduct(@PathVariable String categoryId,
                                                                 @RequestParam(required = false, defaultValue = "0") int page,
                                                                 @RequestParam(required = false, defaultValue = "10") int size,
                                                                 @RequestParam(required = false, defaultValue = "id") String sortField,
                                                                 @RequestParam(required = false, defaultValue = "ASC") String direction,
                                                                 @RequestParam(required = false) String query) {
        List<CustomerAllProductsDTO> products = productService.getCustomerAllProduct(page, size, sortField, direction, query, categoryId);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, products), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping(value = "/customer/similar-products/{productId}")
    public ResponseEntity<SuccessResponse> getCustomerSimilarProducts(@PathVariable String productId,
                                                                 @RequestParam(required = false, defaultValue = "0") int page,
                                                                 @RequestParam(required = false, defaultValue = "10") int size,
                                                                 @RequestParam(required = false, defaultValue = "id") String sortField,
                                                                 @RequestParam(required = false, defaultValue = "ASC") String direction,
                                                                 @RequestParam(required = false) String query) {
        List<CustomerProductDTO> products = productService.getCustomerSimilarProducts(page, size, sortField, direction, query, productId);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, products), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/admin/{productId}")
    public ResponseEntity<SuccessResponse> getAdminProduct(@PathVariable String productId) {
        AdminProductDTO product = productService.getAdminProduct(productId);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, product), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/admin/all-products")
    public ResponseEntity<SuccessResponse> getAdminAllProducts(@RequestParam(required = false, defaultValue = "0") int page,
                                                               @RequestParam(required = false, defaultValue = "10") int size,
                                                               @RequestParam(required = false, defaultValue = "id") String sortField,
                                                               @RequestParam(required = false, defaultValue = "ASC") String direction,
                                                               @RequestParam(required = false) String query) {
        List<AdminProductDTO> products = productService.getAdminAllProducts(page, size, sortField, direction, query);
        return new ResponseEntity<>(responseUtil.successWithData(HttpStatus.OK, products), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/admin/activate/{productId}")
    public ResponseEntity<SuccessResponse> activateProduct(@PathVariable String productId) throws MessagingException {
        String responseMessage = productService.activateDeactivateProduct(productId, "activate");
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/admin/de-activate/{productId}")
    public ResponseEntity<SuccessResponse> deactivateProduct(@PathVariable String productId) throws MessagingException {
        String responseMessage = productService.activateDeactivateProduct(productId, "deactivate");
        return new ResponseEntity<>(responseUtil.success(HttpStatus.OK, responseMessage), HttpStatus.OK);
    }
}
