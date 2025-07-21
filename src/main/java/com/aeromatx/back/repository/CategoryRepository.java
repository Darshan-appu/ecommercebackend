package com.aeromatx.back.repository;

import com.aeromatx.back.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByStatus(String status);

    Optional<Category> findByName(String name);

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subCategories")
    List<Category> findAllWithSubCategories();
}

