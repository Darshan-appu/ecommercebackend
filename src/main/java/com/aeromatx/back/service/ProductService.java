// src/main/java/com/aeromatx/back/service/ProductService.java
package com.aeromatx.back.service;

import com.aeromatx.back.dto.product.ProductDTO;
import com.aeromatx.back.dto.product.ProductResponseDTO;
import com.aeromatx.back.entity.Application;
import com.aeromatx.back.entity.Product;
import com.aeromatx.back.entity.ProductSpecification;
import com.aeromatx.back.entity.Vendor;
import com.aeromatx.back.repository.ApplicationRepository;
import com.aeromatx.back.repository.ProductRepository;
import com.aeromatx.back.repository.SubCategoryRepository;
import com.aeromatx.back.repository.VendorRepository;
import com.aeromatx.back.repository.spec.ProductSpecifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.aeromatx.back.dto.product.ProductResponseDTO;
import com.aeromatx.back.entity.Product;
import com.aeromatx.back.repository.ProductRepository;
import com.aeromatx.back.repository.spec.ProductSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private SubCategoryRepository subCategoryRepository;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private VendorRepository vendorRepository;

    /** ---------- Public mappers (reuse from other services) ---------- */
    public ProductResponseDTO toProductResponseDTO(Product p) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setStatus(p.getStatus());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock());
        dto.setImageUrl(p.getImageUrl());
        dto.setDatasheetUrl(p.getDatasheetUrl());

        // Category & Subcategory
        if (p.getSubCategory() != null) {
            dto.setSubCategoryName(p.getSubCategory().getName());
            if (p.getSubCategory().getCategory() != null) {
                dto.setCategoryName(p.getSubCategory().getCategory().getName());
            }
        }

        // Specifications
        if (p.getSpecifications() != null) {
            dto.setSpecifications(
                p.getSpecifications().stream()
                    .map(s -> new ProductResponseDTO.SpecificationDTO(s.getKey(), s.getValue()))
                    .collect(Collectors.toList())
            );
        } else {
            dto.setSpecifications(new ArrayList<>());
        }

        // Applications
        if (p.getApplications() != null) {
            dto.setApplicationNames(
                p.getApplications().stream()
                    .map(Application::getName)
                    .collect(Collectors.toList())
            );
        } else {
            dto.setApplicationNames(new ArrayList<>());
        }

        // Vendor info (only the simple fields you need)
        if (p.getVendor() != null) {
            dto.setVendorId(p.getVendor().getVendorId());
            dto.setVendorName(p.getVendor().getBusinessName());
        }

        return dto;
    }

    /** ---------- Reads ---------- */
    public List<ProductResponseDTO> getAllProductDTOs() {
        return productRepository.findAllWithRelations()
                .stream()
                .map(this::toProductResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findAllByCategoryId(categoryId);
    }

    /** ---------- Create ---------- */
    public Product createProduct(ProductDTO dto) {
        var subCat = subCategoryRepository.findById(dto.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException("SubCategory not found"));

        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Product p = new Product();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setStatus(dto.getStatus());
        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        p.setDatasheetUrl(dto.getDatasheetUrl());
       // p.setImageUrl(dto.getImageUrl()); // if DTO carries one
        p.setSubCategory(subCat);
        p.setVendor(vendor);

        // Specifications
        if (dto.getSpecifications() != null && !dto.getSpecifications().isEmpty()) {
            p.setSpecifications(dto.getSpecifications().stream().map(s -> {
                var ps = new ProductSpecification();
                ps.setKey(s.getKey());
                ps.setValue(s.getValue());
                ps.setProduct(p);
                return ps;
            }).collect(Collectors.toList()));
        } else {
            p.setSpecifications(new ArrayList<>());
        }

        // Applications
        if (dto.getApplicationIds() != null && !dto.getApplicationIds().isEmpty()) {
            List<Application> apps = applicationRepository.findAllById(dto.getApplicationIds());
            p.setApplications(apps);
        } else {
            p.setApplications(new ArrayList<>());
        }

        return productRepository.save(p);
    }

    /** ---------- Update ---------- */
    public Product updateProduct(Long id, ProductDTO dto) {
        return productRepository.findById(id).map(p -> {
            p.setName(dto.getName());
            p.setDescription(dto.getDescription());
            p.setStatus(dto.getStatus());
            p.setPrice(dto.getPrice());
            p.setStock(dto.getStock());
            p.setDatasheetUrl(dto.getDatasheetUrl());
           // if (dto.getImageUrl() != null) p.setImageUrl(dto.getImageUrl());

            var subCat = subCategoryRepository.findById(dto.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("SubCategory not found"));
            p.setSubCategory(subCat);

            Vendor vendor = vendorRepository.findById(dto.getVendorId())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));
            p.setVendor(vendor);

            // Replace specifications
            p.getSpecifications().clear();
            if (dto.getSpecifications() != null && !dto.getSpecifications().isEmpty()) {
                p.getSpecifications().addAll(
                    dto.getSpecifications().stream().map(s -> {
                        var ps = new ProductSpecification();
                        ps.setKey(s.getKey());
                        ps.setValue(s.getValue());
                        ps.setProduct(p);
                        return ps;
                    }).collect(Collectors.toList())
                );
            }

            // Update applications
            if (dto.getApplicationIds() != null) {
                if (dto.getApplicationIds().isEmpty()) {
                    p.setApplications(new ArrayList<>());
                } else {
                    List<Application> apps = applicationRepository.findAllById(dto.getApplicationIds());
                    p.setApplications(apps);
                }
            }

            return productRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    /** ---------- Delete ---------- */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    //search
    public Page<ProductResponseDTO> searchProducts(
            String q,
            Long categoryId,
            Long subCategoryId,
            Long applicationId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String status,
            Boolean inStock,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        if (page < 0) page = 0;
        if (size <= 0) size = 12;
        String sortField = (sortBy == null || sortBy.isBlank()) ? "id" : sortBy;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction == null ? Sort.Direction.DESC : direction, sortField));

        Specification<Product> spec = Specification.where(ProductSpecifications.distinct())
                .and(ProductSpecifications.text(q))
                .and(ProductSpecifications.categoryId(categoryId))
                .and(ProductSpecifications.subCategoryId(subCategoryId))
                .and(ProductSpecifications.applicationId(applicationId))
                .and(ProductSpecifications.minPrice(minPrice))
                .and(ProductSpecifications.maxPrice(maxPrice))
                .and(ProductSpecifications.status(status))
                .and(ProductSpecifications.inStock(inStock));

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        return productPage.map(this::toProductResponseDTO);
    }
    
}
