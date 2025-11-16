package com.example.pizzaapp.model;

public class Food {
    private int id;
    private String name;
    private String description;
    private double price;
    private String image; // Tên resource ảnh
    private int categoryId; // Foreign key
    // Constructor, getters, setters


    public Food(int categoryId, String image, double price, String description, String name, int id) {
        this.categoryId = categoryId;
        this.image = image;
        this.price = price;
        this.description = description;
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
