package com.example.pizzaapp.database;
// package com.example.pizzaapp.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pizzaapp.model.Food; // Import model Food của bạn

import java.util.ArrayList;
import java.util.List;

public class FoodDAO {
    private SQLiteDatabase db;

    public FoodDAO(Context context) {
        PizzaAppDbHelper dbHelper = new PizzaAppDbHelper(context);
        this.db = dbHelper.getReadableDatabase();
    }

    @SuppressLint("Range")
    private List<Food> get(String sql, String... selectArgs) {
        List<Food> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, selectArgs);
        while (cursor.moveToNext()) {
            Food food = new Food();
            food.setId(cursor.getInt(cursor.getColumnIndex(PizzaAppDbHelper.KEY_ID)));
            food.setName(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_NAME)));
            food.setDescription(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_DESCRIPTION)));
            food.setPrice(cursor.getDouble(cursor.getColumnIndex(PizzaAppDbHelper.KEY_PRICE)));
            food.setImage(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_IMAGE)));
            food.setCategoryId(cursor.getInt(cursor.getColumnIndex(PizzaAppDbHelper.KEY_CATEGORY_ID)));
            list.add(food);
        }
        cursor.close();
        return list;
    }

    // Lấy tất cả món ăn (có thể dùng cho trang "All")
    public List<Food> getAll() {
        String sql = "SELECT * FROM " + PizzaAppDbHelper.TABLE_FOODS;
        return get(sql);
    }

    // Lọc món ăn theo Category (giống hệt getAllByLophoc của bạn)
    public List<Food> getFoodByCategory(int categoryId) {
        String sql = "SELECT * FROM " + PizzaAppDbHelper.TABLE_FOODS + " WHERE " + PizzaAppDbHelper.KEY_CATEGORY_ID + " = ?";
        return get(sql, String.valueOf(categoryId));
    }

    // Lấy chi tiết 1 món ăn
    @SuppressLint("Range")
    public Food getFoodById(int foodId) {
        String sql = "SELECT * FROM " + PizzaAppDbHelper.TABLE_FOODS + " WHERE " + PizzaAppDbHelper.KEY_ID + " = ?";
        List<Food> list = get(sql, String.valueOf(foodId));
        return list.isEmpty() ? null : list.get(0);
    }
}
