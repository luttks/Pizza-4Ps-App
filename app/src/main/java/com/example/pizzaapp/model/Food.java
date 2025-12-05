package com.example.pizzaapp.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Food implements Serializable {

    // @SerializedName giúp ánh xạ chính xác tên trường từ JSON vào biến Java
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("image")
    private String image; // Server sẽ trả về URL: "http://localhost:8080/uploads/..."

    @SerializedName("categoryId")
    private int categoryId;

    // Constructor rỗng (Bắt buộc để Gson hoạt động)
    public Food() {
    }

    // Constructor đầy đủ
    public Food(int id, String name, String description, double price, String image, int categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.categoryId = categoryId;
    }

    // --- Getter & Setter (Giữ nguyên hoặc Generate lại) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
}