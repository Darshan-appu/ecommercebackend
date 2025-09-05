package com.aeromatx.back.dto.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ProductRequestSubmissionResponseDTO {
    private Long id;
    private String vendorEmail;
    private String productName;
    private String description;
    
    private BigDecimal price; // ✅ Change this from Double to BigDecimal
    private int stock;

    private String selectedCategory;
    private String selectedSubCategory;

    private String suggestedCategory;
    private String suggestedSubCategory;

    private String productImageUrl;
    private String datasheetUrl;
    private String categoryImageUrl;

    private String submittedAt;

    private Map<String, String> specifications;

    private String status; // <-- NEW FIELD
    private String rejectionReason; // <-- NEW FIELD
}
