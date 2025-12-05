package com.example.pizzaapp.api;

import com.example.pizzaapp.model.Food;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    // Định nghĩa hàm gọi API lấy danh sách món ăn
    // Đường dẫn tương ứng với Server: /api/foods
    @GET("api/foods")
    Call<List<Food>> getListFood();
}