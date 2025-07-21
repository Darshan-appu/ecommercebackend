package com.aeromatx.back.dto.product;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserOrderDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String status;
    private String billingAddress;
    private String shippingAddress;
    private String city;
    private String country;
    private String zipCode;
    private String phone;
    private String notes;
    private String paymentMethod;
    private double totalAmount;
    private LocalDateTime orderDate;

    private List<OrderItemDTO> orderItems;

    @Data
    public static class OrderItemDTO {
        private String productName;
        private int quantity;
        private String notes;
        private double price;
        private String specifications;
    }
}
