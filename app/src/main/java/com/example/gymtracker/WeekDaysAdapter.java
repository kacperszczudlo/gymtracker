package com.example.gymtracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeekDaysAdapter extends RecyclerView.Adapter<WeekDaysAdapter.ViewHolder> {
    private static final String[] DAYS = {"Pon", "Wt", "Śr", "Czw", "Pt", "Sob", "Niedz"};
    private static final String[] FULL_DAYS = {"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"};
    private int selectedPosition = 0;
    private final OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(String dayName);
    }

    public WeekDaysAdapter(OnDayClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_week_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int realPosition = position % DAYS.length;
        String day = DAYS[realPosition];
        holder.dayTextView.setText(day);
        holder.dayTextView.setBackgroundResource(
                position == selectedPosition ? R.drawable.calendar_selected_background : 0
        );
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            listener.onDayClick(FULL_DAYS[realPosition]);
        });
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // Symuluje nieskończoną listę
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(selectedPosition);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;

        ViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
        }
    }
}