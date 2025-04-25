package com.Project.ecommerce.utils.specifications;

import com.Project.ecommerce.entities.product.ProductVariation;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductVariationSpecification {

    public static Specification<ProductVariation> byProductId(String productId) {
        return (root, query, cb) -> cb.equal(root.get("product").get("id"), productId);
    }

    public static Specification<ProductVariation> fromQueryString(String queryString) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            String[] filters = queryString.split(",");

            for (String filter : filters) {
                if (filter.contains("!=")) {
                    String[] parts = filter.split("!=");
                    addPredicate(predicates, root, cb, parts[0], parts[1], "!=");
                } else if (filter.contains(">")) {
                    String[] parts = filter.split(">");
                    addPredicate(predicates, root, cb, parts[0], parts[1], ">");
                } else if (filter.contains("<")) {
                    String[] parts = filter.split("<");
                    addPredicate(predicates, root, cb, parts[0], parts[1], "<");
                } else if (filter.contains("=")) {
                    String[] parts = filter.split("=");
                    addPredicate(predicates, root, cb, parts[0], parts[1], "=");
                } else if (filter.contains(":")) {
                    String[] parts = filter.split(":");
                    addPredicate(predicates, root, cb, parts[0], parts[1], ":");
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addPredicate(List<Predicate> predicates, Root<ProductVariation> root, CriteriaBuilder cb,
                                     String field, String value, String operator) {

        Path<?> path = root.get(field);
        Class<?> type = path.getJavaType();
        Object castedValue = castToFieldType(type, value);

        switch (operator) {
            case "=" -> predicates.add(cb.equal(path, castedValue));
            case "!=" -> predicates.add(cb.notEqual(path, castedValue));
            case ">" -> predicates.add(cb.greaterThan((Path<Comparable>) path, (Comparable) castedValue));
            case "<" -> predicates.add(cb.lessThan((Path<Comparable>) path, (Comparable) castedValue));
            case ":" -> {
                if (type == String.class) {
                    predicates.add(cb.like(cb.lower((Path<String>) path), "%" + value.toLowerCase() + "%"));
                }
            }
        }
    }

    private static Object castToFieldType(Class<?> type, String value) {
        if (type == String.class) {
            return value;
        } else if (type == Integer.class || type == int.class) {
            return Integer.parseInt(value);
        } else if (type == Long.class || type == long.class) {
            return Long.parseLong(value);
        } else if (type == Boolean.class || type == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        return value; // fallback
    }
}
