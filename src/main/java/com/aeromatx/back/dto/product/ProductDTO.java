package com.aeromatx.back.dto.product;

import java.math.BigDecimal;
import java.util.List;

import com.aeromatx.back.entity.Product;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String name;
    private String description;
    private String status;
    private BigDecimal price;
    private int stock;
    private Long subCategoryId;
    

    private List<SpecificationDTO> specifications;

    private String datasheetUrl;

public String getDatasheetUrl() { return datasheetUrl; }



    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public Long getSubCategoryId() { return subCategoryId; }
    public List<SpecificationDTO> getSpecifications() { return specifications; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setSubCategoryId(Long subCategoryId) { this.subCategoryId = subCategoryId; }
    public void setSpecifications(List<SpecificationDTO> specifications) { this.specifications = specifications; }
    public void setDatasheetUrl(String datasheetUrl) { this.datasheetUrl = datasheetUrl; }

    public static class SpecificationDTO {
        private String key;
        private String value;

        public String getKey() { return key; }
        public String getValue() { return value; }
        public void setKey(String key) { this.key = key; }
        public void setValue(String value) { this.value = value; }
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
