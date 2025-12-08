package com.example.pizzaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pizzaapp.R;
import com.example.pizzaapp.model.Order;
import com.example.pizzaapp.model.OrderItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(long orderId);
        void onCancelOrder(long orderId, int position);
    }

    public HistoryAdapter(Context context, List<Order> orderList, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_order, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText("Order #" + order.getId());
        holder.tvOrderDate.setText(order.getDate());
        holder.tvOrderStatus.setText(order.getStatus());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvOrderTotal.setText(formatter.format(order.getTotalPrice()));

        // --- LOGIC LOAD ẢNH ---
        // Lấy ảnh của món đầu tiên trong list items để hiển thị
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            OrderItem firstItem = order.getItems().get(0);
            String imageUrl = firstItem.getFoodImage();

            if (imageUrl != null && imageUrl.contains("localhost")) {
                imageUrl = imageUrl.replace("localhost", "10.0.2.2");
            }

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.ivOrderThumb);
        } else {
            holder.ivOrderThumb.setImageResource(R.drawable.ic_launcher_background);
        }
        // ----------------------

        // Chỉ hiện nút hủy khi Status là "Processing"
        if ("Processing".equals(order.getStatus())) {
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> listener.onCancelOrder(order.getId(), position));
        } else {
            holder.btnCancel.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order.getId()));
    }

    @Override
    public int getItemCount() {
        if(orderList != null) return orderList.size();
        return 0;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderTotal;
        ImageView btnCancel, ivOrderThumb; // Thêm ivOrderThumb

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            btnCancel = itemView.findViewById(R.id.btn_cancel_order);
            ivOrderThumb = itemView.findViewById(R.id.iv_order_thumb); // Ánh xạ
        }
    }
}