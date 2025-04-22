package com.Project.ecommerce.utils.validator;

import com.Project.ecommerce.entities.category.CategoryMetaDataFieldValues;
import com.Project.ecommerce.entities.product.Product;
import com.Project.ecommerce.entities.product.ProductVariation;
import com.Project.ecommerce.entities.user.Seller;
import com.Project.ecommerce.exceptions.customExceptions.*;
import com.Project.ecommerce.utils.JsonUtil;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static com.Project.ecommerce.utils.JsonUtil.isDuplicateVariation;

@Component
public class ProductVariationUtil {
    //ensuring product is active and non deleted
    public void validateAndFetchProduct(Product product){
        if (!product.getIsActive()) {
            throw new InactiveResourceException("Inactive product, please reach out to admin to activate the product");
        }
        if (product.getIsDeleted()) {
            throw new DeletedProductException("Chosen product is deleted, please create a product, if already created, reach out to admin");
        }
    }

    // ensuring that variation should have at least one "metadata field - value"
    public void validateBlankMetadata(Map<String, String> metadata){
        metadata.forEach((key, value) -> {
            if (key == null || key.trim().isEmpty() || value == null || value.trim().isEmpty()) {
                throw new BlankMetadataException("Metadata keys and values cannot be blank");
            }
        });
    }

    // ensuring each variation of a product should be unique
    public void checkDuplicateVariation(Product product, Map<String, String> metadata){
        if (isDuplicateVariation(product, metadata)) {
            throw new DuplicateResourceException("A variation with identical metadata already exists.");
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
                throw new InactiveResourceException("Invalid metadata field: " + fieldName);
            }

            List<String> allowedValues = allowedFieldValuesMap.get(fieldName);
            if (!allowedValues.contains(fieldValue)) {
                ;
                throw new InactiveResourceException(
                        "Invalid value '" + fieldValue + "' for field '" + fieldName +
                                "'. Allowed values: " + allowedValues
                );
            }
        }
    }

    // ensuring metadata structure is similar to rest variation's metadata structure
    public void validateMetadataStructure(Product product, Map<String, String> metadata){
        if (!JsonUtil.matchesStructure(product, metadata)) {
            throw new InactiveResourceException("Meta data values provided does not matched the structure of meta data fields");
        }
    }

    public void validateIsSellerProductVariation(Seller seller, ProductVariation productVariation){
        if(!productVariation.getProduct().getSeller().getId().equals(seller.getId())){
            throw new UnauthorizedAccessException("You do not have permission to view this product variation.");
        }
    }
}
