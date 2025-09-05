package com.aeromatx.back.controller;

import com.aeromatx.back.entity.CartItem;
import com.aeromatx.back.entity.Product;
import com.aeromatx.back.entity.User;
import com.aeromatx.back.payload.request.CartItemRequest;
import com.aeromatx.back.repository.ProductRepository;
import com.aeromatx.back.repository.UserRepository;
import com.aeromatx.back.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartt")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItems(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        List<CartItem> cartItems = cartService.getCartItems(user);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartItemRequest cartItemRequest, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        Product product = productRepository.findById(cartItemRequest.getProductId()).orElseThrow();
        cartService.addToCart(user, product, cartItemRequest.getQuantity(), cartItemRequest.getNotes(), cartItemRequest.getSpecifications());
        return ResponseEntity.ok("Item added to cart");
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemId, @Valid @RequestBody CartItemRequest cartItemRequest, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        cartService.updateCartItem(user, cartItemId, cartItemRequest.getQuantity(), cartItemRequest.getNotes(), cartItemRequest.getSpecifications());
        return ResponseEntity.ok("Cart item updated");
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartItemId, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        cartService.removeFromCart(user, cartItemId);
        return ResponseEntity.ok("Item removed from cart");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        cartService.clearCart(user);
        return ResponseEntity.ok("Cart cleared");
    }
}
