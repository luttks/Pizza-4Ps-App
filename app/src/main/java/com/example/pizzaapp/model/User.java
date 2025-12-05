package com.example.pizzaapp.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("address")
    private String address;

    @SerializedName("phone")
    private String phone;

    // --- MỚI: Thêm trường Name ---
    @SerializedName("name")
    private String name;

    // Constructor rỗng (Bắt buộc cho Gson/Retrofit)
    public User() {
    }

    // Constructor đầy đủ
    public User(int id, String email, String password, String address, String phone, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.name = name;
    }

    // --- Getters & Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}