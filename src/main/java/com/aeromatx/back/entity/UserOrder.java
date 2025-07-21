package com.aeromatx.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;


@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime orderDate;

    private double totalAmount;

    private String status;

    private String billingAddress;
    private String shippingAddress;

    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String country;
    private String zipCode;
    private String phone;
    private String notes;

    private String paymentMethod;

    @OneToMany(mappedBy = "userOrder", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> orderItems;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
}
