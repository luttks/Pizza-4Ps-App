package com.example.pizzaapp.activity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pizzaapp.R;
import com.example.pizzaapp.adapter.OrderDetailAdapter;
import com.example.pizzaapp.database.OrderDAO;
import com.example.pizzaapp.model.Cart;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        int orderId = getIntent().getIntExtra("ORDER_ID", -1);

        Toolbar toolbar = findViewById(R.id.toolbar_order_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order Details");
        }

        // Xử lý sự kiện bấm nút Back
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvOrderId = findViewById(R.id.tv_detail_order_id);
        tvOrderId.setText("Order #" + orderId);

        RecyclerView rvItems = findViewById(R.id.rv_order_detail_items);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        OrderDAO orderDAO = new OrderDAO(this);
        List<Cart> items = orderDAO.getOrderItems(orderId);

        OrderDetailAdapter adapter = new OrderDetailAdapter(items);
        rvItems.setAdapter(adapter);
    }
}