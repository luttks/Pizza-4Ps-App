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
import com.example.pizzaapp.activity.FoodDetailActivity; // Activity chúng ta sẽ tạo ở bước 5
import com.example.pizzaapp.adapter.CategoryAdapter;
import com.example.pizzaapp.adapter.FoodAdapter;
import com.example.pizzaapp.database.CategoryDAO;
import com.example.pizzaapp.database.FoodDAO;
import com.example.pizzaapp.model.Category;
import com.example.pizzaapp.model.Food;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.pizzaapp.activity.CartActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener, FoodAdapter.OnFoodClickListener {

    private RecyclerView rvCategories, rvFoods;
    private CategoryAdapter categoryAdapter;
    private FoodAdapter foodAdapter;

    private CategoryDAO categoryDAO;
    private FoodDAO foodDAO;

    private List<Category> categoryList;
    private List<Food> foodList;
    private FloatingActionButton fabCart;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment này
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
        foodDAO = new FoodDAO(getContext());

        // Khởi tạo danh sách
        foodList = new ArrayList<>();

        // Ánh xạ nút FAB
        fabCart = view.findViewById(R.id.fab_cart);

        // Xử lý sự kiện bấm nút Giỏ hàng
        fabCart.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CartActivity.class);
            startActivity(intent);
        });

        // Setup RecyclerViews
        setupCategoryRecyclerView();
        setupFoodRecyclerView();

        // Load dữ liệu
        loadCategories();
    }

    private void setupCategoryRecyclerView() {
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupFoodRecyclerView() {
        rvFoods.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        // Khởi tạo FoodAdapter (với danh sách rỗng) và gán nó
        foodAdapter = new FoodAdapter(getContext(), foodList, this);
        rvFoods.setAdapter(foodAdapter);
    }

    private void loadCategories() {
        categoryList = categoryDAO.getAll(); // Gọi DAO
        if (categoryList != null && !categoryList.isEmpty()) {
            categoryAdapter = new CategoryAdapter(getContext(), categoryList, this);
            rvCategories.setAdapter(categoryAdapter);

            // Tự động load món ăn cho category đầu tiên
            loadFoodsByCategory(categoryList.get(0).getId());
        } else {
            Toast.makeText(getContext(), "Không tìm thấy danh mục", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFoodsByCategory(int categoryId) {
        List<Food> newFoodList = foodDAO.getFoodByCategory(categoryId); // Gọi DAO
        if (newFoodList != null) {
            foodAdapter.updateData(newFoodList); // Cập nhật dữ liệu cho adapter
        }
    }

    // Xử lý khi bấm vào 1 Category
    @Override
    public void onCategoryClick(Category category) {
        // Khi bấm vào category, load danh sách món ăn tương ứng
        loadFoodsByCategory(category.getId());
    }

    // Xử lý khi bấm vào 1 Food
    @Override
    public void onFoodClick(Food food) {
        // Mở FoodDetailActivity và truyền ID của món ăn qua
        Intent intent = new Intent(getContext(), FoodDetailActivity.class);
        intent.putExtra("FOOD_ID", food.getId()); // "FOOD_ID" là key
        startActivity(intent);
    }
}
