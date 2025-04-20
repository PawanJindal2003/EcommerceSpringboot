package com.Project.ecommerce.services.product.seller;

import com.Project.ecommerce.co.product.AddProductCO;
import com.Project.ecommerce.co.product.AddProductVariationCO;
import com.Project.ecommerce.co.product.UpdateProductCO;
import com.Project.ecommerce.dto.category.admin.CategoryResponseDTO;
import com.Project.ecommerce.dto.product.seller.SellerProductDTO;
import com.Project.ecommerce.dto.product.seller.SellerProductVariationDTO;
import com.Project.ecommerce.entities.category.Category;
import com.Project.ecommerce.entities.category.CategoryMetaDataFieldValues;
import com.Project.ecommerce.entities.product.Product;
import com.Project.ecommerce.entities.product.ProductReview;
import com.Project.ecommerce.entities.product.ProductVariation;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.exceptions.customExceptions.*;
import com.Project.ecommerce.repositories.category.CategoryRepository;
import com.Project.ecommerce.repositories.product.ProductRepository;
import com.Project.ecommerce.repositories.product.ProductVariationRepository;
import com.Project.ecommerce.repositories.user.SellerRepository;
import com.Project.ecommerce.services.category.CategoryService;
import com.Project.ecommerce.utils.ImageUtil;
import com.Project.ecommerce.utils.JsonUtil;
import com.Project.ecommerce.utils.specifications.ProductSpecifications;
import com.Project.ecommerce.utils.specifications.ProductVariationSpecification;
import com.Project.ecommerce.utils.validator.ProductUtil;
import com.Project.ecommerce.utils.validator.ProductVariationUtil;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerProductEmailService sellerProductEmailService;
    private final SellerRepository sellerRepository;
    private final ProductVariationRepository productVariationRepository;
    private final ImageUtil imageUtil;
    private final ProductVariationUtil productVariationValidator;
    private final ProductUtil productValidator;
    private final CategoryService categoryService;

    public String addProduct(Principal principal, AddProductCO addProductCO) throws MessagingException {
        Seller seller = sellerRepository.findByEmail(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("Seller not found"));
        String categoryId = addProductCO.getCategoryId();
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Category not found"));

        //category should be leaf category
        if (!category.getIsLeafCategory()) {
            throw new NonLeafCategoryException("Please select a leaf category to add the product");
        }
        //unique product name
        Product existingProduct = productRepository.getNameByBrandAndSellerIdAndCategoryId(
                addProductCO.getBrand(), seller.getId(), categoryId
        );

        if (existingProduct != null && existingProduct.getName().equalsIgnoreCase(addProductCO.getName())) {
            throw new DuplicateCompanyException("Product name already exists, please add a unique product name.");
        }

        Product product = createProduct(addProductCO, category, seller);

        productRepository.save(product);

        //sending email to admin
        sellerProductEmailService.sendNewProductActivationEmail(principal.getName(), product, seller);
        return "Product added successfully";
    }

    private Product createProduct(AddProductCO addProductCO, Category category, Seller seller) {
        Product product = new Product();

        product.setSeller(seller);
        product.setCategory(category);
        product.setName(addProductCO.getName());
        product.setBrand(addProductCO.getBrand());

        Optional.ofNullable(addProductCO.getDescription()).ifPresent(product::setDescription);
        Optional.ofNullable(addProductCO.getIsCancellable()).ifPresent(product::setIsCancellable);
        Optional.ofNullable(addProductCO.getIsReturnable()).ifPresent(product::setIsReturnable);

        return product;
    }

    public String addProductVariation( AddProductVariationCO addProductVariationCO, MultipartFile primaryImage, List<MultipartFile> secondaryImages) throws IOException {
        Product product = productRepository.findById(addProductVariationCO.getProductId())
                .orElseThrow(() -> new UserNotFoundException("Invalid Product id"));
        productVariationValidator.validateAndFetchProduct(product);
        ProductVariation productVariation = createProductVariation(product, addProductVariationCO, primaryImage, secondaryImages);
        productVariationRepository.save(productVariation);
        return "Product variation added successfully for your product";
    }

    private ProductVariation createProductVariation(Product product, AddProductVariationCO co, MultipartFile primaryImage, List<MultipartFile> secondaryImages) throws IOException {
        ProductVariation productVariation = new ProductVariation();
        productVariation.setProduct(product);
        productVariation.setPrice(co.getPrice());
        productVariation.setQuantityAvailable(co.getQuantityAvailable());
        String imageName = imageUtil.saveProductVariationImage(primaryImage, product.getId(), "primary");
        productVariation.setPrimaryImageName(imageName);
        if (secondaryImages != null) {
            int sequence = 1;
            for (MultipartFile image : secondaryImages) {
                imageUtil.saveProductVariationImage(image, product.getId(), "secondary_" + sequence);
                sequence++;
            }
        }
        Map<String, String> metadata = co.getMetadata();
        productVariationValidator.validateBlankMetadata(metadata);
        productVariationValidator.checkDuplicateVariation(product, metadata);
        productVariationValidator.validateAllowedMetadata(product, metadata);
        productVariationValidator.validateMetadataStructure(product, metadata);
        productVariation.setMetaData(JsonUtil.mapToJson(co.getMetadata()));
        return productVariation;
    }

    public SellerProductDTO getSellerProduct(Principal principal, String productId){
        String sellerEmail = principal.getName();
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new UserNotFoundException("Seller not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found"));
        productValidator.validateIsDeletedProduct(product);
        productValidator.validateIsSellerProduct(seller, product);
        return createSellerProductDTO(product);
    }

    private SellerProductDTO createSellerProductDTO(Product product){
        SellerProductDTO dto = new SellerProductDTO();

        dto.setName(product.getName());
        dto.setBrand(product.getBrand());
        dto.setDescription(product.getDescription());
        dto.setIsReturnable(product.getIsReturnable());
        dto.setIsCancellable(product.getIsCancellable());
        dto.setIsActive(product.getIsActive());

        Category category = product.getCategory();
        List<CategoryMetaDataFieldValues> metaDataFieldValues = category.getMetadataFieldValues();
        CategoryResponseDTO categoryDTO =categoryService.saveCategoryInDTO(category.getId(), category, metaDataFieldValues);

        dto.setCategory(categoryDTO);

        return dto;
    }

    public SellerProductVariationDTO getSellerProductVariation(Principal principal, String productVariationId){
        String sellerEmail = principal.getName();
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new UserNotFoundException("Seller not found"));
        ProductVariation productVariation = productVariationRepository.findById(productVariationId).orElseThrow(()->new ResourceNotFoundException("Product variation not found"));
        productVariationValidator.validateIsSellerProductVariation(seller, productVariation);
        Product product = productVariation.getProduct();
        productValidator.validateIsDeletedProduct(product);
        return createSellerProductVariationDTO(productVariation, product);
    }

    private SellerProductVariationDTO createSellerProductVariationDTO(ProductVariation productVariation, Product product){
        SellerProductVariationDTO dto = new SellerProductVariationDTO();

        dto.setPrice(productVariation.getPrice());
        dto.setQuantityAvailable(productVariation.getQuantityAvailable());
        dto.setMetaData(JsonUtil.jsonToMap(productVariation.getMetaData()));
        dto.setProduct(createSellerProductDTO(product));
        dto.setIsActive(productVariation.getIsActive());
        dto.setPrimaryImageName(imageUtil.getProductVariationPrimaryImage(product.getId()));
        dto.setSecondaryImageNames(imageUtil.getProductVariationSecondaryImages(product.getId()));
        return dto;
    }

    public List<SellerProductDTO> getSellerAllProducts(Principal principal, int pageNo, int pageSize, String sortField, String direction, String query){
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));

        String sellerEmail = principal.getName();
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new UserNotFoundException("Seller not found"));

        Specification<Product> specification = ProductSpecifications.bySeller(seller.getId()).and(ProductSpecifications.isNotDeleted());
        if (query != null && !query.isBlank()) {
            specification = specification.and(ProductSpecifications.fromQueryString(query));
        }

        Page<Product> sellerProducts = productRepository.findAll(specification, pageable);
        List<SellerProductDTO> sellerProductDTOs = new ArrayList<>();
        for(Product sellerProduct : sellerProducts.getContent()){
            SellerProductDTO dto = createSellerProductDTO(sellerProduct);
            sellerProductDTOs.add(dto);
        }
        return sellerProductDTOs;
    }

    public List<SellerProductVariationDTO> getSellerAllProductVariations(Principal principal, String productId, int pageNo, int pageSize, String sortField, String direction, String query){
        String sellerEmail = principal.getName();
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new UserNotFoundException("Seller not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found"));
        productValidator.validateIsDeletedProduct(product);
        productValidator.validateIsSellerProduct(seller, product);

        Page<ProductVariation> sellerProductVariations;
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));
        if(query!=null && !query.isBlank()){
            Specification<ProductVariation> specification = ProductVariationSpecification.fromQueryString(query);
            sellerProductVariations =  productVariationRepository.findAll(specification, pageable);
        }
        else{
            sellerProductVariations = productVariationRepository.findAll(pageable);
        }

        List<SellerProductVariationDTO> sellerProductVariationDTOS = new ArrayList<>();
        for(ProductVariation sellerProductVariation:sellerProductVariations.getContent()){
            SellerProductVariationDTO sellerProductVariationDTO = createSellerProductVariationDTO(sellerProductVariation, product);
            sellerProductVariationDTOS.add(sellerProductVariationDTO);
        }

        return sellerProductVariationDTOS;
    }

    public String deleteSellerProduct(Principal principal, String productId){
        String sellerEmail = principal.getName();
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new UserNotFoundException("Seller not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found"));
        productValidator.validateIsSellerProduct(seller, product);

        productRepository.deleteById(productId);
        return "Product deleted successfully";
    }

    public String updateSellerProduct(Principal principal, String productId, UpdateProductCO updateProductCO){
        Seller seller = sellerRepository.findByEmail(principal.getName()).orElseThrow(()->new ResourceNotFoundException("Seller not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Category category = categoryRepository.findById(product.getCategory().getId()).orElseThrow(()->new UserNotFoundException("Category not found"));
        productValidator.validateIsSellerProduct(seller, product);

        Product existingProduct = productRepository.getNameByBrandAndSellerIdAndCategoryId(
                product.getBrand(), seller.getId(), category.getId()
        );

        if (existingProduct != null && existingProduct.getName().equalsIgnoreCase(updateProductCO.getName())) {
            throw new DuplicateCompanyException("Product name already exists, please add a unique product name.");
        }

        product.setName(updateProductCO.getName());
        product.setDescription(updateProductCO.getDescription());
        product.setIsCancellable(updateProductCO.getIsCancellable());
        product.setIsReturnable(updateProductCO.getIsReturnable());

        productRepository.save(product);
        return "Product has been updated successfully";
    }
}
