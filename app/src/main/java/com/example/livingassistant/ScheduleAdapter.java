package com.example.livingassistant;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<ScheduleItem> scheduleList;
    private OnDeleteClickListener onDeleteClickListener;

    public static final int REQUEST_CODE_DETAIL = 1;  // 声明请求码

    public ScheduleAdapter(List<ScheduleItem> scheduleList, OnDeleteClickListener listener) {
        this.scheduleList = scheduleList;
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        // 获取当前的日程项
        ScheduleItem item = scheduleList.get(position);

        // 设置标题和事件到视图组件
        holder.scheduleTitle.setText(item.getTitle());
        holder.scheduleEvent.setText(item.getEvent());
//        holder.scheduleTime.setText(item.getTime());

        // 设置卡片点击事件
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            // 传递 ID 和其他数据
            intent.putExtra("EXTRA_ID", item.getId());
            intent.putExtra("EXTRA_TITLE", item.getTitle());
            intent.putExtra("EXTRA_EVENT", item.getEvent());
            intent.putExtra("EXTRA_TIME", item.getTime());
            // 启动 DetailActivity，传递请求码
            ((AppCompatActivity) v.getContext()).startActivityForResult(intent, REQUEST_CODE_DETAIL);
        });

        // 设置删除按钮的点击事件
        holder.deleteButton.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView scheduleTitle;
        TextView scheduleEvent;
        TextView scheduleTime;
        Button deleteButton;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            scheduleTitle = itemView.findViewById(R.id.schedule_title);
            scheduleEvent = itemView.findViewById(R.id.schedule_event);
//            scheduleTime = itemView.findViewById(R.id.schedule_time);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(ScheduleItem item);
    }
}
