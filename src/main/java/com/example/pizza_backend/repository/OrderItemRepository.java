package com.example.pizza_backend.repository;

import org.springframework.stereotype.Repository;
import com.example.pizza_backend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
