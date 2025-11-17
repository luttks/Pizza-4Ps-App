package com.example.pizzaapp.model;

public class User {
    private int id;
    private String email;
    private String password; // Sẽ được hash
    private String address;
    private String phone;
    // Constructor, getters, setters


    public User() {
    }

    public User(int id, String email, String password, String address, String phone) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
