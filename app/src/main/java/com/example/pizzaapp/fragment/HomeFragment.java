package com.example.pizzaapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher; // Import TextWatcher
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pizzaapp.R;
import com.example.pizzaapp.activity.CartActivity;
import com.example.pizzaapp.activity.FoodDetailActivity;
import com.example.pizzaapp.adapter.CategoryAdapter;
import com.example.pizzaapp.adapter.FoodAdapter;
import com.example.pizzaapp.api.ApiService;
import com.example.pizzaapp.model.Category;
import com.example.pizzaapp.model.Food;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private RecyclerView rcvCategory, rcvFood;
    private CategoryAdapter categoryAdapter;
    private FoodAdapter foodAdapter;
    private FloatingActionButton fabCart;

    private List<Category> mListCategory;
    private List<Food> mListFood;

    // List dự phòng để tìm kiếm
    private List<Food> mListFoodFull;

    private ApiService apiService;
    private EditText edtSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Ánh xạ View
        rcvCategory = view.findViewById(R.id.rcv_category);
        rcvFood = view.findViewById(R.id.rv_foods);
        fabCart = view.findViewById(R.id.fab_cart);

        // --- FIX 1: Ánh xạ EditText Search ---
        edtSearch = view.findViewById(R.id.edt_search);
        // -------------------------------------

        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        // --- FIX 2: Bắt sự kiện gõ chữ để gọi hàm tìm kiếm ---
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Gọi hàm lọc khi text thay đổi
                filterFood(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        // ----------------------------------------------------

        // 2. Khởi tạo Retrofit & ApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // 3. Setup RecyclerViews
        mListCategory = new ArrayList<>();
        // --- FIX 3: Khởi tạo list gốc để tránh NullPointerException ---
        mListFoodFull = new ArrayList<>();
        mListFood = new ArrayList<>();
        // -------------------------------------------------------------

        categoryAdapter = new CategoryAdapter(getContext(), mListCategory, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                callApiGetFoodsByCategory(category.getId());
            }
        });

        rcvCategory.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rcvCategory.setAdapter(categoryAdapter);

        foodAdapter = new FoodAdapter(getContext(), mListFood, new FoodAdapter.OnFoodClickListener() {
            @Override
            public void onFoodClick(Food food) {
                Intent intent = new Intent(getContext(), FoodDetailActivity.class);
                intent.putExtra("FOOD_OBJECT", food);
                startActivity(intent);
            }
        });

        rcvFood.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rcvFood.setAdapter(foodAdapter);

        callApiGetCategories();
        callApiGetAllFoods();

        return view;
    }

    private void filterFood(String text) {
        List<Food> filteredList = new ArrayList<>();

        // Kiểm tra nếu list gốc chưa có dữ liệu thì return để tránh lỗi
        if (mListFoodFull == null || mListFoodFull.isEmpty()) {
            return;
        }

        if (text == null || text.isEmpty()) {
            filteredList.addAll(mListFoodFull);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (Food item : mListFoodFull) {
                if (item.getName().toLowerCase().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
        }

        // Cập nhật lại RecyclerView
        if (foodAdapter != null) {
            foodAdapter.updateData(filteredList);
        }
    }

    // --- CÁC HÀM GỌI API ---

    private void callApiGetCategories() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryAdapter.setData(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {}
        });
    }

    private void callApiGetAllFoods() {
        apiService.getListFood().enqueue(new Callback<List<Food>>() {
            @Override
            public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // --- FIX 4: Lưu dữ liệu vào list gốc (Full) ---
                    mListFoodFull.clear();
                    mListFoodFull.addAll(response.body());
                    // ----------------------------------------------

                    foodAdapter.updateData(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {
                if(getContext() != null)
                    Toast.makeText(getContext(), "Lỗi load món: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callApiGetFoodsByCategory(int categoryId) {
        // Reset thanh tìm kiếm về rỗng khi chọn category mới
        edtSearch.setText("");

        apiService.getFoodsByCategory(categoryId).enqueue(new Callback<List<Food>>() {
            @Override
            public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // --- FIX 5: Cập nhật list gốc theo category mới ---
                    mListFoodFull.clear();
                    mListFoodFull.addAll(response.body());
                    // -------------------------------------------------

                    foodAdapter.updateData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {
                if(getContext() != null)
                    Toast.makeText(getContext(), "Lỗi lọc món: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
