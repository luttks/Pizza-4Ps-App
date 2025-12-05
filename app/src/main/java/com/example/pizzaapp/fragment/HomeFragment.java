package com.example.pizzaapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pizzaapp.R;
import com.example.pizzaapp.activity.FoodDetailActivity;
import com.example.pizzaapp.adapter.CategoryAdapter;
import com.example.pizzaapp.adapter.FoodAdapter;
import com.example.pizzaapp.database.CategoryDAO;
// import com.example.pizzaapp.database.FoodDAO; // [CŨ] Không dùng DAO nữa
import com.example.pizzaapp.model.Category;
import com.example.pizzaapp.model.Food;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.pizzaapp.activity.CartActivity;

// --- [MỚI] Import thư viện API ---
import com.example.pizzaapp.api.ApiService;
import com.example.pizzaapp.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener, FoodAdapter.OnFoodClickListener {

    private RecyclerView rvCategories, rvFoods;
    private CategoryAdapter categoryAdapter;
    private FoodAdapter foodAdapter;

    private CategoryDAO categoryDAO;
    // private FoodDAO foodDAO; // [CŨ] Tạm thời tắt FoodDAO

    private List<Category> categoryList;
    private List<Food> foodList;
    private FloatingActionButton fabCart;

    private SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ view
        rvCategories = view.findViewById(R.id.rv_categories);
        rvFoods = view.findViewById(R.id.rv_foods);

        // Khởi tạo DAO
        categoryDAO = new CategoryDAO(getContext());
        // foodDAO = new FoodDAO(getContext()); // [CŨ] Tắt khởi tạo DAO

        // Khởi tạo danh sách
        foodList = new ArrayList<>();

        // Ánh xạ nút FAB
        fabCart = view.findViewById(R.id.fab_cart);

        // 1. Ánh xạ SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        // 2. Cài đặt sự kiện: Khi kéo xuống thì làm gì?
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadFoodsFromApi(); // Gọi lại API lấy món ăn
        });

        // Màu sắc vòng tròn xoay xoay (cho đẹp)
        swipeRefreshLayout.setColorSchemeResources(R.color.purple_700);

        // Xử lý sự kiện bấm nút Giỏ hàng
        fabCart.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CartActivity.class);
            startActivity(intent);
        });

        // Setup RecyclerViews
        setupCategoryRecyclerView();
        setupFoodRecyclerView();

        // --- [MỚI] Gọi API thay vì load từ SQLite ---
        loadCategories(); // Vẫn load Category từ SQLite để giữ giao diện menu ngang
        loadFoodsFromApi(); // Load món ăn từ Server
    }

    private void setupCategoryRecyclerView() {
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupFoodRecyclerView() {
        rvFoods.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        foodAdapter = new FoodAdapter(getContext(), foodList, this);
        rvFoods.setAdapter(foodAdapter);
    }

    // --- [MỚI] Hàm gọi API lấy danh sách món ăn ---
    private void loadFoodsFromApi() {
        // 1. Tạo Service
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // 2. Gọi API
        apiService.getListFood().enqueue(new Callback<List<Food>>() {
            @Override
            public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    foodList = response.body();
                    foodAdapter.updateData(foodList);
                     Toast.makeText(getContext(), "Đã cập nhật menu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {
                // Lỗi kết nối (Server chưa bật, sai IP, mất mạng...)
                swipeRefreshLayout.setRefreshing(false); // tat vong xoay
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadCategories() {
        categoryList = categoryDAO.getAll();
        if (categoryList != null && !categoryList.isEmpty()) {
            categoryAdapter = new CategoryAdapter(getContext(), categoryList, this);
            rvCategories.setAdapter(categoryAdapter);

            // [CŨ] Trước đây ta load món ăn theo category đầu tiên từ SQLite
            // loadFoodsByCategory(categoryList.get(0).getId());
        } else {
            // Toast.makeText(getContext(), "Không tìm thấy danh mục", Toast.LENGTH_SHORT).show();
        }
    }

    // [CŨ] Hàm này tạm thời không dùng vì ta đang load TOÀN BỘ món từ API
    /*
    private void loadFoodsByCategory(int categoryId) {
        List<Food> newFoodList = foodDAO.getFoodByCategory(categoryId);
        if (newFoodList != null) {
            foodAdapter.updateData(newFoodList);
        }
    }
    */

    @Override
    public void onCategoryClick(Category category) {
        // [CŨ] Khi bấm category -> lọc món ăn từ SQLite
        // loadFoodsByCategory(category.getId());

        // [MỚI] Hiện tại API chưa có chức năng lọc theo Category,
        // nên tạm thời bấm vào chỉ hiện Toast thông báo
        Toast.makeText(getContext(), "Đang hiển thị tất cả món từ Server", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFoodClick(Food food) {
        Intent intent = new Intent(getContext(), FoodDetailActivity.class);

        // [CŨ] Chỉ gửi ID -> Detail tự tìm trong SQLite (Sai logic)
        // intent.putExtra("FOOD_ID", food.getId());

        // [MỚI] Gửi nguyên cả cục Food (Object) sang
        // Điều kiện: Class Food phải "implements Serializable" (Ta đã làm ở bước trước)
        intent.putExtra("FOOD_OBJECT", food);

        startActivity(intent);
    }

}