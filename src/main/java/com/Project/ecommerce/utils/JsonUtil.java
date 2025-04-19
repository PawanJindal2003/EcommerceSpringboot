package com.Project.ecommerce.utils;

import com.Project.ecommerce.entities.product.Product;
import com.Project.ecommerce.entities.product.ProductVariation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String mapToJson(Map<String, String> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert the map to string", e);
        }
    }

    public static Map<String, String> jsonToMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON to map", e);
        }
    }

    public static boolean isDuplicateVariation(Product product, Map<String, String> newMetaData) {
        List<ProductVariation> existingVariations = product.getProductVariations();

        for (ProductVariation variation : existingVariations) {
            if (variation.getMetaData() == null || variation.getMetaData().isBlank()) continue;

            Map<String, String> existingMetaData;
            try {
                existingMetaData = objectMapper.readValue(variation.getMetaData(), new TypeReference<>() {});
            } catch (Exception e) {
                continue;
            }

            if (existingMetaData.equals(newMetaData)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchesStructure(Product product, Map<String, String> newMetaData) {
        List<ProductVariation> existingVariations = product.getProductVariations();
        Set<String> currentFields = newMetaData.keySet();
        if (existingVariations == null || existingVariations.isEmpty()) {
            return true;
        }
        for (ProductVariation productVariation : existingVariations) {
            if (productVariation.getMetaData() == null || productVariation.getMetaData().isBlank()) continue;

            Map<String, String> existingMetaData;
            try {
                existingMetaData = objectMapper.readValue(productVariation.getMetaData(), new TypeReference<>() {
                });
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse existing variation metadata for comparison.");
            }
            Set<String> existingFields = existingMetaData.keySet();

            if (existingFields.equals(currentFields)) {
                return true;
            }
        }
        return false;
    }

}
