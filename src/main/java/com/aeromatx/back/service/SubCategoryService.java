package com.aeromatx.back.service;

import com.aeromatx.back.dto.product.SubCategoryDTO;
import com.aeromatx.back.entity.SubCategory;
import com.aeromatx.back.repository.SubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;

    @Autowired
    public SubCategoryService(SubCategoryRepository subCategoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
    }

    // 1. Get all subcategories
    // public List<SubCategory> getAllSubCategories() {
    //     return subCategoryRepository.findAll();
    // }
    public List<SubCategoryDTO> getAllSubCategoryDTOs() {
    return subCategoryRepository.findAllWithCategory().stream().map(subCategory -> {
        SubCategoryDTO dto = new SubCategoryDTO();
        dto.setId(subCategory.getId());
        dto.setName(subCategory.getName());
        dto.setSlug(subCategory.getSlug());
        dto.setDescription(subCategory.getDescription());
        dto.setStatus(subCategory.getStatus());
        dto.setCategoryId(subCategory.getCategory().getId());
        dto.setCategoryName(subCategory.getCategory().getName());
        return dto;
    }).toList();
}


    // 2. Get subcategories by categoryId
    public List<SubCategory> getSubCategoriesByCategoryId(Long categoryId) {
        return subCategoryRepository.findByCategoryId(categoryId);
    }

    // 3. Save subcategory
    public SubCategory saveSubCategory(SubCategory subCategory) {
    if (subCategory.getSlug() == null || subCategory.getSlug().isBlank()) {
        // Auto-generate slug from name
        String generatedSlug = subCategory.getName().toLowerCase().replaceAll("\\s+", "-");
        subCategory.setSlug(generatedSlug);
    }
    return subCategoryRepository.save(subCategory);
}


    // 4. Delete subcategory by ID
    public void deleteSubCategory(Long id) {
        subCategoryRepository.deleteById(id);
    }

    // 5. Update subcategory by ID
    public SubCategory updateSubCategory(Long id, SubCategory updatedSubCategory) {
        Optional<SubCategory> optionalSubCategory = subCategoryRepository.findById(id);
        if (optionalSubCategory.isPresent()) {
            SubCategory existing = optionalSubCategory.get();
            existing.setName(updatedSubCategory.getName());
            existing.setSlug(updatedSubCategory.getSlug());
            existing.setDescription(updatedSubCategory.getDescription());
            existing.setStatus(updatedSubCategory.getStatus());
            existing.setCategory(updatedSubCategory.getCategory());
            return subCategoryRepository.save(existing);
        } else {
            throw new RuntimeException("SubCategory not found with id: " + id);
        }
    }
    
}
