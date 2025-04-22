package com.Project.ecommerce.utils.specifications;

import com.Project.ecommerce.entities.product.ProductVariation;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class ProductVariationSpecification {
    public static Specification<ProductVariation> fromQueryString(String queryString) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            String[] filters = queryString.split(",");

            for (String filter : filters) {
                String[] parts = filter.split(":");
                if (parts.length == 2) {
                    String field = parts[0];
                    String value = parts[1];
                    predicates.add(cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%"));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ProductVariation> byProductId(String productId) {
        return (root, query, cb) ->
                cb.equal(root.get("product").get("id"), productId);
    }
}
