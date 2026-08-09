package com.Project.ecommerce.co.product;

import com.Project.ecommerce.utils.JsonUtil;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Getter
@Setter
public class AddProductVariationCO {
    @NotNull(message = "Primary image is mandatory")
    private MultipartFile primaryImage;
    private MultipartFile[] secondaryImages;
    @NotBlank(message = "Product Id cannot be blank")
    private String productId;

    @Setter(AccessLevel.NONE)
    @NotEmpty(message = "Metadata cannot be empty")
    private Map<String , String> metadata;

    @NotNull(message = "Quantity cannot be blank")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantityAvailable;

    @NotNull(message = "Price cannot be blank")
    @Min(value = 0, message = "Price cannot be negative")
    private Long price;

    public void setMetadata(String metadataJson) {
        if (metadataJson == null || metadataJson.isBlank()) {
            this.metadata = Map.of();
            return;
        }
        this.metadata = JsonUtil.jsonToMap(metadataJson);
    }
}
