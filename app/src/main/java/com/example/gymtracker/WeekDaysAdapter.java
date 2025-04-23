package com.example.gymtracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar; // Dodano import

public class WeekDaysAdapter extends RecyclerView.Adapter<WeekDaysAdapter.ViewHolder> {
    private static final String[] DAYS = {"Pon", "Wt", "Śr", "Czw", "Pt", "Sob", "Niedz"};
    private static final String[] FULL_DAYS = {"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"};
    private final OnDayClickListener listener;
    private final int currentDayIndex; // Indeks aktualnego dnia tygodnia (0 = Poniedziałek, ..., 6 = Niedziela)
    private int selectedDayIndex = -1;

    public interface OnDayClickListener {
        void onDayClick(String dayName);
    }

    public WeekDaysAdapter(OnDayClickListener listener) {
        this.listener = listener;
        // Pobierz aktualny dzień tygodnia
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 = Niedziela, 2 = Poniedziałek, ..., 7 = Sobota
        currentDayIndex = (dayOfWeek + 5) % 7; // Przesunięcie: Poniedziałek = 0, ..., Niedziela = 6
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

        // Zaznacz na zielono, jeśli pozycja odpowiada aktualnemu dniowi tygodnia
        // Tło: priorytet dzisiejszy > kliknięty
        if (realPosition == currentDayIndex) {
            holder.dayTextView.setBackgroundResource(R.drawable.calendar_selected_background); // zielony
        } else if (realPosition == selectedDayIndex) {
            holder.dayTextView.setBackgroundResource(R.drawable.calendar_selected_grey_background); // szary
        } else {
            holder.dayTextView.setBackgroundResource(0); // brak tła
        }

        holder.itemView.setOnClickListener(v -> {
            selectedDayIndex = realPosition;
            listener.onDayClick(FULL_DAYS[realPosition]);
            notifyDataSetChanged(); // odśwież wszystkie elementy
        });
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // Symuluje nieskończoną listę
    }

    public String getFullDayName(int index) {
        return FULL_DAYS[index];
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;

        ViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
        }
    }
}