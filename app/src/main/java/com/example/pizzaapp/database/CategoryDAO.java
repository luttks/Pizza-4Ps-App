 package com.example.pizzaapp.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pizzaapp.helper.PizzaAppDbHelper;
import com.example.pizzaapp.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private SQLiteDatabase db;

    public CategoryDAO(Context context) {
        PizzaAppDbHelper dbHelper = new PizzaAppDbHelper(context);
        this.db = dbHelper.getReadableDatabase(); // Chỉ cần đọc
    }

    // Hàm get() chung giống hệt của bạn, rất tốt!
    @SuppressLint("Range")
    private List<Category> get(String sql, String... selectArgs) {
        List<Category> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, selectArgs);
        while (cursor.moveToNext()) {
            Category category = new Category();
            category.setId(cursor.getInt(cursor.getColumnIndex(PizzaAppDbHelper.KEY_ID)));
            category.setName(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_NAME)));
            category.setImage(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_IMAGE)));
            list.add(category);
        }
        cursor.close();
        return list;
    }

    // Lấy tất cả danh mục
    public List<Category> getAll() {
        String sql = "SELECT * FROM " + PizzaAppDbHelper.TABLE_CATEGORIES;
        return get(sql);
    }

    // 1. Lấy danh sách tên danh mục (cho Spinner)
    public List<String> getCategoryNames() {
        List<String> names = new ArrayList<>();
        // Truy vấn cột 'name' từ bảng Categories
        String sql = "SELECT " + PizzaAppDbHelper.KEY_NAME + " FROM " + PizzaAppDbHelper.TABLE_CATEGORIES;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return names;
    }

    // 2. Lấy ID danh mục dựa trên tên (khi lưu món ăn)
    public int getCategoryIdByName(String name) {
        int id = -1;
        String sql = "SELECT " + PizzaAppDbHelper.KEY_ID + " FROM " + PizzaAppDbHelper.TABLE_CATEGORIES +
                " WHERE " + PizzaAppDbHelper.KEY_NAME + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{name});

        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }
}