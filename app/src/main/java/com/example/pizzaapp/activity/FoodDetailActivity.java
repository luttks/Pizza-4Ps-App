package com.example.pizzaapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.pizzaapp.R;
import com.example.pizzaapp.database.CartDAO;
import com.example.pizzaapp.database.FoodDAO;
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

    private FoodDAO foodDAO;
    private CartDAO cartDAO;
    private Food currentFood; // Biến lưu món ăn đang xem
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);



        // Khởi tạo DAO
        foodDAO = new FoodDAO(this);
        cartDAO = new CartDAO(this);

        // Ánh xạ views
        initViews();

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị mũi tên <
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Ẩn tiêu đề mặc định nếu muốn
        }
        toolbar.setNavigationOnClickListener(v -> finish()); // Đóng activity khi bấm back

        // Lấy foodId từ Intent
        int foodId = getIntent().getIntExtra("FOOD_ID", -1);

        if (foodId != -1) {
            // Lấy thông tin món ăn từ DB
            currentFood = foodDAO.getFoodById(foodId);
            if (currentFood != null) {
                // Hiển thị thông tin lên UI
                loadFoodDetails();
            }
        }

        // Xử lý sự kiện
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

    private void loadFoodDetails() {
        // Set tên món ăn cho CollapsingToolbar
        collapsingToolbar.setTitle(currentFood.getName());

        // Set các TextView
        tvFoodDetailName.setText(currentFood.getName());
        tvFoodDetailDescription.setText(currentFood.getDescription());

        // Định dạng tiền tệ VND
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvFoodDetailPrice.setText(formatter.format(currentFood.getPrice()));

        // Set ảnh (giống như trong adapter)
        int imageId = getResources().getIdentifier(currentFood.getImage(), "drawable", getPackageName());
        if (imageId != 0) {
            ivFoodDetailImage.setImageResource(imageId);
        } else {
            ivFoodDetailImage.setImageResource(R.drawable.ic_launcher_background); // Ảnh mặc định
        }

        // Cập nhật nút "Add to cart"
        updateAddToCartButtonText();
    }

    private void setupEventHandlers() {
        // Nút tăng số lượng
        btnPlusQuantity.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            updateAddToCartButtonText();
        });

        // Nút giảm số lượng
        btnMinusQuantity.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateAddToCartButtonText();
            }
        });

        // Nút Thêm vào giỏ
        btnAddToCart.setOnClickListener(v -> {
            addItemToCart();
        });
    }

    // Cập nhật text cho nút Add to Cart (vd: "Add (294,000đ)")
    private void updateAddToCartButtonText() {
        double totalPrice = currentFood.getPrice() * quantity;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        btnAddToCart.setText(String.format("Add to cart (%s)", formatter.format(totalPrice)));
    }

    private void addItemToCart() {
        if (currentFood == null) return;

        // Tạo đối tượng Cart
        Cart cartItem = new Cart();
        cartItem.setId(currentFood.getId()); // Dùng foodId làm id cho Cart
        cartItem.setFoodName(currentFood.getName());
        cartItem.setPrice(currentFood.getPrice());
        cartItem.setQuantity(quantity);
        cartItem.setImage(currentFood.getImage());
        // Tùy chỉnh (customization) sẽ được thêm ở bước nâng cao
        cartItem.setCustomization("Default");

        // Gọi CartDAO để thêm
        long result = cartDAO.addToCart(cartItem);

        if (result != -1) {
            Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity sau khi thêm thành công
        } else {
            Toast.makeText(this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}
