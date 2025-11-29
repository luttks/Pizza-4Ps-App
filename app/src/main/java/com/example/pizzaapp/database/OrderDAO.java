package com.example.pizzaapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pizzaapp.helper.DateTimeHelper;
import com.example.pizzaapp.helper.PizzaAppDbHelper;
import com.example.pizzaapp.model.Cart;
import com.example.pizzaapp.model.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDAO {
    private SQLiteDatabase db;
    private PizzaAppDbHelper dbHelper;

    public OrderDAO(Context context) {
        dbHelper = new PizzaAppDbHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    // Hàm tạo đơn hàng mới từ danh sách giỏ hàng
    public boolean createOrder(int userId, List<Cart> cartItems, double totalPrice) {
        db.beginTransaction(); // Bắt đầu giao dịch (để đảm bảo toàn vẹn dữ liệu)
        try {
            // 1. Insert vào bảng Orders
            ContentValues orderValues = new ContentValues();
            orderValues.put(PizzaAppDbHelper.KEY_USER_ID, userId);
            orderValues.put(PizzaAppDbHelper.KEY_ORDER_DATE, DateTimeHelper.toString(new Date()));
            orderValues.put(PizzaAppDbHelper.KEY_TOTAL_PRICE, totalPrice);
            orderValues.put(PizzaAppDbHelper.KEY_STATUS, "Processing"); // Trạng thái mặc định

            long orderId = db.insert(PizzaAppDbHelper.TABLE_ORDERS, null, orderValues);

            if (orderId == -1) return false; // Lỗi khi tạo đơn

            // 2. Insert từng món vào bảng OrderItems
            for (Cart item : cartItems) {
                ContentValues itemValues = new ContentValues();
                itemValues.put(PizzaAppDbHelper.KEY_ORDER_ID, orderId);
                itemValues.put(PizzaAppDbHelper.KEY_FOOD_ID, item.getId());
                itemValues.put(PizzaAppDbHelper.KEY_FOOD_NAME, item.getFoodName());
                itemValues.put(PizzaAppDbHelper.KEY_QUANTITY, item.getQuantity());
                itemValues.put(PizzaAppDbHelper.KEY_PRICE, item.getPrice());

                db.insert(PizzaAppDbHelper.TABLE_ORDER_ITEMS, null, itemValues);
            }

            db.setTransactionSuccessful(); // Đánh dấu giao dịch thành công
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction(); // Kết thúc giao dịch
        }
    }
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> list = new ArrayList<>();

        // Sắp xếp giảm dần theo ID để đơn mới nhất lên đầu (DESC)
        String sql = "SELECT * FROM " + PizzaAppDbHelper.TABLE_ORDERS +
                " WHERE " + PizzaAppDbHelper.KEY_USER_ID + " = ? ORDER BY " + PizzaAppDbHelper.KEY_ID + " DESC";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                // Map dữ liệu từ SQLite vào Object
                // Lưu ý: Cần đảm bảo model Order của bạn có các setter này
                order.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PizzaAppDbHelper.KEY_ID)));
                order.setDate(cursor.getString(cursor.getColumnIndexOrThrow(PizzaAppDbHelper.KEY_ORDER_DATE)));
                order.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(PizzaAppDbHelper.KEY_TOTAL_PRICE)));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(PizzaAppDbHelper.KEY_STATUS)));

                list.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
