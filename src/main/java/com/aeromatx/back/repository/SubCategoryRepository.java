package com.aeromatx.back.repository;

import com.aeromatx.back.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    List<SubCategory> findByCategoryId(Long categoryId);

    @Query("SELECT s FROM SubCategory s JOIN FETCH s.category")
List<SubCategory> findAllWithCategory();

}

