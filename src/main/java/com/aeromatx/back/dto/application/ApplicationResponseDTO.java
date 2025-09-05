package com.aeromatx.back.dto.application;

import com.aeromatx.back.dto.product.ProductResponseDTO;
import com.aeromatx.back.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponseDTO {
    private Long id;
    private String name;
    //private List<Product> products; // This will now be included in the response
    private List<ProductResponseDTO> products; // use DTO instead of entity

}
