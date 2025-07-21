package com.aeromatx.back.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @JsonBackReference
    private SubCategory subCategory;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductSpecification> specifications = new ArrayList<>();
}
