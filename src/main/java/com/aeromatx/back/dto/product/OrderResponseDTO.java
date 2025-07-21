package com.aeromatx.back.dto.product;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long id;
    private LocalDateTime orderDate;
    private double totalAmount;
    private String status;
    private String shippingAddress;
    private String billingAddress;
    private List<OrderItemResponseDTO> orderItems;
}