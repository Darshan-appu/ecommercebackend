// src/main/java/com/aeromatx/back/dto/product/ProductResponseDTO.java
package com.aeromatx.back.dto.product;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

import com.aeromatx.back.entity.Product;

@Data @NoArgsConstructor @AllArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String status;
    private BigDecimal price;
    private int stock;
    private String imageUrl;
    private String datasheetUrl;


    private String categoryName;
    private String subCategoryName;

    private List<SpecificationDTO> specifications;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class SpecificationDTO {
        private String key;
        private String value;
    }

    public static ProductResponseDTO from(Product p) {
    ProductResponseDTO dto = new ProductResponseDTO();
    dto.setId(p.getId());
    dto.setName(p.getName());
    dto.setDescription(p.getDescription());
    dto.setStatus(p.getStatus());
    dto.setPrice(p.getPrice());
    dto.setStock(p.getStock());
    dto.setImageUrl(p.getImageUrl());
    dto.setDatasheetUrl(p.getDatasheetUrl());

    
    if (p.getSubCategory() != null) {
        dto.setSubCategoryName(p.getSubCategory().getName());
        if (p.getSubCategory().getCategory() != null) {
            dto.setCategoryName(p.getSubCategory().getCategory().getName());
        }
    }

    if (p.getSpecifications() != null) {
        dto.setSpecifications(
            p.getSpecifications().stream()
                .map(s -> new ProductResponseDTO.SpecificationDTO(s.getKey(), s.getValue()))
                .toList()
        );
    }

    return dto;
}

}
