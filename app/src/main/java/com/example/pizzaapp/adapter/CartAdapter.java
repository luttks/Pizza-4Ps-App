package com.example.pizzaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pizzaapp.R;
import com.example.pizzaapp.model.Cart;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Cart> cartList;
    private OnCartChangeListener listener;

    // Interface để Activity lắng nghe sự thay đổi
    public interface OnCartChangeListener {
        void onQuantityChanged(Cart item, int newQuantity);
        void onItemDeleted(Cart item);
    }

    public CartAdapter(Context context, List<Cart> cartList, OnCartChangeListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart item = cartList.get(position);

        holder.tvName.setText(item.getFoodName());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Format giá tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(formatter.format(item.getPrice()));

        // Load ảnh
        int imageId = context.getResources().getIdentifier(item.getImage(), "drawable", context.getPackageName());
        if (imageId != 0) {
            holder.ivImage.setImageResource(imageId);
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_launcher_background);
        }

        // Sự kiện nút Trừ
        holder.btnMinus.setOnClickListener(v -> {
            int currentQty = item.getQuantity();
            if (currentQty > 1) {
                listener.onQuantityChanged(item, currentQty - 1);
            }
        });

        // Sự kiện nút Cộng
        holder.btnPlus.setOnClickListener(v -> {
            listener.onQuantityChanged(item, item.getQuantity() + 1);
        });

        // Sự kiện nút Xóa
        holder.btnDelete.setOnClickListener(v -> {
            listener.onItemDeleted(item);
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvQuantity;
        Button btnMinus, btnPlus;
        ImageButton btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_cart_image);
            tvName = itemView.findViewById(R.id.tv_cart_name);
            tvPrice = itemView.findViewById(R.id.tv_cart_price);
            tvQuantity = itemView.findViewById(R.id.tv_cart_quantity);
            btnMinus = itemView.findViewById(R.id.btn_cart_minus);
            btnPlus = itemView.findViewById(R.id.btn_cart_plus);
            btnDelete = itemView.findViewById(R.id.btn_cart_delete);
        }
    }
}
