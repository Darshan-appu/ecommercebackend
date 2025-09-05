package com.aeromatx.back.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

import com.aeromatx.back.enums.RequestStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Data



@Entity
public class ProductRequestSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vendorEmail;

    private String productName;
    private String description;
    private BigDecimal price;
    private int stock;

    private String selectedCategory; // dropdown value
    private String selectedSubCategory;

    private String suggestedCategory;     // if "Other"
    private String suggestedSubCategory;

    private String productImageUrl;
    private String datasheetUrl;
    private String categoryImageUrl;

    @Column(length = 10000)
    private String specificationsJson; // Store as JSON string
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    private String rejectionReason;
}
