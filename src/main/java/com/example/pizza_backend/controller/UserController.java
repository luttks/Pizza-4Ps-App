package com.example.pizza_backend.controller;

import com.example.pizza_backend.model.User;
import com.example.pizza_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}