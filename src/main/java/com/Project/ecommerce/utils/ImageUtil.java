package com.Project.ecommerce.utils;

import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.UnsupportedImageTypeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ImageUtil {
    @Value("${user.upload-dir}")
    String uploadDir;

    @Value("${product.variations.upload-dir}")
    String variationUploadDir;
    public void saveUserImage(MultipartFile multipartFile, User user){
        try {
            String extension = Objects.requireNonNull(multipartFile.getOriginalFilename()).substring(multipartFile.getOriginalFilename().lastIndexOf('.'));
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = user.getId() + extension;
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store profile picture", e);
        }
    }

    public String saveProductVariationImage(MultipartFile image, String productId, String imageType) throws IOException {
        String originalFilename = image.getOriginalFilename();

        assert originalFilename != null;
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

        List<String> allowedExtensions = List.of(".jpg", ".jpeg", ".png", ".bmp");
        if (!allowedExtensions.contains(extension)) {
            throw new UnsupportedImageTypeException("Unsupported image format. Allowed formats: jpg, jpeg, png, bmp");
        }

        String filename;
        if ("primary".equals(imageType)) {
            filename = productId + "_primary" + extension;
        }
        else {
            filename = productId + "_" + imageType + extension;
        }

        Path imagePath = Paths.get(variationUploadDir, productId, filename);

        Files.createDirectories(imagePath.getParent());
        Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
        return filename;

    }

    public String getProductVariationPrimaryImage(String productId){
        Path dir = Paths.get("src/main/resources/images/products/variations", productId);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, productId + "_primary*.*")) {
            for (Path entry : stream) {
//                return entry.toAbsolutePath().toString();
                String filename = entry.getFileName().toString();
                return "http://localhost:8080/product/variations/primary-image/" + filename;
            }
        } catch (IOException e) {
            return "http://localhost:8080/profile-pics/default.jpg";
        }
        return null;
    }
    public List<String> getProductVariationSecondaryImages(String productId) {
        List<String> imageUrls = new ArrayList<>();
        Path dir = Paths.get("src/main/resources/images/products/variations", productId);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, productId + "_secondary_*.*")) {
            for (Path entry : stream) {
                String filename = entry.getFileName().toString();
                imageUrls.add("http://localhost:8080/product/variation/image/" + productId + "/" + filename);
            }
        } catch (IOException e) {
            imageUrls.add("http://localhost:8080/profile-pics/default.jpg");
        }
        return imageUrls;
    }


    public String getImage(String id){
        Path dir = Paths.get("src/main/resources/images/users");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, id + "*")) {
            for (Path entry : stream) {
//                return entry.toAbsolutePath().toString();
                return "http://localhost:8080/users/image/" + id;
            }
        } catch (IOException e) {
            return "http://localhost:8080/profile-pics/default.jpg";
        }
        return null;
    }
}

