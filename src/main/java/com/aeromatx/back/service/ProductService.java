// src/main/java/com/aeromatx/back/service/ProductService.java
package com.aeromatx.back.service;

import com.aeromatx.back.dto.product.ProductDTO;
import com.aeromatx.back.dto.product.ProductResponseDTO;
import com.aeromatx.back.entity.Product;
import com.aeromatx.back.entity.ProductSpecification;
import com.aeromatx.back.repository.ProductRepository;
import com.aeromatx.back.repository.SubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;


@Service
public class ProductService {
    @Autowired private ProductRepository productRepository;
    @Autowired private SubCategoryRepository subCategoryRepository;

    public List<ProductResponseDTO> getAllProductDTOs() {
    return productRepository.findAllWithRelations().stream().map(p -> {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setStatus(p.getStatus());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock());
        dto.setImageUrl(p.getImageUrl());
        dto.setDatasheetUrl(p.getDatasheetUrl()); // ✅ THIS WAS MISSING

        if (p.getSubCategory() != null) {
            dto.setSubCategoryName(p.getSubCategory().getName());
            if (p.getSubCategory().getCategory() != null) {
                dto.setCategoryName(p.getSubCategory().getCategory().getName());
            }
        }

        if (p.getSpecifications() != null) {
            dto.setSpecifications(p.getSpecifications().stream()
                .map(s -> new ProductResponseDTO.SpecificationDTO(s.getKey(), s.getValue()))
                .collect(Collectors.toList()));
        }

        return dto;
    }).collect(Collectors.toList());
}


    public Product createProduct(ProductDTO dto) {
        var subCat = subCategoryRepository.findById(dto.getSubCategoryId())
            .orElseThrow(() -> new RuntimeException("SubCategory not found"));
        Product p = new Product();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setStatus(dto.getStatus());
        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        p.setSubCategory(subCat);
        if (dto.getSpecifications() != null) {
            p.setSpecifications(dto.getSpecifications().stream().map(s -> {
                var ps = new ProductSpecification();
                ps.setKey(s.getKey());
                ps.setValue(s.getValue());
                p.setDatasheetUrl(dto.getDatasheetUrl());

                ps.setProduct(p);
                return ps;
            }).collect(Collectors.toList()));
        }
        return productRepository.save(p);
    }

        
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Long id, ProductDTO dto) {
        return productRepository.findById(id).map(p -> {
            p.setDatasheetUrl(dto.getDatasheetUrl());

            p.setName(dto.getName());
            p.setDescription(dto.getDescription());
            p.setStatus(dto.getStatus());
            p.setPrice(dto.getPrice());
            p.setStock(dto.getStock());
            var subCat = subCategoryRepository.findById(dto.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException("SubCategory not found"));
            p.setSubCategory(subCat);
            p.getSpecifications().clear();
            if (dto.getSpecifications() != null) {
                p.getSpecifications().addAll(dto.getSpecifications().stream().map(s -> {
                    var ps = new ProductSpecification();
                    ps.setKey(s.getKey());
                    ps.setValue(s.getValue());
                    ps.setProduct(p);
                    return ps;
                }).collect(Collectors.toList()));
            }
            return productRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
    return productRepository.findAllByCategoryId(categoryId);
}
}
