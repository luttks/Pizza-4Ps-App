package com.example.pizzaapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.pizzaapp.R;
import com.example.pizzaapp.database.CartDAO;
import com.example.pizzaapp.model.Cart;
import com.example.pizzaapp.model.Food;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.NumberFormat;
import java.util.Locale;
import android.graphics.Color;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView ivFoodDetailImage;
    private TextView tvFoodDetailName, tvFoodDetailPrice, tvFoodDetailDescription, tvQuantity;
    private Button btnMinusQuantity, btnPlusQuantity, btnAddToCart;
    private CollapsingToolbarLayout collapsingToolbar;

    private TextView tvAllergyInfo;

    private CartDAO cartDAO;
    private Food currentFood;
    private int quantity = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        cartDAO = new CartDAO(this);
        initViews();
        setupToolbar();

        // Nhận object Food từ Intent
        currentFood = (Food) getIntent().getSerializableExtra("FOOD_OBJECT");

        if (currentFood != null) {
            loadFoodDetails();
        } else {
            Toast.makeText(this, "Không lấy được thông tin món!", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupEventHandlers();
    }

    private void initViews() {
        ivFoodDetailImage = findViewById(R.id.iv_food_detail_image);
        tvFoodDetailName = findViewById(R.id.tv_food_detail_name);
        tvFoodDetailPrice = findViewById(R.id.tv_food_detail_price);
        tvFoodDetailDescription = findViewById(R.id.tv_food_detail_description);
        tvQuantity = findViewById(R.id.tv_quantity);
        btnMinusQuantity = findViewById(R.id.btn_minus_quantity);
        btnPlusQuantity = findViewById(R.id.btn_plus_quantity);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        tvAllergyInfo = findViewById(R.id.tv_allergy_info);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadFoodDetails() {
        collapsingToolbar.setTitle(currentFood.getName());
        collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
        tvFoodDetailName.setText(currentFood.getName());
        tvFoodDetailDescription.setText(currentFood.getDescription());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvFoodDetailPrice.setText(formatter.format(currentFood.getPrice()));

        // Load ảnh chi tiết
        String imageUrl = currentFood.getImage();
        if (imageUrl != null && imageUrl.contains("localhost")) {
            imageUrl = imageUrl.replace("localhost", "10.0.2.2");
        }
        Glide.with(this).load(imageUrl).into(ivFoodDetailImage);
        if (currentFood.getAllergyInfo() != null && !currentFood.getAllergyInfo().isEmpty()) {
            tvAllergyInfo.setText("⚠️ Cảnh báo dị ứng: " + currentFood.getAllergyInfo());
            tvAllergyInfo.setVisibility(View.VISIBLE);
        } else {
            tvAllergyInfo.setVisibility(View.GONE);
        }
        updateAddToCartButtonText();
    }

    private void updateAddToCartButtonText() {
        double totalPrice = currentFood.getPrice() * quantity;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        btnAddToCart.setText(String.format("Thêm vào giỏ (%s)", formatter.format(totalPrice)));
    }

    private void setupEventHandlers() {
        btnPlusQuantity.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            updateAddToCartButtonText();
        });

        btnMinusQuantity.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateAddToCartButtonText();
            }
        });

        // SỰ KIỆN QUAN TRỌNG: ADD TO CART
        btnAddToCart.setOnClickListener(v -> addItemToCart());
    }

    private void addItemToCart() {
        Cart cartItem = new Cart();
        cartItem.setId(currentFood.getId());       // ID món ăn
        cartItem.setFoodName(currentFood.getName());
        cartItem.setPrice(currentFood.getPrice());
        cartItem.setQuantity(quantity);
        cartItem.setImage(currentFood.getImage()); // Lưu URL ảnh để hiển thị bên giỏ hàng
        cartItem.setCustomization("Mặc định");

        // Gọi DAO để lưu vào SQLite
        long result = cartDAO.addToCart(cartItem);

        if (result != -1) {
            Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình chi tiết, quay lại menu
        } else {
            Toast.makeText(this, "Lỗi thêm giỏ hàng!", Toast.LENGTH_SHORT).show();
        }
    }
}