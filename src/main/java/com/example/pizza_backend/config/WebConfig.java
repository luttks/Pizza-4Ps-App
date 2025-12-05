package com.example.pizza_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. Trỏ vào thư mục hiện tại "./uploads"
        Path uploadDir = Paths.get("./uploads");

        // 2. Chuyển thành URI chuẩn (Java sẽ tự thêm file:/// đúng chuẩn cho Mac)
        String uploadPath = uploadDir.toUri().toString();

        System.out.println("LOG_PATH: " + uploadPath); // In ra để kiểm tra xem đúng chưa

        // 3. Cấu hình Mapping
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}