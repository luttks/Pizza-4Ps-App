package com.example.pizzaapp.activity;


import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pizzaapp.R;
import com.example.pizzaapp.adapter.CartAdapter;
import com.example.pizzaapp.api.ApiClient;
import com.example.pizzaapp.api.ApiService;
import com.example.pizzaapp.database.CartDAO;
import com.example.pizzaapp.database.OrderDAO;
import com.example.pizzaapp.helper.UserSession;
import com.example.pizzaapp.model.Cart;
import com.example.pizzaapp.model.Order;
import com.example.pizzaapp.model.OrderItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private RecyclerView rvCartItems;
    private TextView tvCartTotal;
    private Button btnCheckout;
    private CartAdapter cartAdapter;
    private List<Cart> cartList;

    private CartDAO cartDAO;
    private OrderDAO orderDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Init Views
        rvCartItems = findViewById(R.id.rv_cart_items);
        tvCartTotal = findViewById(R.id.tv_cart_total);
        btnCheckout = findViewById(R.id.btn_checkout);

        // Init DAO
        cartDAO = new CartDAO(this);
        orderDAO = new OrderDAO(this);

        // Setup RecyclerView
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartList, this);
        rvCartItems.setAdapter(cartAdapter);

        // Load data
        loadCartData();

        // Nút Thanh toán
        btnCheckout.setOnClickListener(v -> handleCheckout());
    }

    private void loadCartData() {
        cartList.clear();
        cartList.addAll(cartDAO.getAllItems());
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = 0;
        for (Cart item : cartList) {
            total += item.getPrice() * item.getQuantity();
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvCartTotal.setText(formatter.format(total));

        // Disable nút thanh toán nếu giỏ hàng trống
        btnCheckout.setEnabled(!cartList.isEmpty());
    }

    // Xử lý sự kiện thay đổi số lượng từ Adapter
    @Override
    public void onQuantityChanged(Cart item, int newQuantity) {
        cartDAO.updateQuantity(item.getId(), newQuantity);
        item.setQuantity(newQuantity); // Cập nhật list trong RAM
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    // Xử lý sự kiện xóa món từ Adapter
    @Override
    public void onItemDeleted(Cart item) {
        cartDAO.deleteItem(item.getId());
        cartList.remove(item); // Xóa khỏi list trong RAM
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
        Toast.makeText(this, "Đã xóa món ăn", Toast.LENGTH_SHORT).show();
    }

    private void handleCheckout() {
        if (cartList.isEmpty()) return;

        // 1. Lấy User ID
        UserSession session = new UserSession(this);
        // Kiểm tra an toàn: Nếu chưa login thì không cho đặt
        if (session.getUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }
        int userId = session.getUser().getId();

        double total = 0;
        for (Cart item : cartList) {
            total += item.getPrice() * item.getQuantity();
        }

        // 2. Map Cart -> OrderItem
        List<OrderItem> orderItems = new ArrayList<>();
        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setFoodId((long) cart.getId());
            orderItem.setFoodName(cart.getFoodName());
            orderItem.setPrice(cart.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItems.add(orderItem);
        }

        // 3. Tạo Order Object
        Order orderRequest = new Order();
        orderRequest.setUserId((long) userId);
        orderRequest.setTotalPrice(total);
        orderRequest.setStatus("Processing");

        // Thêm ngày giờ hiện tại
        String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
        orderRequest.setDate(currentDate);

        orderRequest.setItems(orderItems);

        // 4. Gửi lên Server
        sendOrderToServer(orderRequest, userId, total);
    }

    private void sendOrderToServer(Order orderRequest, int userId, double total) {
        // Khởi tạo API Service (Đảm bảo bạn đã có class ApiClient để lấy Retrofit instance)
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<Order> call = apiService.createOrder(orderRequest);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // A. THÀNH CÔNG TRÊN SERVER
                    // Server đã lưu vào DB (PostgreSQL/MySQL) xong.

                    // B. Lưu bản sao vào SQLite (để xem offline nếu cần - tuỳ chọn)
                    // orderDAO.createOrder(userId, cartList, total);

                    // C. Xóa giỏ hàng & Chuyển màn hình
                    cartDAO.clearCart();
                    Toast.makeText(CartActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    // Server trả về lỗi (400, 500...)
                    Toast.makeText(CartActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                // Lỗi mạng, rớt mạng, hoặc server chết
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}