package com.example.pizzaapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pizzaapp.R;
import com.example.pizzaapp.adapter.HistoryAdapter;
import com.example.pizzaapp.database.OrderDAO;
import com.example.pizzaapp.helper.UserSession;
import com.example.pizzaapp.model.Order;

import java.util.List;

public class HistoryFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_history, container, false); // Cần tạo layout này ở bước dưới
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvHistory = view.findViewById(R.id.rv_history);
        orderDAO = new OrderDAO(getContext());

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- ĐOẠN CODE ĐÃ SỬA ---
        // 1. Khởi tạo session (Fragment dùng getContext())
        UserSession session = new UserSession(getContext());
        // 2. Lấy ID user thật
        int userId = session.getUser().getId();

        // 3. Truyền ID thật vào hàm lấy lịch sử
        orderList = orderDAO.getOrdersByUserId(userId);
        // ------------------------

        historyAdapter = new HistoryAdapter(getContext(), orderList);
        rvHistory.setAdapter(historyAdapter);
    }
}
