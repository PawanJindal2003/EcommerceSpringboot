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
import java.util.Locale;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ImageUtil {
    private final MessageSource messageSource;
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

        String filename;
        Path variationDir = Paths.get(variationUploadDir, productId);
        Files.createDirectories(variationDir);
        if ("primary".equals(imageType)) {
            filename = productId + "_primary" + extension;
            try(DirectoryStream<Path> stream = Files.newDirectoryStream(variationDir, productId + "_primary.*")){
                for(Path oldFile:stream){
                    Files.deleteIfExists(oldFile);
                }
            }
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
                return "http://localhost:8080/users/image/" + id;
        } catch (IOException e) {
            return "http://localhost:8080/profile-pics/default.jpg";
        }
    }
}

