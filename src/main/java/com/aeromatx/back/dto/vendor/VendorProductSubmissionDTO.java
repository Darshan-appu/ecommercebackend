package com.aeromatx.back.dto.vendor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import com.aeromatx.back.dto.product.ProductSpecificationDTO;


@Data
public class VendorProductSubmissionDTO {
    private String productName;
    private String description;
    private BigDecimal price;
    private int stock;

    private String selectedCategory;       // From dropdown
    private String selectedSubCategory;

    private String suggestedCategory;      // Only if "Other"
    private String suggestedSubCategory;

    private List<ProductSpecificationDTO> specifications;
}
