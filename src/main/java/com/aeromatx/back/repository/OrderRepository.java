package com.aeromatx.back.repository;

import com.aeromatx.back.entity.UserOrder;
import com.aeromatx.back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<UserOrder, Long> {
    List<UserOrder> findByUser(User user);
}
