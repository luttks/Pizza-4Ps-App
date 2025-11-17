 package com.example.pizzaapp.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
}