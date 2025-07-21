package com.aeromatx.back.dto.product;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class OrderRequestDTO {
    private String billingAddress;
    private String shippingAddress;
    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String country;
    private String zipCode;
    private String phone;
    private String notes; // Order notes
    private String paymentMethod;

    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private int quantity;
        private String notes;
        private Map<String, String> specifications;
    }
}
