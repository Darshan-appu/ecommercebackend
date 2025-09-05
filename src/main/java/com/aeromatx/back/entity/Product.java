package com.aeromatx.back.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference; // <-- Add this import
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String status;
    private BigDecimal price;

    @Column(name = "stock")
    private int stock;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "datasheet_url")
    private String datasheetUrl;

    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    @JsonBackReference // <-- FIX: Add this annotation
    private SubCategory subCategory;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSpecification> specifications = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "product_application",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "application_id")
    )
    private List<Application> applications = new ArrayList<>();

    //to store vendor details
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
@JoinColumn(name = "vendor_id")
private Vendor vendor;


}