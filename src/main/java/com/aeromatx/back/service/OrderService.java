package com.aeromatx.back.service;

import com.aeromatx.back.dto.product.OrderRequestDTO;
import com.aeromatx.back.dto.product.UserOrderDTO;
import com.aeromatx.back.entity.UserOrder;
import com.aeromatx.back.entity.OrderItem;
import com.aeromatx.back.entity.Product;
import com.aeromatx.back.entity.User;
import com.aeromatx.back.repository.OrderRepository;
import com.aeromatx.back.repository.ProductRepository;
import com.aeromatx.back.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserOrder placeOrder(OrderRequestDTO orderRequestDTO, String userEmail) {
    User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

    UserOrder order = new UserOrder();
    order.setUser(user);
    order.setOrderDate(LocalDateTime.now(ZoneId.systemDefault()));
    order.setStatus("PLACED");
    order.setShippingAddress(orderRequestDTO.getShippingAddress());
    order.setBillingAddress(orderRequestDTO.getBillingAddress());

    // ✅ Set new fields from the request
    order.setFirstName(orderRequestDTO.getFirstName());
    order.setLastName(orderRequestDTO.getLastName());
    order.setEmail(orderRequestDTO.getEmail());
    order.setCity(orderRequestDTO.getCity());
    order.setCountry(orderRequestDTO.getCountry());
    order.setZipCode(orderRequestDTO.getZipCode());
    order.setPhone(orderRequestDTO.getPhone());
    order.setNotes(orderRequestDTO.getNotes());
    order.setPaymentMethod(orderRequestDTO.getPaymentMethod());

    List<OrderItem> orderItems = new ArrayList<>();
    BigDecimal totalAmount = BigDecimal.ZERO;

    for (OrderRequestDTO.OrderItemDTO itemDTO : orderRequestDTO.getItems()) {
        Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int quantity = itemDTO.getQuantity();

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setNotes(itemDTO.getNotes());

        try {
            String specificationsJson = objectMapper.writeValueAsString(itemDTO.getSpecifications());
            orderItem.setSpecificationsJson(specificationsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize specifications", e);
        }

        orderItem.setUserOrder(order);
        orderItems.add(orderItem);

        BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        totalAmount = totalAmount.add(itemTotal);
    }

    order.setOrderItems(orderItems);
    order.setTotalAmount(totalAmount.doubleValue());

    return orderRepository.save(order);
}

public List<UserOrder> getAllOrders() {
    return orderRepository.findAll();
}


public List<UserOrderDTO> getAllOrdersAsDTOs() {
    List<UserOrder> orders = orderRepository.findAll();

    return orders.stream().map(order -> {
        UserOrderDTO dto = new UserOrderDTO();
        dto.setId(order.getId());
        dto.setEmail(order.getEmail());
        dto.setFirstName(order.getFirstName());
        dto.setLastName(order.getLastName());
        dto.setStatus(order.getStatus());
        dto.setBillingAddress(order.getBillingAddress());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCity(order.getCity());
        dto.setCountry(order.getCountry());
        dto.setZipCode(order.getZipCode());
        dto.setPhone(order.getPhone());
        dto.setNotes(order.getNotes());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());

        List<UserOrderDTO.OrderItemDTO> items = order.getOrderItems().stream().map(item -> {
            UserOrderDTO.OrderItemDTO itemDTO = new UserOrderDTO.OrderItemDTO();
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setNotes(item.getNotes());
            itemDTO.setPrice(item.getProduct().getPrice().doubleValue());
            itemDTO.setSpecifications(item.getSpecificationsJson());
            return itemDTO;
        }).toList();

        dto.setOrderItems(items);
        return dto;
    }).toList();
}


public UserOrderDTO convertToDTO(UserOrder order) {
    UserOrderDTO dto = new UserOrderDTO();
    dto.setId(order.getId());
    dto.setEmail(order.getEmail());
    dto.setFirstName(order.getFirstName());
    dto.setLastName(order.getLastName());
    dto.setStatus(order.getStatus());
    dto.setBillingAddress(order.getBillingAddress());
    dto.setShippingAddress(order.getShippingAddress());
    dto.setCity(order.getCity());
    dto.setCountry(order.getCountry());
    dto.setZipCode(order.getZipCode());
    dto.setPhone(order.getPhone());
    dto.setNotes(order.getNotes());
    dto.setPaymentMethod(order.getPaymentMethod());
    dto.setTotalAmount(order.getTotalAmount());
    dto.setOrderDate(order.getOrderDate());

    List<UserOrderDTO.OrderItemDTO> items = order.getOrderItems().stream().map(item -> {
        UserOrderDTO.OrderItemDTO itemDTO = new UserOrderDTO.OrderItemDTO();
        itemDTO.setProductName(item.getProduct().getName());
        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setNotes(item.getNotes());
        itemDTO.setPrice(item.getProduct().getPrice().doubleValue());
        itemDTO.setSpecifications(item.getSpecificationsJson());
        return itemDTO;
    }).toList();

    dto.setOrderItems(items);
    return dto;
}

public void updateOrderStatus(Long orderId, String newStatus) {
    UserOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

    order.setStatus(newStatus);
    orderRepository.save(order);
}

public void deleteOrderIfCompleted(Long orderId) {
    UserOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

    // if (!"COMPLETED".equalsIgnoreCase(order.getStatus())) {
    //     throw new RuntimeException("Only COMPLETED orders can be deleted.");
    // }

    if (!("COMPLETED".equalsIgnoreCase(order.getStatus()) || "CANCELLED".equalsIgnoreCase(order.getStatus()))) {
    throw new RuntimeException("Only COMPLETED or CANCELLED orders can be deleted.");
}


    orderRepository.delete(order);
}

public void deleteOrderIfCompletedOrCancelled(Long orderId) {
    UserOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

    if (!("COMPLETED".equalsIgnoreCase(order.getStatus()) || "CANCELLED".equalsIgnoreCase(order.getStatus()))) {
        throw new RuntimeException("Only COMPLETED or CANCELLED orders can be deleted.");
    }

    orderRepository.delete(order);
}





}
