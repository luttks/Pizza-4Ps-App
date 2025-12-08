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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class AccountFragment extends Fragment {

    private TextView tvEmail, tvPhone, tvAddress;
    private Button btnLogout;
    private UserSession session;

    // Thêm biến Google Client
    private GoogleSignInClient mGoogleSignInClient;

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

        // 1. Ánh xạ View
        tvEmail = view.findViewById(R.id.tv_acc_email);
        tvPhone = view.findViewById(R.id.tv_acc_phone);
        tvAddress = view.findViewById(R.id.tv_acc_address);
        btnLogout = view.findViewById(R.id.btn_logout);

        // 2. Khởi tạo Session
        session = new UserSession(getContext());

        // 3. Khởi tạo Google Client (để dùng cho việc đăng xuất)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // 4. Hiển thị thông tin User
        if (session.isLoggedIn()) {
            User user = session.getUser();
            tvEmail.setText("Email: " + user.getEmail());
            // Kiểm tra null để tránh lỗi crash nếu user Google không có sđt/địa chỉ
            tvPhone.setText("Phone: " + (user.getPhone() != null ? user.getPhone() : "Chưa cập nhật"));
            tvAddress.setText("Address: " + (user.getAddress() != null ? user.getAddress() : "Chưa cập nhật"));
        }

        // 5. Xử lý sự kiện Đăng xuất
        btnLogout.setOnClickListener(v -> performLogout());
    }

    private void performLogout() {
        // B1: Xóa Session lưu trong máy (SharedPreferences)
        session.logoutUser();

        // B2: Đăng xuất khỏi Google (Quan trọng)
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            // B3: Sau khi Google sign out xong thì mới chuyển màn hình
            Toast.makeText(getContext(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), LoginActivity.class);
            // Cờ này để xóa sạch stack Activity, user không thể bấm Back để quay lại
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}