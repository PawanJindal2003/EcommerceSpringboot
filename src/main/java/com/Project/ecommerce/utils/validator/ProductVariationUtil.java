package com.Project.ecommerce.utils.validator;

import com.Project.ecommerce.entities.category.CategoryMetaDataFieldValues;
import com.Project.ecommerce.entities.product.Product;
import com.Project.ecommerce.entities.product.ProductVariation;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.exceptions.customExceptions.*;
import com.Project.ecommerce.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductVariationUtil {
    private final MessageSource messageSource;
    //ensuring product is active and non deleted
    public void validateAndFetchProduct(Product product){
        if (!product.getIsActive()) {
            throw new InactiveResourceException(messageSource.getMessage("productVariationUtil.product.inactive", null, LocaleContextHolder.getLocale()));
        }
        if (product.getIsDeleted()) {
            throw new DeletedProductException(messageSource.getMessage("productVariationUtil.product.deleted", null, LocaleContextHolder.getLocale()));
        }
    }

    // ensuring that variation should have at least one "metadata field - value"
    public void validateBlankMetadata(Map<String, String> metadata){
        metadata.forEach((key, value) -> {
            if (key == null || key.trim().isEmpty() || value == null || value.trim().isEmpty()) {
                throw new BlankMetadataException(messageSource.getMessage("productVariationUtil.metadata.blank", null, LocaleContextHolder.getLocale()));
            }
        });
    }

    // ensuring each variation of a product should be unique
    public void checkDuplicateVariation(Product product, Map<String, String> metadata){
        if (JsonUtil.isDuplicateVariation(product, metadata)) {
            throw new DuplicateResourceException(messageSource.getMessage("productVariationUtil.variation.duplicate", null, LocaleContextHolder.getLocale()));
        }
    }

    //ensuring entered metadata field and value are from allowed metadata field and values
    public void validateAllowedMetadata(Product product, Map<String, String> metadata){
        List<CategoryMetaDataFieldValues> categoryMetaDataFieldValues = product.getCategory().getMetadataFieldValues();
        Map<String, List<String>> allowedFieldValuesMap = categoryMetaDataFieldValues.stream()
                .collect(Collectors.toMap(
                        fieldValue -> fieldValue.getCategoryMetaDataField().getName(),
                        fieldValue -> List.of(fieldValue.getValue().split("\\s*,\\s*"))
                ));
        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            if (!allowedFieldValuesMap.containsKey(fieldName)) {
                throw new InactiveResourceException(messageSource.getMessage("productVariationUtil.metadata.invalid.field", new Object[]{fieldName}, LocaleContextHolder.getLocale()));
            }

            List<String> allowedValues = allowedFieldValuesMap.get(fieldName);
            if (!allowedValues.contains(fieldValue)) {
                throw new InactiveResourceException(messageSource.getMessage("productVariationUtil.metadata.invalid.value", new Object[]{fieldName, allowedValues}, LocaleContextHolder.getLocale()));
            }
        }
    }

    // ensuring metadata structure is similar to rest variation's metadata structure
    public void validateMetadataStructure(Product product, Map<String, String> metadata){
        if (!JsonUtil.matchesStructure(product, metadata)) {
            throw new InactiveResourceException(messageSource.getMessage("productVariationUtil.metadata.structure.mismatch", null, LocaleContextHolder.getLocale()));
        }
    }

    public void validateIsSellerProductVariation(Seller seller, ProductVariation productVariation){
        if(!productVariation.getProduct().getSeller().getId().equals(seller.getId())){
            throw new UnauthorizedAccessException(messageSource.getMessage("productVariationUtil.variation.unauthorized.access", null, LocaleContextHolder.getLocale()));
        }
    }
}