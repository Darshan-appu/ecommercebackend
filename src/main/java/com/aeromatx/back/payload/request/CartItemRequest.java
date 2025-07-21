package com.aeromatx.back.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CartItemRequest {

    @NotNull
    private Long productId;

    @Min(1)
    private int quantity;

    private String notes;

    private List<SpecificationRequest> specifications;

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<SpecificationRequest> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<SpecificationRequest> specifications) {
        this.specifications = specifications;
    }
}
