package com.example.gymtracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;

public class WeekDaysAdapter extends RecyclerView.Adapter<WeekDaysAdapter.ViewHolder> {
    private static final String[] DAYS = {"Pon", "Wt", "Śr", "Czw", "Pt", "Sob", "Niedz"};
    public static final String[] FULL_DAYS = {"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"};
    private final OnDayClickListener listener;
    private final int todayDeviceActualIndex;
    private int selectedUserDayIndex = -1;

    public interface OnDayClickListener {
        void onDayClick(String dayName);
    }

    public WeekDaysAdapter(OnDayClickListener listener) {
        this.listener = listener;
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        todayDeviceActualIndex = getOurIndexFromCalendarField(dayOfWeek);
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

        if (realPosition == todayDeviceActualIndex) {
            holder.dayTextView.setBackgroundResource(R.drawable.calendar_selected_background);
        } else if (realPosition == selectedUserDayIndex) {
            holder.dayTextView.setBackgroundResource(R.drawable.calendar_selected_grey_background);
        } else {
            holder.dayTextView.setBackgroundResource(0);
        }

        holder.itemView.setOnClickListener(v -> {
            listener.onDayClick(FULL_DAYS[realPosition]);
        });
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public String getFullDayName(int index) {
        if (index >= 0 && index < FULL_DAYS.length) {
            return FULL_DAYS[index];
        }
        return "";
    }

    public int getIndexForDayName(String dayName) {
        for (int i = 0; i < FULL_DAYS.length; i++) {
            if (FULL_DAYS[i].equalsIgnoreCase(dayName)) {
                return i;
            }
        }
        return -1;
    }

    public void setSelectedUserDay(String dayName) {
        int newSelectedDayIndex = getIndexForDayName(dayName);
        if (this.selectedUserDayIndex != newSelectedDayIndex) {
            this.selectedUserDayIndex = newSelectedDayIndex;
            notifyDataSetChanged();
        }
    }

    public void clearSelectedUserDay() {
        if (this.selectedUserDayIndex != -1) {
            this.selectedUserDayIndex = -1;
            notifyDataSetChanged();
        }
    }

    public static int getOurIndexFromCalendarField(int calendarApiDayOfWeek) {
        if (calendarApiDayOfWeek == Calendar.SUNDAY) {
            return 6;
        } else {
            return calendarApiDayOfWeek - Calendar.MONDAY;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;

        ViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
        }
    }
}