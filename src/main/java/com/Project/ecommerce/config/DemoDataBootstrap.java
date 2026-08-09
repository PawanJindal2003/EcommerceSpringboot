package com.Project.ecommerce.config;

import com.Project.ecommerce.entities.address.Address;
import com.Project.ecommerce.entities.category.Category;
import com.Project.ecommerce.entities.category.CategoryMetaDataField;
import com.Project.ecommerce.entities.category.CategoryMetaDataFieldValues;
import com.Project.ecommerce.entities.category.CategoryMetaDataFieldValuesId;
import com.Project.ecommerce.entities.product.Product;
import com.Project.ecommerce.entities.product.ProductVariation;
import com.Project.ecommerce.entities.user.Customer;
import com.Project.ecommerce.entities.user.Role;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.repositories.category.CategoryMetaDataFieldRepository;
import com.Project.ecommerce.repositories.category.CategoryMetaDataFieldValuesRepository;
import com.Project.ecommerce.repositories.category.CategoryRepository;
import com.Project.ecommerce.repositories.product.ProductRepository;
import com.Project.ecommerce.repositories.product.ProductVariationRepository;
import com.Project.ecommerce.repositories.user.CustomerRepository;
import com.Project.ecommerce.repositories.user.RoleRepository;
import com.Project.ecommerce.repositories.user.SellerRepository;
import com.Project.ecommerce.repositories.user.UserRepository;
import com.Project.ecommerce.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Order(1)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.demo-data", havingValue = "true")
public class DemoDataBootstrap implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(DemoDataBootstrap.class);

    private static final String TINY_PNG_BASE64 =
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==";

    private static final String ADMIN_EMAIL = "admin@demo.com";
    private static final String CUSTOMER_EMAIL = "customer@demo.com";
    private static final String SELLER_EMAIL = "seller@demo.com";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMetaDataFieldRepository categoryMetaDataFieldRepository;
    private final CategoryMetaDataFieldValuesRepository categoryMetaDataFieldValuesRepository;
    private final ProductRepository productRepository;
    private final ProductVariationRepository productVariationRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${demo.password:Password@123}")
    private String demoPassword;

    @Value("${product.variations.upload-dir}")
    private String variationUploadDir;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        ensureRoles();
        ensureAdmin();
        ensureCustomer();
        Seller seller = ensureSeller();

        Category electronics = ensureCategory("electronics");
        linkMetadata(electronics, "color", List.of("black", "white", "silver"));
        linkMetadata(electronics, "storage", List.of("64gb", "128gb"));

        Category clothing = ensureCategory("clothing");
        linkMetadata(clothing, "size", List.of("s", "m", "l"));
        linkMetadata(clothing, "color", List.of("red", "blue", "black"));

        seedElectronicsProducts(seller, electronics);
        seedClothingProducts(seller, clothing);

        logger.info("Demo data seeding completed. Login with admin@demo.com, customer@demo.com, or seller@demo.com");
    }

    private void ensureRoles() {
        createRoleIfMissing("ADMIN");
        createRoleIfMissing("SELLER");
        createRoleIfMissing("CUSTOMER");
    }

    private void createRoleIfMissing(String authority) {
        if (roleRepository.findByAuthority(authority) == null) {
            roleRepository.save(new Role(authority));
            logger.info("Created role: {}", authority);
        }
    }

    private void ensureAdmin() {
        if (userRepository.findByEmail(ADMIN_EMAIL).isPresent()) {
            return;
        }
        User admin = new User();
        admin.setFirstName("Demo");
        admin.setLastName("Admin");
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(demoPassword));
        admin.setIsLocked(false);
        admin.setIsActive(true);
        admin.setRole(roleRepository.findByAuthority("ADMIN"));
        userRepository.save(admin);
        logger.info("Seeded admin user: {}", ADMIN_EMAIL);
    }

    private void ensureCustomer() {
        if (customerRepository.findByEmail(CUSTOMER_EMAIL).isPresent()) {
            return;
        }
        Customer customer = new Customer();
        customer.setFirstName("Demo");
        customer.setLastName("Customer");
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setPassword(passwordEncoder.encode(demoPassword));
        customer.setCustomerContact("9876543210");
        customer.setIsLocked(false);
        customer.setIsActive(true);
        customer.setRole(roleRepository.findByAuthority("CUSTOMER"));
        customer.setAddresses(List.of(createDemoAddress("home")));
        customerRepository.save(customer);
        logger.info("Seeded customer user: {}", CUSTOMER_EMAIL);
    }

    private Seller ensureSeller() {
        return sellerRepository.findByEmail(SELLER_EMAIL).orElseGet(() -> {
            Seller seller = new Seller();
            seller.setFirstName("Demo");
            seller.setLastName("Seller");
            seller.setEmail(SELLER_EMAIL);
            seller.setPassword(passwordEncoder.encode(demoPassword));
            seller.setGST("22AAAAA0000A1Z5");
            seller.setCompanyName("demo store");
            seller.setCompanyContact("9876543211");
            seller.setIsLocked(false);
            seller.setIsActive(true);
            seller.setRole(roleRepository.findByAuthority("SELLER"));
            seller.setAddresses(List.of(createDemoAddress("office")));
            Seller saved = sellerRepository.save(seller);
            logger.info("Seeded seller user: {}", SELLER_EMAIL);
            return saved;
        });
    }

    private Address createDemoAddress(String label) {
        Address address = new Address();
        address.setCity("Delhi");
        address.setState("Delhi");
        address.setCountry("India");
        address.setAddressLine("123 Demo Street");
        address.setZipCode("110001");
        address.setLabel(label);
        return address;
    }

    private Category ensureCategory(String name) {
        List<Category> rootCategories = categoryRepository.findAllByParentCategoryIdIsNull().orElse(List.of());
        Optional<Category> existing = rootCategories.stream()
                .filter(category -> name.equalsIgnoreCase(category.getName()))
                .findFirst();
        if (existing.isPresent()) {
            return existing.get();
        }

        Category category = new Category();
        category.setName(name.toLowerCase());
        category.setIsLeafCategory(true);
        Category saved = categoryRepository.save(category);
        logger.info("Seeded category: {}", name);
        return saved;
    }

    private CategoryMetaDataField ensureMetadataField(String name) {
        return categoryMetaDataFieldRepository.findByName(name.toLowerCase())
                .orElseGet(() -> {
                    CategoryMetaDataField field = new CategoryMetaDataField();
                    field.setName(name.toLowerCase());
                    return categoryMetaDataFieldRepository.save(field);
                });
    }

    private void linkMetadata(Category category, String fieldName, List<String> values) {
        CategoryMetaDataField field = ensureMetadataField(fieldName);
        CategoryMetaDataFieldValuesId id = new CategoryMetaDataFieldValuesId(category.getId(), field.getId());
        if (categoryMetaDataFieldValuesRepository.findById(id).isPresent()) {
            return;
        }

        CategoryMetaDataFieldValues fieldValues = new CategoryMetaDataFieldValues();
        fieldValues.setId(id);
        fieldValues.setCategory(category);
        fieldValues.setCategoryMetaDataField(field);
        fieldValues.setValue(String.join(",", values));
        categoryMetaDataFieldValuesRepository.save(fieldValues);
        logger.info("Linked metadata '{}' to category '{}'", fieldName, category.getName());
    }

    private void seedElectronicsProducts(Seller seller, Category category) throws IOException {
        Product headphones = ensureProduct(seller, category, "wireless bluetooth headphones", "sony",
                "Noise-cancelling over-ear headphones with 30-hour battery life");
        ensureVariation(headphones, Map.of("color", "black", "storage", "64gb"), 4999L, 25);
        ensureVariation(headphones, Map.of("color", "white", "storage", "128gb"), 5999L, 18);

        Product watch = ensureProduct(seller, category, "smart watch series x", "samsung",
                "Fitness tracking smartwatch with AMOLED display");
        ensureVariation(watch, Map.of("color", "silver", "storage", "64gb"), 12999L, 15);
        ensureVariation(watch, Map.of("color", "black", "storage", "128gb"), 14999L, 10);
    }

    private void seedClothingProducts(Seller seller, Category category) throws IOException {
        Product shoes = ensureProduct(seller, category, "running shoes", "nike",
                "Lightweight running shoes with cushioned sole");
        ensureVariation(shoes, Map.of("size", "m", "color", "red"), 3999L, 30);
        ensureVariation(shoes, Map.of("size", "l", "color", "blue"), 3999L, 22);

        Product tshirt = ensureProduct(seller, category, "cotton t-shirt", "adidas",
                "Breathable cotton t-shirt for everyday wear");
        ensureVariation(tshirt, Map.of("size", "s", "color", "black"), 1299L, 50);
        ensureVariation(tshirt, Map.of("size", "m", "color", "blue"), 1299L, 40);
    }

    private Product ensureProduct(Seller seller, Category category, String name, String brand, String description) {
        boolean exists = productRepository.findByBrandAndSellerIdAndCategoryId(
                brand.toLowerCase(), seller.getId(), category.getId()
        ).stream().anyMatch(product -> product.getName().equalsIgnoreCase(name));

        if (exists) {
            return productRepository.findByBrandAndSellerIdAndCategoryId(
                    brand.toLowerCase(), seller.getId(), category.getId()
            ).stream().filter(product -> product.getName().equalsIgnoreCase(name)).findFirst().orElseThrow();
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
        Product saved = productRepository.save(product);
        logger.info("Seeded product: {} ({})", name, saved.getId());
        return saved;
    }

    private void ensureVariation(Product product, Map<String, String> metadata, long price, int quantity) throws IOException {
        String metadataJson = JsonUtil.mapToJson(metadata);
        boolean exists = productVariationRepository.findAll().stream()
                .anyMatch(variation -> product.getId().equals(variation.getProduct().getId())
                        && metadataJson.equals(variation.getMetaData()));

        if (exists) {
            return;
        }

        String imageFileName = product.getId() + "_primary.png";
        writePlaceholderImage(product.getId(), imageFileName);

        ProductVariation variation = new ProductVariation();
        variation.setProduct(product);
        variation.setPrice(price);
        variation.setQuantityAvailable(quantity);
        variation.setMetaData(metadataJson);
        variation.setPrimaryImageName(imageFileName);
        variation.setIsActive(true);
        productVariationRepository.save(variation);
        logger.info("Seeded variation for product '{}' with metadata {}", product.getName(), metadata);
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
