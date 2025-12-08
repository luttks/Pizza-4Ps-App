package com.example.pizzaapp.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pizzaapp.R;
import com.example.pizzaapp.database.UserDAO;
import com.example.pizzaapp.model.User;
import com.example.pizzaapp.api.ApiService;
import com.example.pizzaapp.api.RetrofitClient;
import java.util.regex.Pattern;

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

        // ==================================================
        // 1. CHECK FULL NAME
        // ==================================================
        if (TextUtils.isEmpty(name)) {
            etName.setError("Họ tên không được để trống");
            etName.requestFocus();
            return;
        }
        // Regex: Chấp nhận chữ cái (kể cả tiếng Việt), khoảng trắng, dấu chấm. KHÔNG số, KHÔNG ký tự đặc biệt.
        // \p{L} đại diện cho chữ cái Unicode.
        if (!name.matches("^[\\p{L} .'-]+$")) {
            etName.setError("Tên không được chứa số hoặc ký tự đặc biệt");
            etName.requestFocus();
            return;
        }
        // Tên ngắn nhất VN thường khoảng 2-3 ký tự (Ví dụ: La O), dài nhất hiếm khi quá 50.
        if (name.length() < 2 || name.length() > 50) {
            etName.setError("Họ tên phải từ 2 đến 50 ký tự");
            etName.requestFocus();
            return;
        }
        // Check phải có ít nhất 2 từ (Mới - Optional)
        // Logic: Phải chứa ít nhất 1 dấu cách (sau khi đã trim)
        if (!name.contains(" ")) {
            etName.setError("Vui lòng nhập đầy đủ Họ và Tên (ví dụ: Nguyễn Văn A)");
            etName.requestFocus();
            return;
        }

        // ==================================================
        // 2. CHECK EMAIL
        // ==================================================
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email không được để trống");
            etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không đúng định dạng (ví dụ: abc@gmail.com)");
            etEmail.requestFocus();
            return;
        }

        // ==================================================
        // 3. CHECK PASSWORD
        // ==================================================
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Mật khẩu không được để trống");
            etPassword.requestFocus();
            return;
        }
        // Yêu cầu: 6-8 ký tự
        if (pass.length() < 6) {
            etPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
            etPassword.requestFocus();
            return;
        }

        // ==================================================
        // 4. CHECK PHONE NUMBER
        // ==================================================
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Số điện thoại không được để trống");
            etPhone.requestFocus();
            return;
        }
        // Regex: Bắt đầu bằng 0, theo sau là 9 chữ số. Tổng cộng 10 số.
        if (!phone.matches("^0\\d{9}$")) {
            etPhone.setError("SĐT phải gồm 10 số và bắt đầu bằng số 0");
            etPhone.requestFocus();
            return;
        }

        // ==================================================
        // 5. CHECK ADDRESS
        // ==================================================
        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Địa chỉ không được để trống");
            etAddress.requestFocus();
            return;
        }
        // Regex: Kiểm tra chuỗi có chứa ít nhất 1 chữ cái hay không (để tránh nhập toàn số hoặc ký tự lạ)
        boolean hasLetter = Pattern.compile("[\\p{L}]").matcher(address).find();
        if (!hasLetter) {
            etAddress.setError("Địa chỉ phải chứa ít nhất 1 ký tự chữ cái");
            etAddress.requestFocus();
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