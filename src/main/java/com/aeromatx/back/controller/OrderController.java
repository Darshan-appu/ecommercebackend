package com.aeromatx.back.controller;

import com.aeromatx.back.dto.product.OrderRequestDTO;
import com.aeromatx.back.dto.product.UserOrderDTO;
import com.aeromatx.back.entity.UserOrder;
import com.aeromatx.back.service.OrderService;
import com.aeromatx.back.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    @PostMapping
public ResponseEntity<?> placeOrder(@RequestBody OrderRequestDTO request, HttpServletRequest http) {
    try {
        String jwt = http.getHeader("Authorization").substring(7);
        String email = jwtUtil.getUserNameFromJwtToken(jwt);
        UserOrder placedOrder = orderService.placeOrder(request, email);

        // Convert to DTO for the response
        UserOrderDTO dto = orderService.convertToDTO(placedOrder);
        return ResponseEntity.ok(dto);

    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Order failed: " + e.getMessage());
    }
}


   @GetMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<UserOrderDTO>> getAllOrders() {
    List<UserOrderDTO> orders = orderService.getAllOrdersAsDTOs();
    return ResponseEntity.ok(orders);
}


@PutMapping("/admin/{orderId}/status")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
    try {
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok("Order status updated to " + status);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Failed to update status: " + e.getMessage());
    }
}

@DeleteMapping("/admin/{orderId}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
    try {
        orderService.deleteOrderIfCompleted(orderId);
        return ResponseEntity.ok("Order deleted successfully");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Failed to delete order: " + e.getMessage());
    }
}



}