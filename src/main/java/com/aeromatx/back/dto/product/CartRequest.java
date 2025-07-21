package com.aeromatx.back.dto.product;

import lombok.Data;

import java.util.Map;

@Data
public class CartRequest {
    private Long productId;
    private int quantity;
    private String notes;
    private Map<String, String> specifications;
}
