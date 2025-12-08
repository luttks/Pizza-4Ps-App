package com.example.pizza_backend.controller;

import com.example.pizza_backend.dto.GoogleLoginRequest;
import com.example.pizza_backend.model.User;
import com.example.pizza_backend.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import org.springframework.http.HttpStatus;

import org.mindrot.jbcrypt.BCrypt;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 1. API Đăng ký

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already exists!");
        }

        String plainPassword = user.getPassword();
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        user.setPassword(hashedPassword);

        // --- THÊM DÒNG NÀY (QUAN TRỌNG) ---
        user.setId(null);
        // Lý do: Android gửi lên id=0, JPA sẽ tưởng là Update.
        // Set null để JPA hiểu là Insert (Tạo mới).
        // ----------------------------------

        User newUser = userRepository.save(user);
        return ResponseEntity.ok(newUser);
    }

    // 2. API Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        // 1. Tìm user theo email
        User user = userRepository.findByEmail(loginRequest.getEmail());

        // 2. Kiểm tra mật khẩu bằng BCrypt
        // checkpw(mật_khẩu_nhập_vào, mật_khẩu_đã_mã_hóa_trong_db)
        if (user != null && BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {

            // [Bảo mật] Trước khi trả về cho App, ta nên xóa password đi để nó không lộ ra
            // trong JSON
            user.setPassword("");

            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(401).body("Invalid Email or Password");
        }
    }

    // Thay bằng Client ID lấy từ google-services.json của Android App (client_type:
    // 3)
    private static final String GOOGLE_CLIENT_ID = "";

    @PostMapping("/login-google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequest requestBody) {
        try {
            // 1. Cấu hình bộ xác thực (Verifier)
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            // 2. Xác thực Token gửi từ Android
            GoogleIdToken idToken = verifier.verify(requestBody.getIdToken());

            if (idToken != null) {
                // Token hợp lệ -> Lấy thông tin User
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                // 3. Kiểm tra User trong Database
                User user = userRepository.findByEmail(email);

                if (user == null) {
                    // 3a. Nếu chưa có -> Tự động ĐĂNG KÝ
                    user = new User();
                    user.setEmail(email);
                    user.setName(name);
                    user.setPassword(""); // User Google không cần pass
                    user.setPhone(""); // Có thể cho user cập nhật sau
                    user.setAddress("");
                    userRepository.save(user);
                }

                // 3b. Nếu có rồi -> ĐĂNG NHẬP (Trả về User đó)
                return ResponseEntity.ok(user);

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi Server: " + e.getMessage());
        }
    }
}