package com.aeromatx.back.controller;

import com.aeromatx.back.dto.product.SubCategoryDTO;
import com.aeromatx.back.entity.SubCategory;
import com.aeromatx.back.service.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aeromatx.back.service.SubCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/subcategories")
public class SubCategoryController {

    @Autowired
    private SubCategoryService subCategoryService;


    @GetMapping
public ResponseEntity<List<SubCategoryDTO>> getAllSubCategoriesWithCategory() {
    return ResponseEntity.ok(subCategoryService.getAllSubCategoryDTOs());
}

    @GetMapping("/by-category/{categoryId}")
    public List<SubCategory> getByCategoryId(@PathVariable Long categoryId) {
        return subCategoryService.getSubCategoriesByCategoryId(categoryId);
    }

    @PostMapping
    public SubCategory create(@RequestBody SubCategory subCategory) {
        return subCategoryService.saveSubCategory(subCategory);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
    }

    @PutMapping("/{id}")
public SubCategory update(@PathVariable Long id, @RequestBody SubCategory updatedSubCategory) {
    return subCategoryService.updateSubCategory(id, updatedSubCategory);
}

}
