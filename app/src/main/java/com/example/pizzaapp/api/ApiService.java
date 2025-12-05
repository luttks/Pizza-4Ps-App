package com.example.pizzaapp.api;

import com.example.pizzaapp.model.Food;
import com.example.pizzaapp.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;


public interface ApiService {

    // Định nghĩa hàm gọi API lấy danh sách món ăn
    // Đường dẫn tương ứng với Server: /api/foods
    @GET("api/foods")
    Call<List<Food>> getListFood();

    @POST("api/users/register")
    Call<User> registerUser(@Body User user);

    @POST("api/users/login")
    Call<User> loginUser(@Body User user);
}