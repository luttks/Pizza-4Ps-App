package com.example.pizza_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.pizza_backend.repository.FoodRepository;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.pizza_backend.model.Food;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/foods")
public class FoodController {
    @Autowired
    private FoodRepository foodRepository;

    // 1. lay danh sach mon an
    @GetMapping
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    // 2. them mon an
    @PostMapping
    public Food createFood(@RequestBody Food food) {
        return foodRepository.save(food);
    }

}
