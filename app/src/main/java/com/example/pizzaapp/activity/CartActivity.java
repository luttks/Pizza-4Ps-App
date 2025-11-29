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
import com.example.pizzaapp.database.CartDAO;
import com.example.pizzaapp.database.OrderDAO;
import com.example.pizzaapp.helper.UserSession;
import com.example.pizzaapp.model.Cart;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        cartAdapter.notifyDataSetChanged(); // Refresh UI
        updateTotalPrice();
    }

    // Xử lý sự kiện xóa món từ Adapter
    @Override
    public void onItemDeleted(Cart item) {
        cartDAO.deleteItem(item.getId());
        cartList.remove(item); // Xóa khỏi list trong RAM
        cartAdapter.notifyDataSetChanged(); // Refresh UI
        updateTotalPrice();
        Toast.makeText(this, "Đã xóa món ăn", Toast.LENGTH_SHORT).show();
    }

    private void handleCheckout() {
        if (cartList.isEmpty()) return;


        // 1. Khởi tạo session
        UserSession session = new UserSession(this);
        // 2. Lấy ID của người dùng đang đăng nhập
        int userId = session.getUser().getId();
        // ------------------------

        double total = 0;
        for (Cart item : cartList) {
            total += item.getPrice() * item.getQuantity();
        }

        boolean success = orderDAO.createOrder(userId, cartList, total);
        if (success) {
            // Xóa sạch giỏ hàng sau khi đặt thành công
            cartDAO.clearCart();
            Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
            finish(); // Đóng màn hình giỏ hàng
        } else {
            Toast.makeText(this, "Lỗi khi đặt hàng!", Toast.LENGTH_SHORT).show();
        }
    }
}