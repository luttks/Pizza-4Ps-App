package com.example.pizzaapp.helper;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.pizzaapp.model.User;

public class UserSession {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "PizzaAppSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_USER_ADDRESS = "userAddress";

    public UserSession(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Lưu phiên đăng nhập
    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.putString(KEY_USER_ADDRESS, user.getAddress());
        editor.commit();
    }

    // Kiểm tra đã đăng nhập chưa
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Lấy thông tin User hiện tại
    public User getUser() {
        if (!isLoggedIn()) return null;
        User user = new User();
        user.setId(sharedPreferences.getInt(KEY_USER_ID, -1));
        user.setEmail(sharedPreferences.getString(KEY_USER_EMAIL, null));
        user.setPhone(sharedPreferences.getString(KEY_USER_PHONE, null));
        user.setAddress(sharedPreferences.getString(KEY_USER_ADDRESS, null));
        return user;
    }

    // Đăng xuất
    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}
