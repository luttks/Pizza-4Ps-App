package com.example.pizza_backend.repository;

import com.example.pizza_backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Additional query methods can be defined here if needed
}
