package com.aeromatx.back.dto.product;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String status;
    private Long categoryId;
    private String categoryName;
}
