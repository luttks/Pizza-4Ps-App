package com.example.pizzaapp.database;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pizzaapp.helper.PizzaAppDbHelper;
import com.example.pizzaapp.model.User;

public class UserDAO {
    private SQLiteDatabase db;

    public UserDAO(Context context) {
        PizzaAppDbHelper dbHelper = new PizzaAppDbHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    // Đăng ký tài khoản mới (giống insert của bạn)
    public long registerUser(User user) {
        ContentValues values = new ContentValues();
        values.put(PizzaAppDbHelper.KEY_EMAIL, user.getEmail());
        values.put(PizzaAppDbHelper.KEY_PASSWORD, user.getPassword()); // Cần mã hóa ở đây
        values.put(PizzaAppDbHelper.KEY_ADDRESS, user.getAddress());
        values.put(PizzaAppDbHelper.KEY_PHONE, user.getPhone());
        return db.insert(PizzaAppDbHelper.TABLE_USERS, null, values);
    }

    // Kiểm tra đăng nhập
    @SuppressLint("Range")
    public User checkUserLogin(String email, String password) {
        String sql = "SELECT * FROM " + PizzaAppDbHelper.TABLE_USERS + " WHERE "
                + PizzaAppDbHelper.KEY_EMAIL + " = ? AND "
                + PizzaAppDbHelper.KEY_PASSWORD + " = ?"; // Cần so sánh mật khẩu đã mã hóa

        Cursor cursor = db.rawQuery(sql, new String[]{email, password});
        if (cursor.moveToNext()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(PizzaAppDbHelper.KEY_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_EMAIL)));
            user.setAddress(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_ADDRESS)));
            user.setPhone(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_PHONE)));
            cursor.close();
            return user;
        }
        cursor.close();
        return null; // Đăng nhập thất bại
    }

    // Cập nhật thông tin (địa chỉ, SĐT)
    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(PizzaAppDbHelper.KEY_ADDRESS, user.getAddress());
        values.put(PizzaAppDbHelper.KEY_PHONE, user.getPhone());
        return db.update(PizzaAppDbHelper.TABLE_USERS, values,
                PizzaAppDbHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }
}
