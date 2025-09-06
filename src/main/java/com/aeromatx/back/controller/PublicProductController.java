package com.aeromatx.back.controller;

import com.aeromatx.back.dto.product.ProductResponseDTO;
import com.aeromatx.back.entity.Product;
import com.aeromatx.back.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.List;
//import java.util.UUID;

import com.aeromatx.back.dto.product.ProductResponseDTO;
import com.aeromatx.back.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:5500","https://ecommercebackend-4zll.onrender.com"})
public class PublicProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDTO>> getAll(){
        return ResponseEntity.ok(productService.getAllProductDTOs());
    }

    //get product by product id 
    @GetMapping("/by-category/{categoryId}")
public ResponseEntity<?> getProductsByCategory(@PathVariable Long categoryId) {
    List<Product> products = productService.getProductsByCategoryId(categoryId);
    List<ProductResponseDTO> result = products.stream().map(ProductResponseDTO::from).toList();
    return ResponseEntity.ok(result);
}

//get product by product id
@GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //search
    //  @GetMapping("/search")
    // public ResponseEntity<Page<ProductResponseDTO>> search(
    //         @RequestParam(required = false) String q,
    //         @RequestParam(required = false) Long categoryId,
    //         @RequestParam(required = false) Long subCategoryId,
    //         @RequestParam(required = false) Long applicationId,
    //         @RequestParam(required = false) BigDecimal minPrice,
    //         @RequestParam(required = false) BigDecimal maxPrice,
    //         @RequestParam(required = false) String status,
    //         @RequestParam(required = false) Boolean inStock,
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "12") int size,
    //         @RequestParam(defaultValue = "id") String sortBy,
    //         @RequestParam(defaultValue = "DESC") Sort.Direction direction
    // ) {
    //     Page<ProductResponseDTO> results = productService.searchProducts(
    //             q, categoryId, subCategoryId, applicationId, minPrice, maxPrice, status, inStock,
    //             page, size, sortBy, direction
    //     );
    //     return ResponseEntity.ok(results);
    // }

    //
        @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subCategoryId,
            @RequestParam(required = false) Long applicationId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        Page<ProductResponseDTO> results = productService.searchProducts(
                q, categoryId, subCategoryId, applicationId, minPrice, maxPrice, status, inStock,
                page, size, sortBy, direction
        );
        return ResponseEntity.ok(results);
    }

}
