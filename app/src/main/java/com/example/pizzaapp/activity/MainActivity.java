package com.example.pizzaapp.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

// package com.example.pizzaapp.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pizzaapp.R;
import com.example.pizzaapp.fragment.HomeFragment; // Import fragment của bạn

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Chỉ tải fragment khi app mới khởi động
            loadFragment(new HomeFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment); // Thay thế FrameLayout bằng fragment
        ft.commit();
    }
}