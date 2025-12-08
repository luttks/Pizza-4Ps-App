package com.example.pizzaapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pizzaapp.R;
import com.example.pizzaapp.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    // Biến để lưu vị trí đang được chọn (để đổi màu cho đẹp - tuỳ chọn)
    private int selectedPosition = -1;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Category> newList) {
        this.categoryList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());

        // 1. Xử lý hiển thị ảnh
        String imageUrl = category.getImage();
        if (imageUrl != null && imageUrl.contains("localhost")) {
            imageUrl = imageUrl.replace("localhost", "10.0.2.2");
        }

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.ivCategoryImage);

        // 2. Xử lý hiệu ứng chọn (Optional: Đổi màu nền khi được chọn)
        if (selectedPosition == position) {
            holder.categoryLayout.setBackgroundColor(Color.parseColor("#e0f7fa")); // Màu xanh nhạt khi chọn
        } else {
            holder.categoryLayout.setBackgroundColor(Color.TRANSPARENT); // Màu mặc định
        }

        // 3. --- QUAN TRỌNG: SỰ KIỆN CLICK ---
        // Đây là phần bạn bị thiếu
        holder.categoryLayout.setOnClickListener(v -> {
            // Cập nhật vị trí được chọn để đổi màu
            selectedPosition = position;
            notifyDataSetChanged(); // Load lại giao diện để cập nhật màu sắc

            // Gọi interface để HomeFragment biết
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (categoryList != null) {
            return categoryList.size();
        }
        return 0;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryImage;
        TextView tvCategoryName;
        LinearLayout categoryLayout;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Đảm bảo ID này khớp với file layout_item_category.xml
            ivCategoryImage = itemView.findViewById(R.id.iv_category_image);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            categoryLayout = itemView.findViewById(R.id.category_layout);
        }
    }
}