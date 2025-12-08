package com.example.pizzaapp.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class PizzaAppDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Pizza4P_Local.db";
    private static final int DATABASE_VERSION = 1;

    // Tên các bảng
    public static final String TABLE_USERS = "Users";
    public static final String TABLE_CATEGORIES = "Categories";
    public static final String TABLE_FOODS = "Foods";
    public static final String TABLE_CART = "Cart";
    public static final String TABLE_ORDERS = "Orders";
    public static final String TABLE_ORDER_ITEMS = "OrderItems";

    // Các cột chung
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_PRICE = "price";

    // Cột bảng Users
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PHONE = "phone";

    // Cột bảng Foods
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CATEGORY_ID = "categoryId";

    // Cột bảng Cart
    public static final String KEY_FOOD_ID = "foodId";
    public static final String KEY_FOOD_NAME = "foodName";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_CUSTOMIZATION = "customization";

    // Cột bảng Orders
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_ORDER_DATE = "orderDate";
    public static final String KEY_TOTAL_PRICE = "totalPrice";
    public static final String KEY_STATUS = "status";

    // Cột bảng OrderItems
    public static final String KEY_ORDER_ID = "orderId";


    public PizzaAppDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng Users
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EMAIL + " TEXT UNIQUE NOT NULL,"
                + KEY_PASSWORD + " TEXT NOT NULL,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_PHONE + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // 2. Tạo bảng Categories
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT NOT NULL,"
                + KEY_IMAGE + " TEXT" + ")"; // 'image' sẽ lưu tên ảnh trong drawable, vd: "cat_pizza"
        db.execSQL(CREATE_CATEGORIES_TABLE);

        // 3. Tạo bảng Foods
        String CREATE_FOODS_TABLE = "CREATE TABLE " + TABLE_FOODS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT NOT NULL,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_PRICE + " REAL NOT NULL,"
                + KEY_IMAGE + " TEXT,"
                + KEY_CATEGORY_ID + " INTEGER,"
                + " FOREIGN KEY (" + KEY_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + KEY_ID + ")" + ")";
        db.execSQL(CREATE_FOODS_TABLE);

        // 4. Tạo bảng Cart (Giỏ hàng)
        // Dùng foodId làm PK, nếu muốn tùy chỉnh (vd: nửa-nửa) thì cần PK phức tạp hơn
        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
                + KEY_FOOD_ID + " INTEGER PRIMARY KEY,"
                + KEY_FOOD_NAME + " TEXT,"
                + KEY_PRICE + " REAL,"
                + KEY_QUANTITY + " INTEGER,"
                + KEY_IMAGE + " TEXT,"
                + KEY_CUSTOMIZATION + " TEXT" + ")"; // Lưu tùy chỉnh, vd: "Nửa Beef, Nửa Curry"
        db.execSQL(CREATE_CART_TABLE);

        // 5. Tạo bảng Orders (Đơn hàng)
        String CREATE_ORDERS_TABLE = "CREATE TABLE " + TABLE_ORDERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_ID + " INTEGER," // ID của người dùng đặt hàng
                + KEY_ORDER_DATE + " TEXT," // Dùng DateTimeHelper
                + KEY_TOTAL_PRICE + " REAL,"
                + KEY_STATUS + " TEXT," // Vd: "Đang xử lý", "Đã giao"
                + " FOREIGN KEY (" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + ")" + ")";
        db.execSQL(CREATE_ORDERS_TABLE);

        // 6. Tạo bảng OrderItems (Các món trong đơn hàng)
        String CREATE_ORDER_ITEMS_TABLE = "CREATE TABLE " + TABLE_ORDER_ITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_ORDER_ID + " INTEGER NOT NULL," // Khóa ngoại tới Orders
                + KEY_FOOD_ID + " INTEGER NOT NULL," // ID của món ăn
                + KEY_FOOD_NAME + " TEXT," // Lưu lại tên, phòng trường hợp menu thay đổi
                + KEY_QUANTITY + " INTEGER,"
                + KEY_PRICE + " REAL," // Giá tại thời điểm đặt
                + " FOREIGN KEY (" + KEY_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + KEY_ID + ")" + ")";
        db.execSQL(CREATE_ORDER_ITEMS_TABLE);

        // Chèn dữ liệu mẫu
        insertMockData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOODS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Hàm chèn dữ liệu mẫu để test
    private void insertMockData(SQLiteDatabase db) {
        // 1. Chèn Categories
        ContentValues cat1 = new ContentValues();
        cat1.put(KEY_NAME, "Pizza");
        cat1.put(KEY_IMAGE, "cat_pizza"); // Tên file ảnh trong res/drawable
        long cat1Id = db.insert(TABLE_CATEGORIES, null, cat1);

        ContentValues cat2 = new ContentValues();
        cat2.put(KEY_NAME, "Appetizer");
        cat2.put(KEY_IMAGE, "cat_appetizer");
        long cat2Id = db.insert(TABLE_CATEGORIES, null, cat2);



        // 2. Chèn Foods (Pizza)
        ContentValues food1 = new ContentValues();
        food1.put(KEY_NAME, "Spicy Beef Kebab Pizza");
        food1.put(KEY_DESCRIPTION, "Spicy marinated beef, tomato sauce, green chilies, cumin seeds");
        food1.put(KEY_PRICE, 294000.0);
        food1.put(KEY_IMAGE, "img_beef_kebab"); // Tên file ảnh
        food1.put(KEY_CATEGORY_ID, cat1Id);
        db.insert(TABLE_FOODS, null, food1);

        ContentValues food2 = new ContentValues();
        food2.put(KEY_NAME, "Hokkaido Scallops Sweet Miso Gratin");
        food2.put(KEY_DESCRIPTION, "Hokkaido scallops, sweet miso gratin sauce, mozzarella");
        food2.put(KEY_PRICE, 398000.0);
        food2.put(KEY_IMAGE, "img_hokkaido_scallop");
        food2.put(KEY_CATEGORY_ID, cat1Id);
        db.insert(TABLE_FOODS, null, food2);

        // 3. Chèn Foods (Appetizer)
        ContentValues food3 = new ContentValues();
        food3.put(KEY_NAME, "House-made Mozzarella");
        food3.put(KEY_DESCRIPTION, "Our original house-made Mozzarella cheese");
        food3.put(KEY_PRICE, 45000.0);
        food3.put(KEY_IMAGE, "img_mozzarella");
        food3.put(KEY_CATEGORY_ID, cat2Id);
        db.insert(TABLE_FOODS, null, food3);
    }
}