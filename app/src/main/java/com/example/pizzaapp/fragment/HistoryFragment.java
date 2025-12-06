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

        // 4. Khởi tạo adapter
        historyAdapter = new HistoryAdapter(getContext(), orderList, this);
        rvHistory.setAdapter(historyAdapter);
    }

    // Xử lý xem chi tiết
    // SỬA LỖI 1: Đổi int -> long
    @Override
    public void onOrderClick(long orderId) {
        Intent intent = new Intent(getContext(), OrderDetailActivity.class);
        // Intent có thể putLong thoải mái
        intent.putExtra("ORDER_ID", orderId);
        startActivity(intent);
    }

    // Xử lý hủy đơn
    // SỬA LỖI 2: Đổi int -> long
    @Override
    public void onCancelOrder(long orderId, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel this order?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    // Lưu ý: Nếu OrderDAO.deleteOrder nhận vào int, ta ép kiểu (int).
                    // Nếu sau này bạn sửa DAO thành long thì bỏ (int) đi.
                    boolean success = orderDAO.deleteOrder((int) orderId);

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