//package com.example.pizzaapp.helper;
//
//import com.example.pizzaapp.model.OrderItem;
//import java.util.ArrayList;
//import java.util.List;
//
//public class CartManager {
//    private static CartManager instance;
//    private List<OrderItem> cartItems;
//
//    private CartManager() {
//        cartItems = new ArrayList<>();
//    }
//
//    public static CartManager getInstance() {
//        if (instance == null) instance = new CartManager();
//        return instance;
//    }
//
//    public List<OrderItem> getCartItems() {
//        return cartItems;
//    }
//
//    public void addToCart(OrderItem item) {
//        boolean exists = false;
//        for (OrderItem existingItem : cartItems) {
//            // So sánh FoodId (dùng equals cho Long)
//            if (existingItem.getFoodId().equals(item.getFoodId())) {
//                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
//                exists = true;
//                break;
//            }
//        }
//        if (!exists) {
//            cartItems.add(item);
//        }
//    }
//
//    // --- CÁC HÀM MỚI THÊM ĐỂ HỖ TRỢ CART ACTIVITY ---
//
//    // Xóa món ăn khỏi list
//    public void removeItem(OrderItem item) {
//        cartItems.remove(item);
//    }
//
//    // Cập nhật số lượng trực tiếp
//    public void updateQuantity(OrderItem item, int quantity) {
//        if (quantity <= 0) {
//            removeItem(item);
//        } else {
//            item.setQuantity(quantity);
//        }
//    }
//
//    public double getTotalPrice() {
//        double total = 0;
//        for (OrderItem item : cartItems) {
//            total += item.getPrice() * item.getQuantity();
//        }
//        return total;
//    }
//
//    public void clearCart() {
//        cartItems.clear();
//    }
//}