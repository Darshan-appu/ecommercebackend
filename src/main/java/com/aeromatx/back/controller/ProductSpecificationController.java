package com.aeromatx.back.controller;

import com.aeromatx.back.entity.ProductSpecification;
import com.aeromatx.back.service.ProductSpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/admin/specifications")
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:5500"})
public class ProductSpecificationController {

    @Autowired
    private ProductSpecificationService specService;

    // ✅ Add a new specification to a product
    @PostMapping("/{productId}")
    public ResponseEntity<ProductSpecification> addSpec(
            @PathVariable Long productId,
            @RequestBody ProductSpecification spec) {
        return ResponseEntity.ok(specService.addSpecification(productId, spec));
    }

    // ✅ Get all specifications for a product
    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductSpecification>> getSpecs(@PathVariable Long productId) {
        return ResponseEntity.ok(specService.getSpecifications(productId));
    }

    // ✅ Update an existing specification
    @PutMapping("/{specId}")
    public ResponseEntity<ProductSpecification> updateSpec(
            @PathVariable Long specId,
            @RequestBody ProductSpecification spec) {
        return ResponseEntity.ok(specService.updateSpecification(specId, spec));
    }

    // ✅ Delete a specification
    @DeleteMapping("/{specId}")
    public ResponseEntity<Void> deleteSpec(@PathVariable Long specId) {
        specService.deleteSpecification(specId);
        return ResponseEntity.noContent().build();
    }
}
