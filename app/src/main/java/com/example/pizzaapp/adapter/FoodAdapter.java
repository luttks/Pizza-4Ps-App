package com.example.pizzaapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pizzaapp.R;
//import com.example.pizzaapp.helper.CartManager;
import com.example.pizzaapp.model.Food;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.bumptech.glide.Glide;
import com.example.pizzaapp.model.OrderItem;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context context;
    private List<Food> foodList;
    private OnFoodClickListener listener;

    // Interface để xử lý click
    public interface OnFoodClickListener {
        void onFoodClick(Food food);
    }

    public FoodAdapter(Context context, List<Food> foodList, OnFoodClickListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
    }

    // Hàm này để cập nhật danh sách khi đổi category
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Food> newList) {
        this.foodList.clear();
        this.foodList.addAll(newList);
        notifyDataSetChanged(); // Báo cho adapter vẽ lại
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);

        holder.tvFoodName.setText(food.getName());
        holder.tvFoodDescription.setText(food.getDescription());

        // Format tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvFoodPrice.setText(formatter.format(food.getPrice()));

        // --- XỬ LÝ ẢNH TỪ SERVER---
        String imageUrl = food.getImage();

        if (imageUrl.contains("localhost")) {
            imageUrl = imageUrl.replace("localhost", "10.0.2.2");
        }

        // Dùng Glide để tải ảnh từ URL vào ImageView
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background) // Ảnh chờ khi đang tải
                .error(R.drawable.ic_launcher_background) // Ảnh lỗi nếu không tải được
                .into(holder.ivFoodImage);

        // --- Xử lý sự kiện click ---
        holder.foodLayout.setOnClickListener(v -> {
            listener.onFoodClick(food);
        });

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvFoodName, tvFoodDescription, tvFoodPrice;
        CardView foodLayout;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.iv_food_image);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvFoodDescription = itemView.findViewById(R.id.tv_food_description);
            tvFoodPrice = itemView.findViewById(R.id.tv_food_price);
            foodLayout = itemView.findViewById(R.id.food_layout);
        }
    }
}
