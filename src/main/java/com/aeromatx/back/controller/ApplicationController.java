package com.aeromatx.back.controller;

import com.aeromatx.back.dto.application.ApplicationDTO;
import com.aeromatx.back.dto.application.ApplicationResponseDTO;
import com.aeromatx.back.dto.product.ProductResponseDTO;
import com.aeromatx.back.entity.Application;
import com.aeromatx.back.entity.Product;
import com.aeromatx.back.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applicationss")
@Validated
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    // CREATE
    @PostMapping
    public ResponseEntity<Application> createApplication(@RequestBody ApplicationDTO dto) {
        Application app = new Application();
        app.setName(dto.getName());
        Application saved = applicationService.save(app);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET ALL with DTO mapping
    @GetMapping
    public ResponseEntity<List<ApplicationResponseDTO>> getAllApplications() {
        List<Application> applications = applicationService.findAll();

        List<ApplicationResponseDTO> response = applications.stream()
            .map(this::mapToApplicationResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // GET ONE by ID with DTO mapping
    @GetMapping("/{id}")
public ResponseEntity<ApplicationResponseDTO> getById(@PathVariable Long id) {
    Optional<Application> found = applicationService.findById(id);

    if (found.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    ApplicationResponseDTO dto = mapToApplicationResponse(found.get());
    return ResponseEntity.ok(dto);
}


    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Application> updateApplication(@PathVariable Long id,
                                                         @RequestBody ApplicationDTO dto) {
        Optional<Application> existing = applicationService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Application app = existing.get();
        app.setName(dto.getName());
        Application updated = applicationService.save(app);
        return ResponseEntity.ok(updated);
    }

    // PATCH (partial update)
    @PatchMapping("/{id}")
    public ResponseEntity<Application> patchApplication(@PathVariable Long id,
                                                        @RequestBody ApplicationDTO dto) {
        Optional<Application> existing = applicationService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Application app = existing.get();
        if (dto.getName() != null && !dto.getName().isBlank()) {
            app.setName(dto.getName());
        }
        Application updated = applicationService.save(app);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        if (!applicationService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        applicationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------
    // Helper method: map Application -> ApplicationResponseDTO
    private ApplicationResponseDTO mapToApplicationResponse(Application app) {
        ApplicationResponseDTO dto = new ApplicationResponseDTO();
        dto.setId(app.getId());
        dto.setName(app.getName());

        // Map products
        List<ProductResponseDTO> products = app.getProducts().stream()
            .map(this::mapToProductResponse) // map each Product entity to ProductResponseDTO
            .collect(Collectors.toList());

        dto.setProducts(products);

        return dto;
    }

    // Helper method: map Product -> ProductResponseDTO
    private ProductResponseDTO mapToProductResponse(Product p) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setStatus(p.getStatus());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock());
        dto.setImageUrl(p.getImageUrl());
        dto.setDatasheetUrl(p.getDatasheetUrl());
        dto.setCategoryName(p.getSubCategory() != null && p.getSubCategory().getCategory() != null
                            ? p.getSubCategory().getCategory().getName() : null);
        dto.setSubCategoryName(p.getSubCategory() != null ? p.getSubCategory().getName() : null);

        if (p.getSpecifications() != null) {
            dto.setSpecifications(
                p.getSpecifications().stream()
                 .map(s -> new ProductResponseDTO.SpecificationDTO(s.getKey(), s.getValue()))
                 .collect(Collectors.toList())
            );
        }

        // Vendor info
        if (p.getVendor() != null) {
            dto.setVendorId(p.getVendor().getVendorId());
            dto.setVendorName(p.getVendor().getBusinessName());
        }

        // Applications for this product
        if (p.getApplications() != null) {
            dto.setApplicationIds(p.getApplications().stream()
                                   .map(a -> a.getId())
                                   .collect(Collectors.toList()));
            dto.setApplicationNames(p.getApplications().stream()
                                     .map(a -> a.getName())
                                     .collect(Collectors.toList()));
        }

        return dto;
    }
}
