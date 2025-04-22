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
            throw new DuplicateResourceException("Product name already exists, please add a unique product name.");
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
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Product id"));
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
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
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
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
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
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

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
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
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
        Seller seller = sellerRepository.findByEmail(sellerEmail).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productValidator.validateIsSellerProduct(seller, product);

        productRepository.deleteById(productId);
        return "Product deleted successfully";
    }

    public String updateSellerProduct(Principal principal, String productId, UpdateProductCO updateProductCO){
        Seller seller = sellerRepository.findByEmail(principal.getName()).orElseThrow(()->new ResourceNotFoundException("Seller not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Category category = categoryRepository.findById(product.getCategory().getId()).orElseThrow(()->new ResourceNotFoundException("Category not found"));
        productValidator.validateIsSellerProduct(seller, product);

        Product existingProduct = productRepository.getNameByBrandAndSellerIdAndCategoryId(
                product.getBrand(), seller.getId(), category.getId()
        );

        if (existingProduct != null && existingProduct.getName().equalsIgnoreCase(updateProductCO.getName())) {
            throw new DuplicateResourceException("Product name already exists, please add a unique product name.");
        }

        product.setName(updateProductCO.getName());
        product.setDescription(updateProductCO.getDescription());
        product.setIsCancellable(updateProductCO.getIsCancellable());
        product.setIsReturnable(updateProductCO.getIsReturnable());

        productRepository.save(product);
        return "Product has been updated successfully";
    }

    public String updateProductVariation(Principal principal, String productVariationId, UpdateProductVariationCO updateProductVariationCO, MultipartFile primaryImage, List<MultipartFile> secondaryImages) throws IOException {
        ProductVariation productVariation = productVariationRepository.findById(productVariationId).orElseThrow(()->new ResourceNotFoundException("Product variation not found"));
        Seller seller = sellerRepository.findByEmail(principal.getName()).orElseThrow(()->new ResourceNotFoundException("Seller not found"));
        Product product = productRepository.findById(productVariation.getProduct().getId()).orElseThrow(()->new ResourceNotFoundException("Product not found"));
        productVariationValidator.validateIsSellerProductVariation(seller, productVariation);

        productVariation.setQuantityAvailable(updateProductVariationCO.getQuantityAvailable());
        productVariation.setPrice(updateProductVariationCO.getPrice());
        productVariation.setMetaData(JsonUtil.mapToJson(updateProductVariationCO.getMetadata()));
        productVariation.setIsActive(updateProductVariationCO.getIsActive());

        String imageName = imageUtil.saveProductVariationImage(primaryImage, product.getId(), "primary");
        productVariation.setPrimaryImageName(imageName);


        if (secondaryImages != null) {
            int sequence = 1;
            for (MultipartFile image : secondaryImages) {
                imageUtil.saveProductVariationImage(image, product.getId(), "secondary_" + sequence);
                sequence++;
            }
        }

        productVariationRepository.save(productVariation);
        return "Product variation has been updated successfully.";
    }

    public CustomerProductDTO getCustomerProduct(String productId){
        Product product = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found"));
        productValidator.validateIsDeletedProduct(product);
        productValidator.validateIsActiveProduct(product);
        productValidator.containsValidProductVariation(product);

        return createCustomerProductDTO(product, productId);
    }

    private CustomerProductDTO createCustomerProductDTO(Product product, String productId){
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
        for(ProductVariation productVariation:productVariations){
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
        while(category.getParentCategory() != null){
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

    public List<CustomerAllProductsDTO> getCustomerAllProduct(int pageNo, int pageSize, String sortField, String direction, String query, String categoryId){
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category not found"));

        List<String> leafCategoryIds = new ArrayList<>();

        if(category.getIsLeafCategory()){
            leafCategoryIds.add(categoryId);
        }
        else{
            List<Category> leafCategories = findAssociatedLeafCategories(category);
            for(Category leafCategory : leafCategories){
                leafCategoryIds.add(leafCategory.getId());
            }
        }

        Specification<Product> specification = ProductSpecifications.byCategories(leafCategoryIds).and(ProductSpecifications.isNotDeleted().and(ProductSpecifications.isActive()));
        if(query != null && !query.isBlank()){
            specification = ProductSpecifications.byCategories(leafCategoryIds).and(ProductSpecifications.isNotDeleted().and(ProductSpecifications.isActive().and(ProductSpecifications.fromQueryString(query))));
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));

        Page<Product> products  = productRepository.findAll(specification, pageable);

        List<CustomerAllProductsDTO> productsDTOs = new ArrayList<>();
        for(Product product:products){
            productValidator.containsValidProductVariation(product);
            Category associatedCategory = product.getCategory();
            CustomerAllProductsDTO dto = createCustomerAllProductDTO(product, associatedCategory, associatedCategory.getId());
            productsDTOs.add(dto);
        }
        return productsDTOs;
    }

    private List<Category> findAssociatedLeafCategories(Category category){
        List<Category> leafCategories = new ArrayList<>();
        Queue<Category> queue = new LinkedList<>();
        queue.add(category);
        while (!queue.isEmpty()){
            Category currentCategory = queue.poll();

            if(currentCategory.getIsLeafCategory()){
                leafCategories.add(currentCategory);
            }
            else{
                queue.addAll(categoryRepository.findAllByParentCategoryId(currentCategory.getId()).orElseThrow(()-> new InvalidResourceException("Invalid category id provided")));
            }
        }
        return leafCategories;
    }

    private CustomerAllProductsDTO createCustomerAllProductDTO(Product product, Category category, String categoryId){
        CustomerAllProductsDTO productDTO = new CustomerAllProductsDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setBrand(product.getBrand());
        productDTO.setRetailer(product.getSeller().getCompanyName());

        List<CustomerAllProductsVariationsDTO> productVariationsDTOs = new ArrayList<>();
        List<ProductVariation> productVariations = product.getProductVariations();
        for(ProductVariation productVariation:productVariations){
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

        if(category.getParentCategory().getId() == null){
            categoryDTO.setCategoryParentId("NULL");
        }
        else{
            categoryDTO.setCategoryParentId(category.getParentCategory().getId());
        }
        productDTO.setCategories(categoryDTO);

        return productDTO;
    }

    public List<CustomerProductDTO> getCustomerSimilarProducts(int pageNo, int pageSize, String sortField, String direction, String query, String productId){
        Product product = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product not found"));

        //giving similar products by printing rest products of that category
        //1. other products in that category
        //2. products of same brand

        Specification<Product> specification = ProductSpecifications.isActive().and(ProductSpecifications.isNotDeleted()).and(ProductSpecifications.byCategories(List.of(product.getCategory().getId())));
        if(query!=null && !query.isBlank()){
            specification = specification.and(ProductSpecifications.fromQueryString(query));
        }
        if(product.getBrand() != null && !product.getBrand().isBlank()){
            specification = specification.and(ProductSpecifications.byBrand(product.getBrand())).and(ProductSpecifications.excludeProductId(productId));
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));
        Page<Product> similarProducts = productRepository.findAll(specification, pageable);

        List<CustomerProductDTO> similarProductsDTOs= new ArrayList<>();

        for(Product similarProduct:similarProducts.getContent()){
            CustomerProductDTO similarProductDTO = createCustomerProductDTO(similarProduct, similarProduct.getId());
            similarProductsDTOs.add(similarProductDTO);
        }
        return similarProductsDTOs;
    }

    public AdminProductDTO getAdminProduct(String productId){
        Product product = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found"));

        return createAdminProductDTO(product);
    }

    private AdminProductDTO createAdminProductDTO(Product product){
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
        for(ProductVariation productVariation:productVariations){
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
        if(category.getParentCategory().getId() == null){
            categoryDTO.setCategoryParentId("null");
        }
        else{
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

    public List<AdminProductDTO> getAdminAllProducts(int pageNo, int pageSize, String sortField, String direction, String query){
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortField));
        Specification<Product> specification = null;
        if(query!=null && !query.isBlank()){
            specification = ProductSpecifications.fromQueryString(query);
        }
        Page<Product> allProducts = productRepository.findAll(specification, pageable);

        List<AdminProductDTO> allProductsDTOs = new ArrayList<>();
        for(Product product:allProducts.getContent()){
            AdminProductDTO adminProductDTO = createAdminProductDTO(product);
            allProductsDTOs.add(adminProductDTO);
        }
        return allProductsDTOs;
    }

    public String activateDeactivateProduct(String productId, String action) throws MessagingException {
        Product product = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found"));

        Boolean isProductActive = product.getIsActive();
        if(Objects.equals(action, "deactivate")){
            if(isProductActive){
                product.setIsActive(false);
                productRepository.save(product);
                adminProductEmailService.sendProductDeactivationEmail(product.getSeller().getEmail(), product);
                return "Product deactivated";
            }
            else{
                return "Product is already deactivated";
            }
        }
        else if(Objects.equals(action, "activate")){
            if(!isProductActive){
                product.setIsActive(true);
                productRepository.save(product);
                adminProductEmailService.sendProductActivationEmail(product.getSeller().getEmail(), product);
                return "Product activated";
            }
            else{
                return "Product is already activated";
            }
        }
        return "Failed to activate-deactivate product";
    }
}