package com.example.pizzaapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide; // Import Glide
import com.example.pizzaapp.R;
import com.example.pizzaapp.database.CartDAO;
// import com.example.pizzaapp.database.FoodDAO; // Không dùng cái này nữa
import com.example.pizzaapp.model.Cart;
import com.example.pizzaapp.model.Food;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.NumberFormat;
import java.util.Locale;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView ivFoodDetailImage;
    private TextView tvFoodDetailName, tvFoodDetailPrice, tvFoodDetailDescription, tvQuantity;
    private Button btnMinusQuantity, btnPlusQuantity, btnAddToCart;
    private CollapsingToolbarLayout collapsingToolbar;

    // private FoodDAO foodDAO; // Bỏ
    private CartDAO cartDAO;
    private Food currentFood;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // foodDAO = new FoodDAO(this); // Bỏ
        cartDAO = new CartDAO(this);

        initViews();
        setupToolbar();

        // --- [MỚI] NHẬN DỮ LIỆU TỪ INTENT ---
        // Thay vì nhận ID, ta nhận cả Object Food
        currentFood = (Food) getIntent().getSerializableExtra("FOOD_OBJECT");

        if (currentFood != null) {
            loadFoodDetails();
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin món", Toast.LENGTH_SHORT).show();
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
        tvFoodDetailName.setText(currentFood.getName());
        tvFoodDetailDescription.setText(currentFood.getDescription());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvFoodDetailPrice.setText(formatter.format(currentFood.getPrice()));

        // --- [MỚI] DÙNG GLIDE LOAD ẢNH URL ---
        String imageUrl = currentFood.getImage();
        // Fix localhost cho máy ảo
        if (imageUrl.contains("localhost")) {
            imageUrl = imageUrl.replace("localhost", "10.0.2.2");
        }

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(ivFoodDetailImage);

        updateAddToCartButtonText();
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

        btnAddToCart.setOnClickListener(v -> addItemToCart());
    }

    private void updateAddToCartButtonText() {
        double totalPrice = currentFood.getPrice() * quantity;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        btnAddToCart.setText(String.format("Add to cart (%s)", formatter.format(totalPrice)));
    }

    private void addItemToCart() {
        if (currentFood == null) return;

        Cart cartItem = new Cart();
        cartItem.setId(currentFood.getId());
        cartItem.setFoodName(currentFood.getName());
        cartItem.setPrice(currentFood.getPrice());
        cartItem.setQuantity(quantity);
        cartItem.setImage(currentFood.getImage()); // Lưu URL ảnh vào giỏ hàng
        cartItem.setCustomization("Default");

        // Lưu vào SQLite Cart (Vẫn dùng SQLite cho giỏ hàng là OK)
        long result = cartDAO.addToCart(cartItem);

        if (result != -1) {
            Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}