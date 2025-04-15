package com.Project.ecommerce.utils;

import com.Project.ecommerce.entities.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

@Component
public class ImageUtil {
    @Value("${file.upload-dir}")
    String uploadDir;
    public void saveImage(MultipartFile multipartFile, User user){
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

