package com.Project.ecommerce.controllers.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/images")
@Slf4j
public class ImageController {

    @Value("${product.variations.upload-dir}")
    private String variationUploadDir;

    @Value("${user.upload-dir}")
    private String userUploadDir;

    @GetMapping("/product-variations/{productId}/{filename}")
    public ResponseEntity<Resource> getProductVariationImage(
            @PathVariable String productId,
            @PathVariable String filename) throws IOException {
        Path imagePath = resolveSafePath(Paths.get(variationUploadDir, productId), filename);
        if (imagePath == null || !Files.exists(imagePath)) {
            return ResponseEntity.notFound().build();
        }
        return buildImageResponse(imagePath);
    }

    @GetMapping("/users/{filename}")
    public ResponseEntity<Resource> getUserImage(@PathVariable String filename) throws IOException {
        Path imagePath = resolveSafePath(Paths.get(userUploadDir), filename);
        if (imagePath == null || !Files.exists(imagePath)) {
            return ResponseEntity.notFound().build();
        }
        return buildImageResponse(imagePath);
    }

    private Path resolveSafePath(Path baseDir, String filename) {
        Path resolved = baseDir.resolve(filename).normalize();
        Path normalizedBase = baseDir.normalize().toAbsolutePath();
        if (!resolved.toAbsolutePath().startsWith(normalizedBase)) {
            log.warn("Blocked path traversal attempt for filename: {}", filename);
            return null;
        }
        return resolved;
    }

    private ResponseEntity<Resource> buildImageResponse(Path imagePath) throws IOException {
        Resource resource = new UrlResource(imagePath.toUri());
        String contentType = Files.probeContentType(imagePath);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .body(resource);
    }
}
