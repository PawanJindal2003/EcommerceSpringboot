package com.Project.ecommerce.services.product.seller;

import com.Project.ecommerce.co.product.AddProductCO;
import com.Project.ecommerce.co.product.AddProductVariationCO;
import com.Project.ecommerce.entities.category.Category;
import com.Project.ecommerce.entities.product.Product;
import com.Project.ecommerce.entities.product.ProductVariation;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.exceptions.customExceptions.*;
import com.Project.ecommerce.repositories.category.CategoryRepository;
import com.Project.ecommerce.repositories.product.ProductRepository;
import com.Project.ecommerce.repositories.product.ProductVariationRepository;
import com.Project.ecommerce.repositories.user.SellerRepository;
import com.Project.ecommerce.utils.ImageUtil;
import com.Project.ecommerce.utils.JsonUtil;
import com.Project.ecommerce.utils.validator.ProductVariationUtil;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private SellerProductEmailService sellerProductEmailService;
    private SellerRepository sellerRepository;
    private ProductVariationRepository productVariationRepository;
    private ImageUtil imageUtil;
    private ProductVariationUtil productVariationValidator;

    public String addProduct(Principal principal, AddProductCO addProductCO) throws MessagingException {
        Seller seller = sellerRepository.findByEmail(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("Seller not found"));
        String categoryId = addProductCO.getCategoryId();
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Category not found"));

        //category should be leaf category
        if (!category.getIsLeafCategory()) {
            throw new NonLeafCategoryException("Please select a leaf category to add the product");
        }
        //unique product name
        if (productRepository.getNameByBrandAndSellerIdAndCategoryId(addProductCO.getBrand(), seller.getId(), categoryId).getName().equals(addProductCO.getName())) {
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
}
