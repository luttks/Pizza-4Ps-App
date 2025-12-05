package com.example.pizza_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.pizza_backend.model.Food;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

}
