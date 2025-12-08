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
import com.example.pizzaapp.api.ApiClient; // Dùng ApiClient singleton
import com.example.pizzaapp.api.ApiService;
import com.example.pizzaapp.helper.UserSession;
import com.example.pizzaapp.model.Order;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnOrderClickListener {

    private RecyclerView rvHistory;
    private HistoryAdapter historyAdapter;
    private List<Order> orderList;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvHistory = view.findViewById(R.id.rv_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(getContext(), orderList, this);
        rvHistory.setAdapter(historyAdapter);

        // Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        UserSession session = new UserSession(getContext());
        if (session.getUser() == null) return;

        int userId = session.getUser().getId();

        // GỌI API LẤY DANH SÁCH ĐƠN HÀNG
        apiService.getOrdersByUserId(userId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    // Đảo ngược list để đơn mới nhất lên đầu (tuỳ chọn)
                    // Collections.reverse(orderList);
                    historyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải lịch sử: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onOrderClick(long orderId) {
        // Chức năng xem chi tiết (làm sau)
        // Intent intent = new Intent(getContext(), OrderDetailActivity.class);
        // intent.putExtra("ORDER_ID", orderId);
        // startActivity(intent);
    }

    @Override
    public void onCancelOrder(long orderId, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc muốn hủy đơn hàng này?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {

                    // GỌI API HỦY ĐƠN
                    callApiCancelOrder(orderId, position);

                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void callApiCancelOrder(long orderId, int position) {
        apiService.cancelOrder(orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã hủy đơn hàng!", Toast.LENGTH_SHORT).show();
                    // Xóa khỏi list và update UI
                    orderList.remove(position);
                    historyAdapter.notifyItemRemoved(position);
                    historyAdapter.notifyItemRangeChanged(position, orderList.size());
                } else {
                    Toast.makeText(getContext(), "Lỗi hủy đơn: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}