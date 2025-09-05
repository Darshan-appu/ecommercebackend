package com.aeromatx.back.repository;

import com.aeromatx.back.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT DISTINCT p FROM Product p " +
       "LEFT JOIN FETCH p.subCategory sc " +
       "LEFT JOIN FETCH sc.category c " +
       "LEFT JOIN FETCH p.specifications")
    List<Product> findAllWithRelations();

    @Query("SELECT p FROM Product p JOIN FETCH p.subCategory sc JOIN FETCH sc.category c WHERE c.id = :categoryId")
    List<Product> findAllByCategoryId(@Param("categoryId") Long categoryId);

}
