package com.aeromatx.back.service;

import com.aeromatx.back.dto.product.CategoryDTO;
import com.aeromatx.back.dto.product.SubCategoryDTO;
import com.aeromatx.back.entity.Category;
import com.aeromatx.back.entity.SubCategory;
import com.aeromatx.back.repository.CategoryRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAllWithSubCategories();
    }

    public List<Category> getActiveCategories() {
        return categoryRepository.findByStatus("Active");
    }

    @Transactional
    public Optional<Category> getCategoryById(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        categoryOpt.ifPresent(category -> category.getSubCategories().size()); // force load
        return categoryOpt;
    }

    public Category updateCategory(Long id, Category updatedCategory) {
        return categoryRepository.findById(id).map(category -> {
            category.setName(updatedCategory.getName());
            category.setSlug(updatedCategory.getSlug());
            category.setDescription(updatedCategory.getDescription());
            category.setStatus(updatedCategory.getStatus());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found with id " + id));
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return categoryRepository.findByName(name).isPresent();
    }

    @Transactional(readOnly = true)
    public CategoryDTO convertToDTO(Category category, boolean includeSubcategories) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setStatus(category.getStatus());

        if (includeSubcategories && category.getSubCategories() != null) {
            List<SubCategoryDTO> subDTOs = new ArrayList<>();
            for (SubCategory sub : new ArrayList<>(category.getSubCategories())) {
                subDTOs.add(new SubCategoryDTO(
                        sub.getId(),
                        sub.getName(),
                        sub.getSlug(),
                        sub.getDescription(),
                        sub.getStatus(),
                        sub.getCategory().getId(),
                        sub.getCategory().getName()
                ));
            }
            dto.setSubCategories(subDTOs);
        }

        return dto;
    }
}


