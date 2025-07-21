package com.aeromatx.back.repository;

import com.aeromatx.back.entity.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, Long> {
}
