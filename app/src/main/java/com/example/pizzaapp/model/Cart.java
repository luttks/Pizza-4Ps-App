package com.example.pizzaapp.model;

public class Cart {
    private int id; // ID của món ăn (foodId)
    private String foodName;
    private double price;
    private int quantity;
    private String image;
    private String customization; // Ví dụ: "Nửa này - Nửa kia", "Thêm phô mai"
    // Constructor, getters, setters


    public Cart(int id, String foodName, double price, int quantity, String image, String customization) {
        this.id = id;
        this.foodName = foodName;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.customization = customization;
    }

    public int getId() {
        return id;
    }

    public String getFoodName() {
        return foodName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImage() {
        return image;
    }

    public String getCustomization() {
        return customization;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCustomization(String customization) {
        this.customization = customization;
    }
}
