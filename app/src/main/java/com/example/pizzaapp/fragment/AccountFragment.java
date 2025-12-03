package com.example.pizzaapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pizzaapp.R;
import com.example.pizzaapp.activity.LoginActivity;
import com.example.pizzaapp.helper.UserSession;
import com.example.pizzaapp.model.User;

public class AccountFragment extends Fragment {

    private TextView tvEmail, tvPhone, tvAddress;
    private Button btnLogout;
    private UserSession session;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ View
        tvEmail = view.findViewById(R.id.tv_acc_email);
        tvPhone = view.findViewById(R.id.tv_acc_phone);
        tvAddress = view.findViewById(R.id.tv_acc_address);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Khởi tạo Session
        session = new UserSession(getContext());

        // Lấy thông tin User và hiển thị
        if (session.isLoggedIn()) {
            User user = session.getUser();
            tvEmail.setText("Email: " + user.getEmail());
            tvPhone.setText("Phone: " + user.getPhone());
            tvAddress.setText("Address: " + user.getAddress());
        }

        // Xử lý sự kiện Đăng xuất
        btnLogout.setOnClickListener(v -> {
            session.logoutUser(); // Xóa session
            Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();

            // Chuyển về màn hình Login
            Intent intent = new Intent(getContext(), LoginActivity.class);
            // Xóa hết các activity trước đó để user không bấm Back quay lại được
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}