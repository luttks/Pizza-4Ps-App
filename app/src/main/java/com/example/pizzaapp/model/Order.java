package com.example.pizzaapp.model;

public class Order {
    private int id; // ID đơn hàng
    private String date; // Ngày đặt
    private double totalPrice;
    private String status; // "Đã đặt (local)"
    // Constructor, getters, setters


    public Order(int id, String date, double totalPrice, String status) {
        this.id = id;
        this.date = date;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getId() {
        return id;
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

    public void setId(int id) {
        this.id = id;
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
}
