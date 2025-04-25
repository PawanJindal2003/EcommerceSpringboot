package com.Project.ecommerce.utils.specifications;

import com.Project.ecommerce.entities.product.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecifications {

    public static Specification<Product> bySeller(String sellerId) {
        return (root, query, cb) -> cb.equal(root.get("seller").get("id"), sellerId);
    }

    public static Specification<Product> isNotDeleted() {
        return ((root, query, cb) -> cb.isFalse(root.get("isDeleted")));
    }

    public static Specification<Product> isActive() {
        return ((root, query, cb) -> cb.isTrue(root.get("isActive")));
    }

    public static Specification<Product> fromQueryString(String queryString, boolean isAdmin) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            String[] filters = queryString.split(",");

            for (String filter : filters) {
                //isDeleted will work only for admin
                if(filter.contains("isDeleted") && !isAdmin){
                    continue;
                }
                if (filter.contains("!=")) {
                    String[] parts = filter.split("!=");
                    addPredicate(predicates, root, cb, parts[0].trim(), parts[1].trim(), "!=");
                } else if (filter.contains("=")) {
                    String[] parts = filter.split("=");
                    addPredicate(predicates, root, cb, parts[0].trim(), parts[1].trim(), "=");
                } else if (filter.contains(":")) {
                    String[] parts = filter.split(":");
                    addPredicate(predicates, root, cb, parts[0].trim(), parts[1].trim(), ":");
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addPredicate(List<Predicate> predicates, Root<Product> root, CriteriaBuilder cb,
                                     String field, String value, String operator) {
        Path<?> path = root.get(field);
        Class<?> type = path.getJavaType();
        Object castedValue = castToFieldType(type, value);

        switch (operator) {
            case "=" -> predicates.add(cb.equal(path, castedValue));
            case "!=" -> predicates.add(cb.notEqual(path, castedValue));
            case ":" -> {
                if (type == String.class) {
                    predicates.add(cb.like(cb.lower((Path<String>) path), "%" + value.toLowerCase() + "%"));
                }
            }
        }
    }

    private static Object castToFieldType(Class<?> type, String value) {
        if (type == String.class) return value;
        if (type == Boolean.class || type == boolean.class) return Boolean.parseBoolean(value);
        return value; // Add more types if needed
    }


    public static Specification<Product> byCategories(List<String> categoryIds) {
        return (root, query, cb) -> root.get("category").get("id").in(categoryIds);
    }

    public static Specification<Product> excludeProductId(String productId) {
        return (root, query, cb) -> cb.notEqual(root.get("id"), productId);
    }

    public static Specification<Product> byBrand(String brandName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("brand")), "%" + brandName.toLowerCase() + "%");
    }
}
