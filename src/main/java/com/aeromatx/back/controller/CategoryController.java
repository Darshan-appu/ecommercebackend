

package com.aeromatx.back.controller;

import com.aeromatx.back.dto.product.CategoryDTO;
import com.aeromatx.back.entity.Category;
import com.aeromatx.back.repository.CategoryRepository;
import com.aeromatx.back.service.CategoryService;
import com.aeromatx.back.service.CloudinaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:5500", "https://ecommercebackend-i16e.onrender.com"})
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // <-- removed 'consumes' so charset variants are accepted
    @PostMapping("/admin")
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        // optional: check duplicate name if you have a service method
        if (category.getName() != null && categoryService.existsByName(category.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Category name already exists"));
        }
        Category createdCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<CategoryDTO>> getAllCategoriesDTO() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> dtos = categories.stream()
                .map(cat -> categoryService.convertToDTO(cat, true))
                .toList();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getActiveCategoriesForFrontend() {
        List<Category> activeCategories = categoryService.getActiveCategories();
        return new ResponseEntity<>(activeCategories, HttpStatus.OK);
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(category -> new ResponseEntity<>(categoryService.convertToDTO(category, true), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/admin/{id}", consumes = {"application/json", "application/json;charset=UTF-8"})
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(id, category);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value="/admin/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCategoryImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) throws IOException {

        String imageUrl = cloudinaryService.uploadFile(file, "categories");

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setImageUrl(imageUrl);
        categoryRepository.save(category);

        return ResponseEntity.ok(Map.of("url", imageUrl));
    }

    @PutMapping(value = "/admin/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCategoryImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) throws IOException {

        String newImageUrl = cloudinaryService.uploadFile(file, "categories");

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // optionally delete old image: cloudinaryService.deleteFileFromUrl(category.getImageUrl());

        category.setImageUrl(newImageUrl);
        categoryRepository.save(category);

        return ResponseEntity.ok(Map.of(
                "message", "Category image updated successfully",
                "url", newImageUrl
        ));
    }
    //for public category access
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryForFrontend(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}