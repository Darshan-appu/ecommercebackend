package com.aeromatx.back.repository;

import com.aeromatx.back.entity.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {
    List<UserOrder> findByUserEmail(String email);
}