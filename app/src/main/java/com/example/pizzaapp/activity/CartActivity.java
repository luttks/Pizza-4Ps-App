package com.example.pizzaapp.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import com.example.pizzaapp.model.User;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private RecyclerView rvCartItems;
    private TextView tvCartTotal, tvShippingAddress, btnChangeAddress;
    private RadioGroup rgPayment;
    private RadioButton rbCod, rbMomo;
    private Button btnCheckout;

    private CartAdapter cartAdapter;
    private List<Cart> cartList;
    private CartDAO cartDAO;
    private String currentShippingAddress = ""; // Biến lưu địa chỉ hiện tại cho đơn hàng này

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Giỏ hàng");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Init Views
        rvCartItems = findViewById(R.id.rv_cart_items);
        tvCartTotal = findViewById(R.id.tv_cart_total);
        btnCheckout = findViewById(R.id.btn_checkout);

        // Mới thêm
        tvShippingAddress = findViewById(R.id.tv_shipping_address);
        btnChangeAddress = findViewById(R.id.btn_change_address);
        rgPayment = findViewById(R.id.rg_payment);
        rbCod = findViewById(R.id.rb_cod);
        rbMomo = findViewById(R.id.rb_momo);

        cartDAO = new CartDAO(this);

        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartList, this);
        rvCartItems.setAdapter(cartAdapter);

        loadCartData();
        loadUserAddress(); // Hàm mới để load địa chỉ

        // Sự kiện đổi địa chỉ
        btnChangeAddress.setOnClickListener(v -> showChangeAddressDialog());

        // Nút Thanh toán
        btnCheckout.setOnClickListener(v -> handleCheckout());
    }

    // Logic 1: Load địa chỉ mặc định của User
    private void loadUserAddress() {
        UserSession session = new UserSession(this);
        User user = session.getUser();
        if (user != null && user.getAddress() != null && !user.getAddress().isEmpty()) {
            currentShippingAddress = user.getAddress();
            tvShippingAddress.setText(currentShippingAddress);
        } else {
            tvShippingAddress.setText("Chưa có địa chỉ. Vui lòng thêm!");
        }
    }

    // Logic 2: Hiển thị Dialog để nhập địa chỉ mới
    private void showChangeAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập địa chỉ giao hàng");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Số nhà, tên đường, phường/xã...");
        // Nếu đã có địa chỉ thì điền sẵn vào
        input.setText(currentShippingAddress);
        builder.setView(input);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String newAddress = input.getText().toString().trim();
            if (!newAddress.isEmpty()) {
                currentShippingAddress = newAddress;
                tvShippingAddress.setText(currentShippingAddress);
            } else {
                Toast.makeText(CartActivity.this, "Địa chỉ không được để trống!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void handleCheckout() {
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra địa chỉ
        if (currentShippingAddress.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ giao hàng!", Toast.LENGTH_LONG).show();
            showChangeAddressDialog(); // Mở luôn dialog cho người dùng nhập
            return;
        }

        UserSession session = new UserSession(this);
        User user = session.getUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }
        int userId = user.getId();

        // Kiểm tra phương thức thanh toán
        String selectedPayment = "COD";
        if (rbMomo.isChecked()) {
            selectedPayment = "MOMO";
            // TODO: Giai đoạn 4 sẽ xử lý gọi App MoMo ở đây
            Toast.makeText(this, "Tính năng MoMo đang phát triển, tạm thời dùng COD", Toast.LENGTH_SHORT).show();
            // Nếu muốn test flow thì cứ để nó chạy tiếp, hoặc return để chặn
        }

        double total = 0;
        for (Cart item : cartList) {
            total += item.getPrice() * item.getQuantity();
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setFoodId((long) cart.getId());
            orderItem.setFoodName(cart.getFoodName());
            orderItem.setPrice(cart.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setFoodImage(cart.getImage());
            orderItems.add(orderItem);
        }

        Order orderRequest = new Order();
        orderRequest.setUserId((long) userId);
        orderRequest.setTotalPrice(total);
        orderRequest.setStatus("Processing");
        String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
        orderRequest.setDate(currentDate);
        orderRequest.setItems(orderItems);

        // --- SET CÁC TRƯỜNG MỚI ---
        orderRequest.setShippingAddress(currentShippingAddress);
        orderRequest.setPaymentMethod(selectedPayment);

        sendOrderToServer(orderRequest, userId, total);
    }

    // ... Các hàm loadCartData, updateTotalPrice, onQuantityChanged, sendOrderToServer giữ nguyên ...
    // (Copy lại từ code cũ của bạn)
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
        btnCheckout.setEnabled(!cartList.isEmpty());
    }

    @Override
    public void onQuantityChanged(Cart item, int newQuantity) {
        cartDAO.updateQuantity(item.getId(), newQuantity);
        item.setQuantity(newQuantity);
        updateTotalPrice();
    }

    @Override
    public void onItemDeleted(Cart item) {
        cartDAO.deleteItem(item.getId());
        cartList.remove(item);
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    private void sendOrderToServer(Order orderRequest, int userId, double total) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Order> call = apiService.createOrder(orderRequest);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    cartDAO.clearCart();
                    Toast.makeText(CartActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(CartActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}