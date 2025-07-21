package com.aeromatx.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Data
@NoArgsConstructor
public class CartItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private User user;

  @ManyToOne
  private Product product;

  private int quantity;
  private String notes;

  @ElementCollection
  @CollectionTable(name = "cart_item_specifications", joinColumns = @JoinColumn(name = "cart_item_id"))
  @MapKeyColumn(name = "spec_key")
  @Column(name = "spec_value")
  private Map<String, String> specifications;
}
