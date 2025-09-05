package com.aeromatx.back.repository.spec;

import com.aeromatx.back.entity.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecifications {

    private ProductSpecifications() {}

    public static Specification<Product> distinct() {
        return (root, query, cb) -> {
            query.distinct(true);
            return null;
        };
    }

    public static Specification<Product> text(String q) {
        if (q == null || q.isBlank()) return null;
        String like = "%" + q.trim().toLowerCase() + "%";
        return (root, query, cb) -> {
            // joins to allow searching across related names/specs/apps
            Join<Object, Object> subCat = root.join("subCategory", JoinType.LEFT);
            Join<Object, Object> cat = subCat.join("category", JoinType.LEFT);
            Join<Object, Object> specs = root.join("specifications", JoinType.LEFT);
            Join<Object, Object> apps = root.join("applications", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like),
                    cb.like(cb.lower(subCat.get("name")), like),
                    cb.like(cb.lower(cat.get("name")), like),
                    cb.like(cb.lower(apps.get("name")), like),
                    cb.like(cb.lower(specs.get("key")), like),
                    cb.like(cb.lower(specs.get("value")), like)
            );
        };
    }

    public static Specification<Product> categoryId(Long categoryId) {
        if (categoryId == null) return null;
        return (root, query, cb) -> {
            Join<Object, Object> subCat = root.join("subCategory", JoinType.LEFT);
            Join<Object, Object> cat = subCat.join("category", JoinType.LEFT);
            return cb.equal(cat.get("id"), categoryId);
        };
    }

    public static Specification<Product> subCategoryId(Long subCategoryId) {
        if (subCategoryId == null) return null;
        return (root, query, cb) -> cb.equal(root.join("subCategory", JoinType.LEFT).get("id"), subCategoryId);
    }

    public static Specification<Product> applicationId(Long applicationId) {
        if (applicationId == null) return null;
        return (root, query, cb) -> cb.equal(root.join("applications", JoinType.LEFT).get("id"), applicationId);
    }

    public static Specification<Product> minPrice(BigDecimal min) {
        if (min == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    public static Specification<Product> maxPrice(BigDecimal max) {
        if (max == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), max);
    }

    public static Specification<Product> status(String status) {
        if (status == null || status.isBlank()) return null;
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Product> inStock(Boolean inStock) {
        if (inStock == null) return null;
        return inStock
                ? (root, query, cb) -> cb.greaterThan(root.get("stock"), 0)
                : (root, query, cb) -> cb.lessThanOrEqualTo(root.get("stock"), 0);
    }
}
