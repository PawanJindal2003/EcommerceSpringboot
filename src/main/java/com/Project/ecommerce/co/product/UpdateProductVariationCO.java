package com.Project.ecommerce.co.product;

import com.Project.ecommerce.utils.JsonUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UpdateProductVariationCO {
    private Integer quantityAvailable;
    private Long price;

    @Setter(AccessLevel.NONE)
    private Map<String, String> metadata;

    private Boolean isActive;

    public void setMetadata(String metadataJson) {
        if (metadataJson == null || metadataJson.isBlank()) {
            this.metadata = null;
            return;
        }
        this.metadata = JsonUtil.jsonToMap(metadataJson);
    }
}
