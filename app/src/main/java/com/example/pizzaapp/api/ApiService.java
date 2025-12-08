package com.example.pizzaapp.api;

import com.example.pizzaapp.model.Category;
import com.example.pizzaapp.model.Food;
import com.example.pizzaapp.model.GoogleLoginRequest;
import com.example.pizzaapp.model.Order;
import com.example.pizzaapp.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Path;


public interface ApiService {

    // Định nghĩa hàm gọi API lấy danh sách món ăn
    // Đường dẫn tương ứng với Server: /api/foods
    @GET("api/foods")
    Call<List<Food>> getListFood();

    @POST("api/users/register")
    Call<User> registerUser(@Body User user);

    @POST("api/users/login")
    Call<User> loginUser(@Body User user);

    @POST("api/users/login-google")
    Call<User> loginWithGoogle(@Body GoogleLoginRequest request);

    // 1. Gui don hang len server
    @POST("api/orders")
    Call<Order> createOrder(@Body Order order);



    @GET("api/categories")
    Call<List<Category>> getCategories();

    @GET("api/foods/category/{categoryId}")
    Call<List<Food>> getFoodsByCategory(@Path("categoryId") int categoryId);

    @GET("api/orders/user/{userId}")
    Call<List<Order>> getOrdersByUserId(@Path("userId") int userId);

    @DELETE("api/orders/{id}")
    Call<Void> cancelOrder(@Path("id") long orderId);


}