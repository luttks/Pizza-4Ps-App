package com.example.pizzaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pizzaapp.R;
import com.example.pizzaapp.model.Cart;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private List<Cart> list;

    public OrderDetailAdapter(List<Cart> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tái sử dụng layout cart item nhưng ẩn các nút đi
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cart item = list.get(position);
        holder.tvName.setText(item.getFoodName());

        // Format giá tiền
        holder.tvPrice.setText(String.format("%,.0fđ", item.getPrice()));

        holder.tvQuantity.setText("x" + item.getQuantity());

        // Ẩn các nút không cần thiết của Cart layout (Nút + , - , Xóa)
        holder.btnMinus.setVisibility(View.GONE);
        holder.btnPlus.setVisibility(View.GONE);
        holder.btnDelete.setVisibility(View.GONE);

        // Setup ảnh (tạm thời để ảnh mặc định nếu chưa xử lý load ảnh động)
        // Nếu muốn load ảnh thật, bạn cần dùng getIdentifier giống trong CartAdapter
        holder.ivImage.setImageResource(R.drawable.ic_launcher_background);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        View btnMinus, btnPlus, btnDelete; // Khai báo View chung để ẩn nút
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_cart_name);
            tvPrice = itemView.findViewById(R.id.tv_cart_price);
            tvQuantity = itemView.findViewById(R.id.tv_cart_quantity);
            btnMinus = itemView.findViewById(R.id.btn_cart_minus);
            btnPlus = itemView.findViewById(R.id.btn_cart_plus);
            btnDelete = itemView.findViewById(R.id.btn_cart_delete);
            ivImage = itemView.findViewById(R.id.iv_cart_image);
        }
    }
}