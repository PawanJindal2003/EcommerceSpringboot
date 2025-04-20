package com.Project.ecommerce.utils.specifications;

import com.Project.ecommerce.entities.product.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecifications {

    public static Specification<Product> bySeller(String sellerId) {
        return (root, query, cb) -> cb.equal(root.get("seller").get("id"), sellerId);
    }

    public static Specification<Product> isNotDeleted(){
        return ((root, query, cb) -> cb.isFalse(root.get("isDeleted")));
    }

    public static Specification<Product> fromQueryString(String queryString) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            String[] filters = queryString.split(",");

            for (String filter : filters) {
                String[] parts = filter.split(":");
                if (parts.length == 2) {
                    String field = parts[0].trim();
                    String value = parts[1].trim();
                    predicates.add(cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%"));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
