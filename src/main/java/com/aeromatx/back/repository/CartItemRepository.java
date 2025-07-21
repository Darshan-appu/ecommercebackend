package com.aeromatx.back.repository;

import com.aeromatx.back.entity.CartItem;
import com.aeromatx.back.entity.Product;
import com.aeromatx.back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByIdAndUser(Long id, User user);
    CartItem findByUserAndProduct(User user, Product product);
}
