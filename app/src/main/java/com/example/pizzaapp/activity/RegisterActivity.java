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

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etPhone, etAddress;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDAO = new UserDAO(this);

        etEmail = findViewById(R.id.et_reg_email);
        etPassword = findViewById(R.id.et_reg_password);
        etPhone = findViewById(R.id.et_reg_phone);
        etAddress = findViewById(R.id.et_reg_address);
        btnRegister = findViewById(R.id.btn_register);
        tvGoToLogin = findViewById(R.id.tv_go_to_login);

        btnRegister.setOnClickListener(v -> handleRegister());

        tvGoToLogin.setOnClickListener(v -> finish()); // Quay lại màn hình Login
    }

    private void handleRegister() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(pass);
        newUser.setPhone(phone);
        newUser.setAddress(address);

        long result = userDAO.registerUser(newUser);
        if (result > 0) {
            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình đăng ký để về đăng nhập
        } else {
            Toast.makeText(this, "Registration Failed! Email might exist.", Toast.LENGTH_SHORT).show();
        }
    }
}