package com.Project.ecommerce.config;

import com.Project.ecommerce.entities.category.Category;
import com.Project.ecommerce.entities.product.Product;
import com.Project.ecommerce.entities.product.ProductVariation;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.repositories.category.CategoryRepository;
import com.Project.ecommerce.repositories.product.ProductRepository;
import com.Project.ecommerce.repositories.product.ProductVariationRepository;
import com.Project.ecommerce.repositories.user.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductBootstrap implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(ProductBootstrap.class);

    private static final String TINY_PNG_BASE64 =
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==";

    private final ProductRepository productRepository;
    private final ProductVariationRepository productVariationRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;

    @Value("${app.seed.dummy-products:false}")
    private boolean seedDummyProducts;

    @Value("${product.variations.upload-dir}")
    private String variationUploadDir;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (!seedDummyProducts) {
            return;
        }

        Seller seller = sellerRepository.findByEmail("testseller2@example.com")
                .or(() -> sellerRepository.findAll().stream().filter(s -> Boolean.TRUE.equals(s.getIsActive())).findFirst())
                .orElse(null);

        if (seller == null) {
            logger.warn("Dummy product seed skipped: no active seller found");
            return;
        }

        Category category = resolveElectronicsCategory();
        seedProductIfMissing(seller, category, "wireless bluetooth headphones", "sony",
                "Noise-cancelling over-ear headphones with 30-hour battery life", 4999L, 25);
        seedProductIfMissing(seller, category, "smart watch series x", "samsung",
                "Fitness tracking smartwatch with AMOLED display", 12999L, 15);
        seedProductIfMissing(seller, category, "usb-c charging cable", "anker",
                "Durable braided USB-C cable, 2m length", 899L, 100);
        seedProductIfMissing(seller, category, "laptop stand aluminum", "amazon basics",
                "Ergonomic adjustable aluminum laptop stand", 2499L, 40);
        ensureVariationForExistingProduct(seller, "shoes", "puma", 3999L, 30);

        logger.info("Dummy product seeding completed for seller {}", seller.getEmail());
    }

    private Category resolveElectronicsCategory() {
        List<Category> rootCategories = categoryRepository.findAllByParentCategoryIdIsNull()
                .orElse(List.of());
        Optional<Category> electronics = rootCategories.stream()
                .filter(category -> "electronics".equalsIgnoreCase(category.getName()))
                .findFirst();
        if (electronics.isPresent()) {
            return electronics.get();
        }

        Category category = new Category();
        category.setName("electronics");
        category.setIsLeafCategory(true);
        return categoryRepository.save(category);
    }

    private void seedProductIfMissing(Seller seller, Category category, String name, String brand,
                                      String description, long pricePaise, int quantity) throws IOException {
        boolean exists = productRepository.findByBrandAndSellerIdAndCategoryId(
                brand.toLowerCase(), seller.getId(), category.getId()
        ).stream().anyMatch(product -> product.getName().equalsIgnoreCase(name));

        if (exists) {
            logger.debug("Skipping existing dummy product: {}", name);
            return;
        }

        Product product = new Product();
        product.setName(name.toLowerCase());
        product.setBrand(brand.toLowerCase());
        product.setDescription(description);
        product.setCategory(category);
        product.setSeller(seller);
        product.setIsCancellable(true);
        product.setIsReturnable(true);
        product.setIsActive(true);
        product.setIsDeleted(false);
        productRepository.save(product);

        createVariation(product, pricePaise, quantity);
        logger.info("Seeded dummy product: {} ({})", name, product.getId());
    }

    private void ensureVariationForExistingProduct(Seller seller, String productName, String brand,
                                                   long pricePaise, int quantity) throws IOException {
        List<Product> matches = productRepository.findAll().stream()
                .filter(product -> seller.getId().equals(product.getSeller().getId()))
                .filter(product -> product.getName().equalsIgnoreCase(productName))
                .filter(product -> product.getBrand().equalsIgnoreCase(brand))
                .toList();

        for (Product product : matches) {
            List<ProductVariation> variations = product.getProductVariations();
            if (variations != null && !variations.isEmpty()) {
                continue;
            }
            product.setIsActive(true);
            productRepository.save(product);
            createVariation(product, pricePaise, quantity);
            logger.info("Added dummy variation to existing product: {} ({})", productName, product.getId());
        }
    }

    private void createVariation(Product product, long price, int quantity) throws IOException {
        String imageFileName = product.getId() + "_primary.png";
        writePlaceholderImage(product.getId(), imageFileName);

        ProductVariation variation = new ProductVariation();
        variation.setProduct(product);
        variation.setPrice(price);
        variation.setQuantityAvailable(quantity);
        variation.setMetaData("{}");
        variation.setPrimaryImageName(imageFileName);
        variation.setIsActive(true);
        productVariationRepository.save(variation);
    }

    private void writePlaceholderImage(String productId, String imageFileName) throws IOException {
        Path variationDir = Paths.get(variationUploadDir, productId);
        Files.createDirectories(variationDir);
        Path imagePath = variationDir.resolve(imageFileName);
        if (!Files.exists(imagePath)) {
            byte[] pngBytes = Base64.getDecoder().decode(TINY_PNG_BASE64);
            Files.write(imagePath, pngBytes);
        }
    }
}
