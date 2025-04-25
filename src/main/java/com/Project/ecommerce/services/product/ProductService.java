package com.Project.ecommerce.services.product;

import com.Project.ecommerce.co.product.AddProductCO;
import com.Project.ecommerce.co.product.AddProductVariationCO;
import com.Project.ecommerce.co.product.UpdateProductCO;
import com.Project.ecommerce.co.product.UpdateProductVariationCO;
import com.Project.ecommerce.dto.category.admin.CategoryResponseDTO;
import com.Project.ecommerce.dto.product.admin.AdminProductDTO;
import com.Project.ecommerce.dto.product.admin.AdminProductVariationDTO;
import com.Project.ecommerce.dto.product.admin.SellerDetailsDTO;
import com.Project.ecommerce.dto.product.customer.*;
import com.Project.ecommerce.dto.product.seller.SellerProductDTO;
import com.Project.ecommerce.dto.product.seller.SellerProductVariationDTO;
import com.Project.ecommerce.entities.category.Category;
import com.Project.ecommerce.entities.category.CategoryMetaDataFieldValues;
import com.Project.ecommerce.entities.product.Product;
import com.Project.ecommerce.entities.product.ProductVariation;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.exceptions.customExceptions.DuplicateResourceException;
import com.Project.ecommerce.exceptions.customExceptions.InvalidResourceException;
import com.Project.ecommerce.exceptions.customExceptions.NonLeafCategoryException;
import com.Project.ecommerce.exceptions.customExceptions.ResourceNotFoundException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerProductEmailService sellerProductEmailService;
    private final SellerRepository sellerRepository;
    private final ProductVariationRepository productVariationRepository;
    private final ImageUtil imageUtil;
    private final ProductVariationUtil productVariationValidator;
    private final ProductUtil productValidator;
    private final CategoryService categoryService;
    private final AdminProductEmailService adminProductEmailService;
    private final MessageSource messageSource;

    public String addProduct(Principal principal, AddProductCO addProductCO) throws MessagingException {

        logger.info("Starting addProduct process for seller: {}", principal.getName());

        Seller seller = sellerRepository.findByEmail(principal.getName()).orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("seller.not.found", null, LocaleContextHolder.getLocale())));
        String categoryId = addProductCO.getCategoryId();
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("category.not.found", null, LocaleContextHolder.getLocale())));

        //category should be leaf category
        if (!category.getIsLeafCategory()) {
            logger.warn("Non-leaf category selected: {}", categoryId);
            throw new NonLeafCategoryException(messageSource.getMessage("select.leaf.category", null, LocaleContextHolder.getLocale()));
        }
        //unique product name
        logger.debug("Validating uniqueness of product name: {} for seller: {}", addProductCO.getName(), seller.getId());
        // Fetching all products for the given brand, seller, and category
        List<Product> existingProducts = productRepository.findByBrandAndSellerIdAndCategoryId(
                addProductCO.getBrand(), seller.getId(), categoryId
        );

        // Check if any product exists with the same name
        for (Product existingProduct : existingProducts) {
            if (existingProduct.getName() != null &&
                    existingProduct.getName().equalsIgnoreCase(addProductCO.getName())) {
                logger.warn("Duplicate product name '{}' found for brand '{}' and seller '{}'",
                        addProductCO.getName(), addProductCO.getBrand(), seller.getId());
                throw new DuplicateResourceException(messageSource.getMessage("duplicate.product.name", null, LocaleContextHolder.getLocale()));
            }
        }

        Product product = createProduct(addProductCO, category, seller);

        productRepository.save(product);
        logger.info("Product saved successfully: {} (ID: {})", product.getName(), product.getId());
        //sending email to admin
        sellerProductEmailService.sendNewProductActivationEmail(principal.getName(), product, seller);
        logger.info("Notification email sent to admin for new product by seller: {}", seller.getEmail());

        return messageSource.getMessage("success.product.added", null, LocaleContextHolder.getLocale());
    }

    private Product createProduct(AddProductCO addProductCO, Category category, Seller seller) {
        Product product = new Product();

        product.setSeller(seller);
        product.setCategory(category);
        product.setName(addProductCO.getName().toLowerCase());
        product.setBrand(addProductCO.getBrand().toLowerCase());

        Optional.ofNullable(addProductCO.getDescription()).ifPresent(product::setDescription);
        Optional.ofNullable(addProductCO.getIsCancellable()).ifPresent(product::setIsCancellable);
        Optional.ofNullable(addProductCO.getIsReturnable()).ifPresent(product::setIsReturnable);

        return product;
    }

    public String addProductVariation(Principal principal, AddProductVariationCO addProductVariationCO) throws IOException {
        logger.info("Starting to add product variation for productId: {}", addProductVariationCO.getProductId());

        String sellerEmail = principal.getName();
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("seller.not.found", null, LocaleContextHolder.getLocale())));
        Product product = productRepository.findById(addProductVariationCO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Product id"));
        productValidator.validateIsSellerProduct(seller, product);
        logger.debug("Validating product with id: {}", product.getId());
        productVariationValidator.validateAndFetchProduct(product);

        logger.debug("Creating product variation for productId: {}", product.getId());
        ProductVariation productVariation = createProductVariation(product, addProductVariationCO);
        productVariationRepository.save(productVariation);
        logger.info("Product variation saved successfully with ID: {}", productVariation.getId());
        return "Product variation added successfully for your product";
    }

    private ProductVariation createProductVariation(Product product, AddProductVariationCO co) throws IOException {
        ProductVariation productVariation = new ProductVariation();
        productVariation.setProduct(product);
        productVariation.setPrice(co.getPrice());
        productVariation.setQuantityAvailable(co.getQuantityAvailable());
        String imageName = imageUtil.saveProductVariationImage(co.getPrimaryImage(), product.getId(), "primary");
        productVariation.setPrimaryImageName(imageName);
        if (co.getSecondaryImages() != null) {
            int sequence = 1;
            for (MultipartFile image : co.getSecondaryImages()) {
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

    public SellerProductDTO getSellerProduct(Principal principal, String productId) {
        String sellerEmail = principal.getName();
        logger.info("Fetching seller product details for seller: {} and productId: {}", sellerEmail, productId);

        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("seller.not.found", null, LocaleContextHolder.getLocale())));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("product.not.found", null, LocaleContextHolder.getLocale())));
        logger.debug("Validating product (id: {}) is not deleted", productId);
        productValidator.validateIsDeletedProduct(product);
        logger.debug("Validating product (id: {}) belongs to seller (id: {})", productId, seller.getId());
        productValidator.validateIsSellerProduct(seller, product);
        logger.info("Successfully fetched seller product details for productId: {}", productId);
        return createSellerProductDTO(product);
    }

    private SellerProductDTO createSellerProductDTO(Product product) {
        SellerProductDTO dto = new SellerProductDTO();

        dto.setName(product.getName());
        dto.setBrand(product.getBrand());
        dto.setDescription(product.getDescription());
        dto.setIsReturnable(product.getIsReturnable());
        dto.setIsCancellable(product.getIsCancellable());
        dto.setIsActive(product.getIsActive());

        Category category = product.getCategory();
        List<CategoryMetaDataFieldValues> metaDataFieldValues = category.getMetadataFieldValues();
        CategoryResponseDTO categoryDTO = categoryService.saveCategoryInDTO(category.getId(), category, metaDataFieldValues);

        dto.setCategory(categoryDTO);

        return dto;
    }

    public SellerProductVariationDTO getSellerProductVariation(Principal principal, String productVariationId) {
        String sellerEmail = principal.getName();
        logger.info("Fetching product variation details for seller: {} and variationId: {}", sellerEmail, productVariationId);
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("seller.not.found", null, LocaleContextHolder.getLocale())));
        ProductVariation productVariation = productVariationRepository.findById(productVariationId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("product.variation.not.found", null, LocaleContextHolder.getLocale())));

        logger.debug("Validating that product");
        productVariationValidator.validateIsSellerProductVariation(seller, productVariation);
        Product product = productVariation.getProduct();
        productValidator.validateIsDeletedProduct(product);
        logger.info("Successfully fetched product variation details for variationId: {}", productVariationId);
        return createSellerProductVariationDTO(productVariation, product);
    }

    private SellerProductVariationDTO createSellerProductVariationDTO(ProductVariation productVariation, Product product) {
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

    public List<SellerProductDTO> getSellerAllProducts(Principal principal, int pageNo, int pageSize, String sortField, String direction, String query) {
        logger.info("Fetching all products for seller");
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));

        String sellerEmail = principal.getName();
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("seller.not.found", null, LocaleContextHolder.getLocale())));

        Specification<Product> specification = ProductSpecifications.bySeller(seller.getId()).and(ProductSpecifications.isNotDeleted());
        if (query != null && !query.isBlank()) {
            specification = specification.and(ProductSpecifications.fromQueryString(query, false));
        }

        Page<Product> sellerProducts = productRepository.findAll(specification, pageable);
        List<SellerProductDTO> sellerProductDTOs = new ArrayList<>();
        for (Product sellerProduct : sellerProducts.getContent()) {
            SellerProductDTO dto = createSellerProductDTO(sellerProduct);
            sellerProductDTOs.add(dto);
        }
        logger.info("Retrieved {} products for seller: {}", sellerProductDTOs.size(), sellerEmail);
        return sellerProductDTOs;
    }

    public List<SellerProductVariationDTO> getSellerAllProductVariations(Principal principal, String productId, int pageNo, int pageSize, String sortField, String direction, String query) {
        logger.info("Fetching product variations for seller");

        String sellerEmail = principal.getName();
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("seller.not.found", null, LocaleContextHolder.getLocale())));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("product.not.found", null, LocaleContextHolder.getLocale())));
        productValidator.validateIsDeletedProduct(product);
        productValidator.validateIsSellerProduct(seller, product);

        Page<ProductVariation> sellerProductVariations;
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));

        Specification<ProductVariation> specification = ProductVariationSpecification.byProductId(productId);

        if (query != null && !query.isBlank()) {
            logger.debug("Applying query filter to product variations: {}", query);
            specification = specification.and(ProductVariationSpecification.fromQueryString(query));
            sellerProductVariations = productVariationRepository.findAll(specification, pageable);
        } else {
            sellerProductVariations = productVariationRepository.findAll(specification, pageable);
        }

        List<SellerProductVariationDTO> sellerProductVariationDTOS = new ArrayList<>();
        for (ProductVariation sellerProductVariation : sellerProductVariations.getContent()) {
            SellerProductVariationDTO sellerProductVariationDTO = createSellerProductVariationDTO(sellerProductVariation, product);
            sellerProductVariationDTOS.add(sellerProductVariationDTO);
        }
        logger.info("Total product variations fetched: {}", sellerProductVariationDTOS.size());
        return sellerProductVariationDTOS;
    }

    public String deleteSellerProduct(Principal principal, String productId) {
        String sellerEmail = principal.getName();
        logger.info("Attempting to delete product for seller: {}, productId: {}", sellerEmail, productId);

        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("seller.not.found", null, LocaleContextHolder.getLocale())));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("product.not.found", null, LocaleContextHolder.getLocale())));
        productValidator.validateIsSellerProduct(seller, product);

        productRepository.deleteById(productId);
        logger.info("Product with ID: {} deleted successfully for seller: {}", productId, sellerEmail);
        return messageSource.getMessage("success.product.deleted", null, LocaleContextHolder.getLocale());
    }

    public String updateSellerProduct(Principal principal, String productId, UpdateProductCO updateProductCO) {
        logger.info("Attempting to update product for seller: {}, productId: {}", principal.getName(), productId);

        Seller seller = sellerRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("seller.not.found", null, LocaleContextHolder.getLocale())));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("product.not.found", null, LocaleContextHolder.getLocale())));
        Category category = categoryRepository.findById(product.getCategory().getId()).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("category.not.found", null, LocaleContextHolder.getLocale())));
        productValidator.validateIsSellerProduct(seller, product);

        List<Product> existingProducts = productRepository.findByBrandAndSellerIdAndCategoryId(
                product.getBrand(), seller.getId(), category.getId()
        );

        for (Product existingProduct : existingProducts) {
            if (existingProduct.getName() != null &&
                    existingProduct.getName().equalsIgnoreCase(updateProductCO.getName()) &&
                    !existingProduct.getId().equals(productId)) {
                throw new DuplicateResourceException(messageSource.getMessage("duplicate.product.name", null, LocaleContextHolder.getLocale()));
            }
        }

        Optional.ofNullable(updateProductCO.getName()).ifPresent(product::setName);
        Optional.ofNullable(updateProductCO.getDescription()).ifPresent(product::setDescription);
        Optional.ofNullable(updateProductCO.getIsCancellable()).ifPresent(product::setIsCancellable);
        Optional.ofNullable(updateProductCO.getIsReturnable()).ifPresent(product::setIsReturnable);

        productRepository.save(product);
        logger.info("Product with ID: {} updated successfully for seller: {}", productId, seller.getEmail());
        return messageSource.getMessage("success.product.updated", null, LocaleContextHolder.getLocale());
    }

    public String updateProductVariation(Principal principal, String productVariationId, UpdateProductVariationCO updateProductVariationCO, MultipartFile primaryImage, List<MultipartFile> secondaryImages) throws IOException {
        logger.info("Attempting to update product variation for productVariationId: {}", productVariationId);

        ProductVariation productVariation = productVariationRepository.findById(productVariationId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("product.variation.not.found", null, LocaleContextHolder.getLocale())));
        Seller seller = sellerRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("seller.not.found", null, LocaleContextHolder.getLocale())));

        // will come null when product is deleted
        if (productVariation.getProduct() == null) {
            throw new ResourceNotFoundException(messageSource.getMessage("product.not.found", null, LocaleContextHolder.getLocale()));
        }
        Product product = productRepository.findById(productVariation.getProduct().getId()).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("product.not.found", null, LocaleContextHolder.getLocale())));
        productVariationValidator.validateIsSellerProductVariation(seller, productVariation);
        productVariationValidator.validateAndFetchProduct(product);
        Optional.ofNullable(updateProductVariationCO.getQuantityAvailable()).ifPresent(productVariation::setQuantityAvailable);
        Optional.ofNullable(updateProductVariationCO.getPrice()).ifPresent(productVariation::setPrice);
        if (updateProductVariationCO.getMetadata() != null) {
            Map<String, String> existingMetadata = JsonUtil.jsonToMap(productVariation.getMetaData());
            Map<String, String> updates = updateProductVariationCO.getMetadata();

            productVariationValidator.validateAllowedMetadata(product, updates);

            updates.forEach((key, value) -> {
                if (value != null && !value.isBlank()) {
                    existingMetadata.put(key, value.toLowerCase());
                }
            });

            productVariation.setMetaData(JsonUtil.mapToJson(existingMetadata));
        }

        Optional.ofNullable(updateProductVariationCO.getIsActive()).ifPresent(productVariation::setIsActive);

        if (primaryImage != null) {
            String imageName = imageUtil.saveProductVariationImage(primaryImage, product.getId(), "primary");
            productVariation.setPrimaryImageName(imageName);
        }

        logger.info("Primary image for product variation ID: {} has been saved", productVariationId);

        if (secondaryImages != null) {
            int sequence = 1;
            for (MultipartFile image : secondaryImages) {
                imageUtil.saveProductVariationImage(image, product.getId(), "secondary_" + sequence);
                sequence++;
            }
        }

        productVariationRepository.save(productVariation);
        logger.info("Product variation ID: {} updated successfully for seller: {}", productVariationId, principal.getName());
        return messageSource.getMessage("success.product.variation.updated", null, LocaleContextHolder.getLocale());
    }

    public CustomerProductDTO getCustomerProduct(String productId) {
        logger.info("Attempting to fetch product for productId: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("product.not.found", null, LocaleContextHolder.getLocale())));
        productValidator.validateIsDeletedProduct(product);
        productValidator.validateIsActiveProduct(product);
        productValidator.containsValidProductVariation(product);
        logger.info("Successfully fetched product details for productId: {}", productId);
        return createCustomerProductDTO(product, productId);
    }

    private CustomerProductDTO createCustomerProductDTO(Product product, String productId) {
        CustomerProductDTO customerProductDTO = new CustomerProductDTO();
        customerProductDTO.setName(product.getName());
        customerProductDTO.setBrand(product.getBrand());
        customerProductDTO.setDescription(product.getDescription());
        customerProductDTO.setIsCancellable(product.getIsCancellable());
        customerProductDTO.setIsReturnable(product.getIsReturnable());
        List<CustomerProductCategoryDTO> categoryDTOs = createCustomerProductCategoryDTOs(product);
        customerProductDTO.setCategory(categoryDTOs);

        List<CustomerProductVariationDTO> productVariationDTOs = new ArrayList<>();
        List<ProductVariation> productVariations = product.getProductVariations();
        for (ProductVariation productVariation : productVariations) {
            CustomerProductVariationDTO dto = new CustomerProductVariationDTO();
            dto.setPrimaryImage(imageUtil.getProductVariationPrimaryImage(productId));
            dto.setSecondaryImages(imageUtil.getProductVariationSecondaryImages(productId));
            dto.setMetadata(JsonUtil.jsonToMap(productVariation.getMetaData()));
            dto.setPrice(productVariation.getPrice());

            productVariationDTOs.add(dto);
        }
        customerProductDTO.setProductVariation(productVariationDTOs);

        return customerProductDTO;
    }

    private List<CustomerProductCategoryDTO> createCustomerProductCategoryDTOs(Product product) {
        List<CustomerProductCategoryDTO> categoryDTOs = new ArrayList<>();

        Category category = product.getCategory();
        while (category.getParentCategory() != null) {
            CustomerProductCategoryDTO categoryDTO = new CustomerProductCategoryDTO();
            categoryDTO.setCategoryId(category.getId());
            categoryDTO.setCategoryName(category.getName());
            categoryDTO.setCategoryParentId(category.getParentCategory().getId());

            categoryDTOs.add(categoryDTO);
            category = category.getParentCategory();
        }
        CustomerProductCategoryDTO categoryDTO = new CustomerProductCategoryDTO();
        categoryDTO.setCategoryId(category.getId());
        categoryDTO.setCategoryName(category.getName());
        categoryDTO.setCategoryParentId("NULL");
        categoryDTOs.add(categoryDTO);
        return categoryDTOs;
    }

    public List<CustomerAllProductsDTO> getCustomerAllProduct(int pageNo, int pageSize, String sortField, String direction, String query, String categoryId) {
        logger.info("Fetching all products for customer");
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("category.not.found", null, LocaleContextHolder.getLocale())));

        List<String> leafCategoryIds = new ArrayList<>();

        if (category.getIsLeafCategory()) {
            leafCategoryIds.add(categoryId);
        } else {
            List<Category> leafCategories = findAssociatedLeafCategories(category);
            for (Category leafCategory : leafCategories) {
                leafCategoryIds.add(leafCategory.getId());
            }
        }

        Specification<Product> specification = ProductSpecifications.byCategories(leafCategoryIds).and(ProductSpecifications.isNotDeleted().and(ProductSpecifications.isActive()));
        if (query != null && !query.isBlank()) {
            specification = ProductSpecifications.byCategories(leafCategoryIds).and(ProductSpecifications.isNotDeleted().and(ProductSpecifications.isActive().and(ProductSpecifications.fromQueryString(query, false))));
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));

        Page<Product> products = productRepository.findAll(specification, pageable);

        List<CustomerAllProductsDTO> productsDTOs = new ArrayList<>();
        for (Product product : products) {
            boolean hasActiveVariation = product.getProductVariations().stream()
                    .anyMatch(variation -> Boolean.TRUE.equals(variation.getIsActive()));

            if (!hasActiveVariation) {
                logger.debug("Skipping product ID: {} due to no active variations", product.getId());
                continue; // skip this product
            }
            Category associatedCategory = product.getCategory();
            CustomerAllProductsDTO dto = createCustomerAllProductDTO(product, associatedCategory, associatedCategory.getId());
            productsDTOs.add(dto);
            logger.debug("Added product ID: {} to the result DTO list", product.getId());
        }
        return productsDTOs;
    }

    private List<Category> findAssociatedLeafCategories(Category category) {
        List<Category> leafCategories = new ArrayList<>();
        Queue<Category> queue = new LinkedList<>();
        queue.add(category);
        while (!queue.isEmpty()) {
            Category currentCategory = queue.poll();

            if (currentCategory.getIsLeafCategory()) {
                leafCategories.add(currentCategory);
            } else {
                queue.addAll(categoryRepository.findAllByParentCategoryId(currentCategory.getId()).orElseThrow(() -> new InvalidResourceException("Invalid category id provided")));
            }
        }
        return leafCategories;
    }

    private CustomerAllProductsDTO createCustomerAllProductDTO(Product product, Category category, String categoryId) {
        CustomerAllProductsDTO productDTO = new CustomerAllProductsDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setBrand(product.getBrand());
        productDTO.setRetailer(product.getSeller().getCompanyName());

        List<CustomerAllProductsVariationsDTO> productVariationsDTOs = new ArrayList<>();
        List<ProductVariation> productVariations = product.getProductVariations();
        for (ProductVariation productVariation : productVariations) {
            CustomerAllProductsVariationsDTO productsVariationDTO = new CustomerAllProductsVariationsDTO();
            productsVariationDTO.setProductVariationId(productVariation.getId());
            productsVariationDTO.setPrimaryImage(imageUtil.getProductVariationPrimaryImage(product.getId()));
            productsVariationDTO.setPrice(productVariation.getPrice());
            productVariationsDTOs.add(productsVariationDTO);
        }
        productDTO.setProductVariations(productVariationsDTOs);

        CustomerProductCategoryDTO categoryDTO = new CustomerProductCategoryDTO();
        categoryDTO.setCategoryId(categoryId);
        categoryDTO.setCategoryName(category.getName());

        if (category.getParentCategory().getId() == null) {
            categoryDTO.setCategoryParentId("NULL");
        } else {
            categoryDTO.setCategoryParentId(category.getParentCategory().getId());
        }
        productDTO.setCategories(categoryDTO);

        return productDTO;
    }

    public List<CustomerProductDTO> getCustomerSimilarProducts(int pageNo, int pageSize, String sortField, String direction, String query, String productId) {
        logger.info("Fetching similar products");
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("product.not.found", null, LocaleContextHolder.getLocale())));

        //giving similar products by printing rest products of that category
        //1. other products in that category
        //2. products of same brand

        Specification<Product> specification = ProductSpecifications.byCategories(List.of(product.getCategory().getId()))
                .and(ProductSpecifications.isActive()).and(ProductSpecifications.isNotDeleted())
                .and(ProductSpecifications.excludeProductId(productId));
        if (query != null && !query.isBlank()) {
            specification = specification.and(ProductSpecifications.fromQueryString(query, false));
        }
        if (product.getBrand() != null && !product.getBrand().isBlank()) {
            specification = specification.and(ProductSpecifications.byBrand(product.getBrand()));
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));
        Page<Product> similarProducts = productRepository.findAll(specification, pageable);

        List<CustomerProductDTO> similarProductsDTOs = new ArrayList<>();

        for (Product similarProduct : similarProducts.getContent()) {
            CustomerProductDTO similarProductDTO = createCustomerProductDTO(similarProduct, similarProduct.getId());
            similarProductsDTOs.add(similarProductDTO);
            logger.debug("Added similar product ID: {} to the result DTO list", similarProduct.getId());
        }
        return similarProductsDTOs;
    }

    public AdminProductDTO getAdminProduct(String productId) {
        logger.info("Fetching product details for admin with productId: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("product.not.found", null, LocaleContextHolder.getLocale())));
        logger.info("Successfully retrieved product details for productId: {}", productId);
        return createAdminProductDTO(product);
    }

    private AdminProductDTO createAdminProductDTO(Product product) {
        AdminProductDTO adminProductDTO = new AdminProductDTO();
        adminProductDTO.setName(product.getName());
        adminProductDTO.setBrand(product.getBrand());
        adminProductDTO.setDescription(product.getDescription());
        adminProductDTO.setIsReturnable(product.getIsReturnable());
        adminProductDTO.setIsCancellable(product.getIsCancellable());
        adminProductDTO.setIsActive(product.getIsActive());
        adminProductDTO.setIsDeleted(product.getIsDeleted());

        List<ProductVariation> productVariations = product.getProductVariations();
        List<AdminProductVariationDTO> productVariationDTOs = new ArrayList<>();
        for (ProductVariation productVariation : productVariations) {
            AdminProductVariationDTO productVariationDTO = new AdminProductVariationDTO();
            productVariationDTO.setProductVariationId(productVariation.getId());
            productVariationDTO.setPrimaryImage(imageUtil.getProductVariationPrimaryImage(product.getId()));

            productVariationDTOs.add(productVariationDTO);
        }
        adminProductDTO.setVariations(productVariationDTOs);

        Category category = product.getCategory();
        CustomerProductCategoryDTO categoryDTO = new CustomerProductCategoryDTO();
        categoryDTO.setCategoryId(category.getId());
        categoryDTO.setCategoryName(category.getName());
        if (category.getParentCategory().getId() == null) {
            categoryDTO.setCategoryParentId("null");
        } else {
            categoryDTO.setCategoryParentId(category.getParentCategory().getId());
        }
        adminProductDTO.setCategory(categoryDTO);

        SellerDetailsDTO sellerDetailsDTO = new SellerDetailsDTO();
        sellerDetailsDTO.setSellerId(product.getSeller().getId());
        sellerDetailsDTO.setName(product.getSeller().getFirstName() + " " + product.getSeller().getLastName());
        sellerDetailsDTO.setIsActiveSeller(product.getSeller().getIsActive());
        sellerDetailsDTO.setCompanyContact(product.getSeller().getCompanyContact());
        sellerDetailsDTO.setCompanyName(product.getSeller().getCompanyName());
        sellerDetailsDTO.setGST(product.getSeller().getGST());

        adminProductDTO.setSellerDetails(sellerDetailsDTO);

        return adminProductDTO;
    }

    public List<AdminProductDTO> getAdminAllProducts(int pageNo, int pageSize, String sortField, String direction, String query) {
        logger.info("Fetching all products for admin");
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));
        Specification<Product> specification = null;
        if (query != null && !query.isBlank()) {
            specification = ProductSpecifications.fromQueryString(query, true);
        }
        Page<Product> allProducts = productRepository.findAll(specification, pageable);
        logger.info("Fetched {} products for admin", allProducts.getContent().size());
        List<AdminProductDTO> allProductsDTOs = new ArrayList<>();
        for (Product product : allProducts.getContent()) {
            AdminProductDTO adminProductDTO = createAdminProductDTO(product);
            allProductsDTOs.add(adminProductDTO);
        }
        return allProductsDTOs;
    }

    public String activateDeactivateProduct(String productId, String action) throws MessagingException {
        logger.info("Attempting to {} product with ID: {}", action, productId);
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("product.not.found", null, LocaleContextHolder.getLocale())));

        Boolean isProductActive = product.getIsActive();
        if (Objects.equals(action, "deactivate")) {
            if (isProductActive) {
                product.setIsActive(false);
                productRepository.save(product);
                adminProductEmailService.sendProductDeactivationEmail(product.getSeller().getEmail(), product);
                logger.info("Product with ID: {} deactivated successfully", productId);
                return messageSource.getMessage("product.deactivated", null, LocaleContextHolder.getLocale());
            } else {
                logger.info("Product with ID: {} is already deactivated", productId);
                return messageSource.getMessage("product.already.deactivated", null, LocaleContextHolder.getLocale());
            }
        } else if (Objects.equals(action, "activate")) {
            if (!isProductActive) {
                product.setIsActive(true);
                productRepository.save(product);
                adminProductEmailService.sendProductActivationEmail(product.getSeller().getEmail(), product);
                logger.info("Product with ID: {} activated successfully", productId);
                return messageSource.getMessage("product.activated", null, LocaleContextHolder.getLocale());
            } else {
                logger.info("Product with ID: {} is already activated", productId);
                return messageSource.getMessage("product.already.activated", null, LocaleContextHolder.getLocale());
            }
        }
        return messageSource.getMessage("fail.activate.deactivate", null, LocaleContextHolder.getLocale());
    }
}