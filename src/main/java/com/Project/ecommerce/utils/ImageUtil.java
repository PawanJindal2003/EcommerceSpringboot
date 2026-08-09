package com.Project.ecommerce.utils;

import com.Project.ecommerce.entities.user.User;
import com.Project.ecommerce.exceptions.customExceptions.ResourceNotFoundException;
import com.Project.ecommerce.exceptions.customExceptions.StoreImageFailureException;
import com.Project.ecommerce.exceptions.customExceptions.UnsupportedImageTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ImageUtil {
    private final MessageSource messageSource;

    @Value("${user.upload-dir}")
    private String uploadDir;

    @Value("${product.variations.upload-dir}")
    private String variationUploadDir;

    public void saveUserImage(MultipartFile multipartFile, User user) {
        try {
            String extension = Objects.requireNonNull(multipartFile.getOriginalFilename())
                    .substring(multipartFile.getOriginalFilename().lastIndexOf('.'));
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = user.getId() + extension;
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StoreImageFailureException(messageSource.getMessage("image.store.profile.picture", null, LocaleContextHolder.getLocale()));
        }
    }

    public String saveProductVariationImage(MultipartFile image, String productId, String imageType) throws IOException {
        if (image == null || image.getOriginalFilename() == null) {
            throw new ResourceNotFoundException("Image file must not be null or empty.");
        }
        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

        List<String> allowedExtensions = List.of(".jpg", ".jpeg", ".png", ".bmp");
        if (!allowedExtensions.contains(extension)) {
            throw new UnsupportedImageTypeException(messageSource.getMessage("image.unsupported.format", null, LocaleContextHolder.getLocale()));
        }

        Path variationDir = Paths.get(variationUploadDir, productId);
        Files.createDirectories(variationDir);

        String filename;
        if ("primary".equals(imageType)) {
            filename = productId + "_primary" + extension;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(variationDir, productId + "_primary.*")) {
                for (Path oldFile : stream) {
                    Files.deleteIfExists(oldFile);
                }
            }
        } else {
            filename = productId + "_" + imageType + extension;
        }

        Path imagePath = variationDir.resolve(filename);
        Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    public String getProductVariationImageUrl(String productId, String filename) {
        if (productId == null || filename == null || filename.isBlank()) {
            return resolvePrimaryImageUrl(productId);
        }
        Path imagePath = Paths.get(variationUploadDir, productId, filename);
        if (!Files.exists(imagePath)) {
            return resolvePrimaryImageUrl(productId);
        }
        return "/api/images/product-variations/" + productId + "/" + filename;
    }

    public String getProductVariationPrimaryImage(String productId) {
        return resolvePrimaryImageUrl(productId);
    }

    public List<String> getProductVariationSecondaryImages(String productId) {
        List<String> imageUrls = new ArrayList<>();
        Path dir = Paths.get(variationUploadDir, productId);

        if (!Files.isDirectory(dir)) {
            return imageUrls;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, productId + "_secondary_*.*")) {
            for (Path entry : stream) {
                String filename = entry.getFileName().toString();
                imageUrls.add("/api/images/product-variations/" + productId + "/" + filename);
            }
        } catch (IOException e) {
            return imageUrls;
        }
        return imageUrls;
    }

    public String getImage(String userId) {
        Path dir = Paths.get(uploadDir);
        if (!Files.isDirectory(dir)) {
            return null;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, userId + "*")) {
            for (Path entry : stream) {
                return "/api/images/users/" + entry.getFileName().toString();
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    private String resolvePrimaryImageUrl(String productId) {
        if (productId == null) {
            return null;
        }
        Path dir = Paths.get(variationUploadDir, productId);
        if (!Files.isDirectory(dir)) {
            return null;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, productId + "_primary*.*")) {
            for (Path entry : stream) {
                String filename = entry.getFileName().toString();
                return "/api/images/product-variations/" + productId + "/" + filename;
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }
}
