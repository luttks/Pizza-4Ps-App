package com.example.pizzaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pizzaapp.R;
import com.example.pizzaapp.model.Order;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnOrderClickListener listener; // 1. Khai báo Listener

    // 2. Tạo Interface để Fragment lắng nghe
    public interface OnOrderClickListener {
        void onOrderClick(long orderId);
        void onCancelOrder(long orderId, int position);
    }

    // 3. Cập nhật Constructor để nhận Listener (Fix lỗi constructor)
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

        // Logic ẩn hiện nút Hủy
        if ("Processing".equals(order.getStatus())) {
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else {
            holder.btnCancel.setVisibility(View.GONE);
        }

        // Bắt sự kiện bấm nút Hủy
        holder.btnCancel.setOnClickListener(v -> {
            listener.onCancelOrder(order.getId(), position);
        });

        // Bắt sự kiện bấm vào cả dòng (Xem chi tiết)
        holder.itemView.setOnClickListener(v -> {
            listener.onOrderClick(order.getId());
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderTotal;
        Button btnCancel;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            btnCancel = itemView.findViewById(R.id.btn_cancel_order); // Nút hủy (đảm bảo đã thêm trong layout xml)
        }
    }
}