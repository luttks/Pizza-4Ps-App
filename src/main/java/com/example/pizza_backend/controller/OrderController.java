package com.example.pizza_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            // 1. Tìm đơn hàng trong DB
            Order order = orderRepository.findById(id).orElse(null);

            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Đơn hàng không tồn tại");
            }

            // 2. Kiểm tra trạng thái: Chỉ được hủy khi đang "Processing"
            if (!"Processing".equals(order.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Không thể hủy đơn hàng này (Đang giao hoặc đã hoàn thành)");
            }

            // 3. Thực hiện xóa
            // Vì trong Entity Order bạn đã để CascadeType.ALL, nên nó sẽ tự xóa luôn các
            // OrderItem đi kèm
            orderRepository.delete(order);

            return ResponseEntity.ok().body("Đã hủy đơn hàng thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server: " + e.getMessage());
        }
    }
}