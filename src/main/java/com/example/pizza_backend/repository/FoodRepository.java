package com.example.pizza_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.pizza_backend.model.Food;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByCategoryId(int categoryId);
}
