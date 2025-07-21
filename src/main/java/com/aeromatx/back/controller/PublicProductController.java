package com.aeromatx.back.controller;

import com.aeromatx.back.dto.product.ProductResponseDTO;
import com.aeromatx.back.entity.Product;
import com.aeromatx.back.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:5500","https://ecommercebackend-i16e.onrender.com"})
public class PublicProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDTO>> getAll(){
        return ResponseEntity.ok(productService.getAllProductDTOs());
    }

    @GetMapping("/by-category/{categoryId}")
public ResponseEntity<?> getProductsByCategory(@PathVariable Long categoryId) {
    List<Product> products = productService.getProductsByCategoryId(categoryId);
    List<ProductResponseDTO> result = products.stream().map(ProductResponseDTO::from).toList();
    return ResponseEntity.ok(result);
}

}
