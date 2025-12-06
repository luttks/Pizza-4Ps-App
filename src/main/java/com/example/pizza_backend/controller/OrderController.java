package com.example.pizza_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Import cái này để dùng @RestController, @PutMapping

import com.example.pizza_backend.model.Order;
import com.example.pizza_backend.model.OrderItem;
import com.example.pizza_backend.repository.OrderRepository;

import java.util.Date;
import java.util.List;

@RestController // <--- SỬA THÀNH CÁI NÀY
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    // 1. Tạo đơn hàng (App Mobile gọi)
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            order.setDate(new Date());
            order.setStatus("Processing");

            // Quan trọng: Gán cha cho con
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    item.setOrder(order);
                }
            }

            Order savedOrder = orderRepository.save(order);
            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 2. Lấy tất cả đơn hàng (Web Admin)
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 3. Lấy lịch sử theo User (App Mobile History)
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(@PathVariable Long userId) {
        // Đảm bảo bạn đã khai báo hàm này trong OrderRepository nhé
        return orderRepository.findByUserIdOrderByIdDesc(userId);
    }

    // 4. Cập nhật trạng thái (Web Admin duyệt đơn)
    @PutMapping("/{id}/status") //
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(status);
            return ResponseEntity.ok(orderRepository.save(order));
        }).orElse(ResponseEntity.notFound().build());
    }
}