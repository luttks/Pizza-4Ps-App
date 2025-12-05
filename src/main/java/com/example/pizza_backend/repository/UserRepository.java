package com.example.pizza_backend.repository;

import com.example.pizza_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user bằng email (để check đăng nhập)
    User findByEmail(String email);
}