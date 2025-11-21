package com.example.pizzaapp.database;

// package com.example.pizzaapp.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pizzaapp.helper.PizzaAppDbHelper;
import com.example.pizzaapp.model.Cart; // Import model Cart của bạn

import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private SQLiteDatabase db;

    public CartDAO(Context context) {
        PizzaAppDbHelper dbHelper = new PizzaAppDbHelper(context);
        this.db = dbHelper.getWritableDatabase(); // Cần ghi/xóa
    }

    // Hàm lấy một món hàng trong giỏ
    @SuppressLint("Range")
    public Cart getItem(int foodId) {
        String sql = "SELECT * FROM " + PizzaAppDbHelper.TABLE_CART + " WHERE " + PizzaAppDbHelper.KEY_FOOD_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(foodId)});
        if (cursor.moveToNext()) {
            Cart cart = new Cart();
            cart.setId(cursor.getInt(cursor.getColumnIndex(PizzaAppDbHelper.KEY_FOOD_ID)));
            cart.setFoodName(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_FOOD_NAME)));
            cart.setPrice(cursor.getDouble(cursor.getColumnIndex(PizzaAppDbHelper.KEY_PRICE)));
            cart.setQuantity(cursor.getInt(cursor.getColumnIndex(PizzaAppDbHelper.KEY_QUANTITY)));
            cart.setImage(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_IMAGE)));
            cart.setCustomization(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_CUSTOMIZATION)));
            cursor.close();
            return cart;
        }
        cursor.close();
        return null;
    }

    // Hàm chính: Thêm vào giỏ. Nếu đã có thì tăng số lượng.
    public long addToCart(Cart cartItem) {
        Cart existingItem = getItem(cartItem.getId()); // Dùng foodId làm id của Cart

        if (existingItem != null) {
            // Đã tồn tại -> Cập nhật số lượng
            int newQuantity = existingItem.getQuantity() + cartItem.getQuantity();
            ContentValues values = new ContentValues();
            values.put(PizzaAppDbHelper.KEY_QUANTITY, newQuantity);
            // Cập nhật cả tùy chỉnh nếu có
            values.put(PizzaAppDbHelper.KEY_CUSTOMIZATION, cartItem.getCustomization());
            return db.update(PizzaAppDbHelper.TABLE_CART, values,
                    PizzaAppDbHelper.KEY_FOOD_ID + " = ?",
                    new String[]{String.valueOf(cartItem.getId())});
        } else {
            // Chưa tồn tại -> Thêm mới
            ContentValues values = new ContentValues();
            values.put(PizzaAppDbHelper.KEY_FOOD_ID, cartItem.getId());
            values.put(PizzaAppDbHelper.KEY_FOOD_NAME, cartItem.getFoodName());
            values.put(PizzaAppDbHelper.KEY_PRICE, cartItem.getPrice());
            values.put(PizzaAppDbHelper.KEY_QUANTITY, cartItem.getQuantity());
            values.put(PizzaAppDbHelper.KEY_IMAGE, cartItem.getImage());
            values.put(PizzaAppDbHelper.KEY_CUSTOMIZATION, cartItem.getCustomization());
            return db.insert(PizzaAppDbHelper.TABLE_CART, null, values);
        }
    }

    // Cập nhật số lượng (dùng cho nút + / - trong giỏ hàng)
    public int updateQuantity(int foodId, int newQuantity) {
        ContentValues values = new ContentValues();
        values.put(PizzaAppDbHelper.KEY_QUANTITY, newQuantity);
        return db.update(PizzaAppDbHelper.TABLE_CART, values,
                PizzaAppDbHelper.KEY_FOOD_ID + " = ?",
                new String[]{String.valueOf(foodId)});
    }

    // Xóa 1 món khỏi giỏ
    public int deleteItem(int foodId) {
        return db.delete(PizzaAppDbHelper.TABLE_CART,
                PizzaAppDbHelper.KEY_FOOD_ID + " = ?",
                new String[]{String.valueOf(foodId)});
    }

    // Xóa sạch giỏ hàng (sau khi đặt hàng)
    public int clearCart() {
        return db.delete(PizzaAppDbHelper.TABLE_CART, null, null);
    }

    // Lấy tất cả giỏ hàng
    @SuppressLint("Range")
    public List<Cart> getAllItems() {
        List<Cart> list = new ArrayList<>();
        String sql = "SELECT * FROM " + PizzaAppDbHelper.TABLE_CART;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Cart cart = new Cart();
            cart.setId(cursor.getInt(cursor.getColumnIndex(PizzaAppDbHelper.KEY_FOOD_ID)));
            cart.setFoodName(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_FOOD_NAME)));
            cart.setPrice(cursor.getDouble(cursor.getColumnIndex(PizzaAppDbHelper.KEY_PRICE)));
            cart.setQuantity(cursor.getInt(cursor.getColumnIndex(PizzaAppDbHelper.KEY_QUANTITY)));
            cart.setImage(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_IMAGE)));
            cart.setCustomization(cursor.getString(cursor.getColumnIndex(PizzaAppDbHelper.KEY_CUSTOMIZATION)));
            list.add(cart);
        }
        cursor.close();
        return list;
    }
}
