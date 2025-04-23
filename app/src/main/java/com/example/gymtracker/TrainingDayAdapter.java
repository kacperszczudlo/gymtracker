package com.example.gymtracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TrainingDayAdapter extends RecyclerView.Adapter<TrainingDayAdapter.ViewHolder> {
    private final ArrayList<String> dayList;
    private final OnItemClickListener clickListener;
    private final OnItemClickListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public TrainingDayAdapter(ArrayList<String> dayList, OnItemClickListener clickListener, OnItemClickListener deleteListener) {
        this.dayList = dayList;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.training_day_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String dayName = dayList.get(position);
        holder.dayNameTextView.setText(dayName);

        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(position));
        holder.deleteButton.setOnClickListener(v -> deleteListener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayNameTextView;
        Button deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            dayNameTextView = itemView.findViewById(R.id.dayNameTextView);
            deleteButton = itemView.findViewById(R.id.deleteDayButton);
        }
    }
}