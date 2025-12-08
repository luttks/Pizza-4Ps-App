package com.example.pizzaapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pizzaapp.R;
import com.example.pizzaapp.api.ApiService;
import com.example.pizzaapp.api.RetrofitClient;
import com.example.pizzaapp.helper.UserSession;
import com.example.pizzaapp.model.GoogleLoginRequest;
import com.example.pizzaapp.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private UserSession userSession;

    // --- KHAI BÁO BIẾN CHO GOOGLE ---
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btnGoogleSignIn;
    private static final int RC_SIGN_IN = 9001;
    // --------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Kiểm tra session và Google Account cũ
        userSession = new UserSession(this);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (userSession.isLoggedIn() || account != null) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

        // 2. Ánh xạ View
        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        tvGoToRegister = findViewById(R.id.tv_go_to_register);
        btnGoogleSignIn = findViewById(R.id.btn_google_sign_in); // Ánh xạ nút Google

        // 3. Cấu hình Google Sign In
        // LƯU Ý: Thay YOUR_WEB_CLIENT_ID bằng ID lấy trong google-services.json
        // (Tìm dòng client_id có client_type: 3)
        String webClientId = "378592990830-63hdaekh6d935l3foc497rboe8uu1viq.apps.googleusercontent.com";

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 4. Bắt sự kiện
        btnLogin.setOnClickListener(v -> handleLoginNormal());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // Sự kiện click nút Google
        btnGoogleSignIn.setOnClickListener(v -> signInGoogle());
    }

    // --- LOGIC ĐĂNG NHẬP THƯỜNG ---
    private void handleLoginNormal() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if(email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        User loginRequest = new User();
        loginRequest.setEmail(email);
        loginRequest.setPassword(pass);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.loginUser(loginRequest).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    userSession.createLoginSession(user);
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- LOGIC ĐĂNG NHẬP GOOGLE ---
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kết quả trả về từ Intent của Google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Lấy được ID Token từ Google
            String idToken = account.getIdToken();
            Log.d("GoogleLogin", "Token: " + idToken);

            // Gửi Token này lên Backend để xác thực
            sendGoogleTokenToBackend(idToken);

        } catch (ApiException e) {
            Log.w("GoogleLogin", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Đăng nhập Google thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendGoogleTokenToBackend(String idToken) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Tạo request body (Bạn cần tạo class GoogleLoginRequest trong package model)
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);

        apiService.loginWithGoogle(request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    // Lưu session
                    userSession.createLoginSession(user);

                    Toast.makeText(LoginActivity.this, "Xin chào " + user.getName(), Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi xác thực Server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối Backend", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}