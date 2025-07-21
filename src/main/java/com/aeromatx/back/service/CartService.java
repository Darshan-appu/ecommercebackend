package com.aeromatx.back.service;

import com.aeromatx.back.entity.CartItem;
import com.aeromatx.back.entity.Product;
import com.aeromatx.back.entity.User;
import com.aeromatx.back.payload.request.SpecificationRequest;
import com.aeromatx.back.repository.CartItemRepository;
import com.aeromatx.back.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    public void addToCart(User user, Product product, int quantity, String notes, List<SpecificationRequest> specifications) {
        Map<String, String> specMap = toSpecMap(specifications);

        // Find if a similar item (same product & same specifications) already exists
        CartItem existingItem = cartItemRepository.findByUser(user).stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()) &&
                        Objects.equals(item.getSpecifications(), specMap))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setNotes(notes);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setNotes(notes);
            newItem.setSpecifications(specMap);
            cartItemRepository.save(newItem);
        }
    }

    @Transactional
    public void updateCartItem(User user, Long cartItemId, int quantity, String notes, List<SpecificationRequest> specifications) {
        CartItem item = cartItemRepository.findByIdAndUser(cartItemId, user)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(quantity);
        item.setNotes(notes);
        item.setSpecifications(toSpecMap(specifications));
        cartItemRepository.save(item);
    }

    public void removeFromCart(User user, Long cartItemId) {
        CartItem item = cartItemRepository.findByIdAndUser(cartItemId, user)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItemRepository.delete(item);
    }

    public void clearCart(User user) {
        List<CartItem> items = cartItemRepository.findByUser(user);
        cartItemRepository.deleteAll(items);
    }

    private Map<String, String> toSpecMap(List<SpecificationRequest> specs) {
        if (specs == null || specs.isEmpty()) return Collections.emptyMap();
        Map<String, String> map = new HashMap<>();
        for (SpecificationRequest spec : specs) {
            map.put(spec.getKey(), spec.getValue());
        }
        return map;
    }
}
