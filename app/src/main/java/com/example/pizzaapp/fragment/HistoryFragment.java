package com.example.pizzaapp.fragment;

import android.app.AlertDialog;
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
import com.example.pizzaapp.activity.OrderDetailActivity;
import com.example.pizzaapp.adapter.HistoryAdapter;
import com.example.pizzaapp.database.OrderDAO;
import com.example.pizzaapp.helper.UserSession;
import com.example.pizzaapp.model.Order;

import java.util.List;

// QUAN TRỌNG: Thêm "implements HistoryAdapter.OnOrderClickListener" để fix lỗi override
public class HistoryFragment extends Fragment implements HistoryAdapter.OnOrderClickListener {

    private RecyclerView rvHistory;
    private HistoryAdapter historyAdapter;
    private OrderDAO orderDAO;
    private List<Order> orderList;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvHistory = view.findViewById(R.id.rv_history);
        orderDAO = new OrderDAO(getContext());

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        // 1. Khởi tạo session
        UserSession session = new UserSession(getContext());
        // 2. Lấy ID user thật
        int userId = session.getUser().getId();

        // 3. Truyền ID thật vào hàm lấy lịch sử
        orderList = orderDAO.getOrdersByUserId(userId);

        // 4. Khởi tạo adapter (lúc này constructor đã đúng)
        historyAdapter = new HistoryAdapter(getContext(), orderList, this);
        rvHistory.setAdapter(historyAdapter);
    }

    // Xử lý xem chi tiết
    @Override
    public void onOrderClick(int orderId) {
        Intent intent = new Intent(getContext(), OrderDetailActivity.class);
        intent.putExtra("ORDER_ID", orderId);
        startActivity(intent);
    }

    // Xử lý hủy đơn
    @Override
    public void onCancelOrder(int orderId, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel this order?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean success = orderDAO.deleteOrder(orderId);
                    if (success) {
                        orderList.remove(position);
                        historyAdapter.notifyItemRemoved(position);
                        // Cập nhật lại range để tránh lỗi index khi xóa tiếp
                        historyAdapter.notifyItemRangeChanged(position, orderList.size());
                        Toast.makeText(getContext(), "Order Cancelled", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to cancel", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}