package com.example.pizzaapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pizzaapp.R;
import com.example.pizzaapp.database.UserDAO;
import com.example.pizzaapp.model.User;
import com.example.pizzaapp.api.ApiService;
import com.example.pizzaapp.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    // 1. Khai báo thêm biến etName
    private EditText etName, etEmail, etPassword, etPhone, etAddress;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDAO = new UserDAO(this);

        // 2. Ánh xạ View (Tìm id et_reg_name)
        etName = findViewById(R.id.et_reg_name);
        etEmail = findViewById(R.id.et_reg_email);
        etPassword = findViewById(R.id.et_reg_password);
        etPhone = findViewById(R.id.et_reg_phone);
        etAddress = findViewById(R.id.et_reg_address);
        btnRegister = findViewById(R.id.btn_register);
        tvGoToLogin = findViewById(R.id.tv_go_to_login);

        btnRegister.setOnClickListener(v -> handleRegister());

        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        // 3. Lấy dữ liệu tên người dùng nhập
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Kiểm tra xem người dùng có nhập tên không
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields (Name, Email, Pass)", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User();
        // 4. Set tên thật vào Object (Thay vì gán cứng "New User")
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(pass);
        newUser.setPhone(phone);
        newUser.setAddress(address);

        // Gọi API
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.registerUser(newUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại (Email trùng?)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}