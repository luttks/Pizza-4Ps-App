package com.example.pizza_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users") // Tên bảng trong PostgreSQL
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password; // Trong thực tế nên mã hóa, bài tập ta để plain text
    private String phone;
    private String address;
    private String name;
}