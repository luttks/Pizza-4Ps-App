package com.example.pizzaapp.model;

import java.util.List;

public class Order {
    private Long id;
    private Long userId;
    private String date;
    private double totalPrice;
    private String status;

    private List<OrderItem> items;

    public Order() {
    }

    public Order(Long id, Long userId, String date, double totalPrice, String status, List<OrderItem> items) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.totalPrice = totalPrice;
        this.status = status;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
