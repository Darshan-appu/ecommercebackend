package com.aeromatx.back.controller;

import com.aeromatx.back.dto.product.ProductDTO;
import com.aeromatx.back.dto.product.ProductResponseDTO;
import com.aeromatx.back.entity.Product;
import com.aeromatx.back.repository.ProductRepository;
import com.aeromatx.back.service.CloudinaryService;
import com.aeromatx.back.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products/admin")
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:5500","https://ecommercebackend-i16e.onrender.com"})
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        return ResponseEntity.ok(productService.getAllProductDTOs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> add(@RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.createProduct(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> edit(@PathVariable Long id, @RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> del(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Upload product image
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) throws IOException {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String imageUrl = cloudinaryService.uploadFile(file, "products");

        product.setImageUrl(imageUrl);
        productRepository.save(product);

        return ResponseEntity.ok("Product image uploaded successfully.");
    }

    // ✅ Upload datasheet (PDF)
    @PostMapping(value = "/{id}/datasheet", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDatasheet(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String pdfUrl = cloudinaryService.uploadFile(file, "datasheets");
        product.setDatasheetUrl(pdfUrl);
        productRepository.save(product);

        return ResponseEntity.ok("Product datasheet uploaded successfully.");
    }
}
