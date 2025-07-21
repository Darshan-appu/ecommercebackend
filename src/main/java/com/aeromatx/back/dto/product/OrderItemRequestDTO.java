package com.aeromatx.back.dto.product;

import lombok.Data;

import java.util.Map;

@Data
public class OrderItemRequestDTO {
    private Long productId;
    private int quantity;
    private Map<String, String> specifications;
}