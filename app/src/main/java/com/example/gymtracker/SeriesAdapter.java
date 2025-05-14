package com.example.gymtracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.ViewHolder> {
    private final ArrayList<Series> seriesList;
    private final boolean isEditable;
    private final OnSeriesRemoveListener removeListener;

    public interface OnSeriesRemoveListener {
        void onSeriesRemove(int position);
    }

    public SeriesAdapter(ArrayList<Series> seriesList, boolean isEditable, OnSeriesRemoveListener removeListener) {
        this.seriesList = seriesList;
        this.isEditable = isEditable;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.series_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Series series = seriesList.get(position);

        // Ustawienie wartości w EditText
        holder.repsEditText.setText(String.valueOf(series.getReps()));
        holder.weightEditText.setText(String.valueOf(series.getWeight()));

        // Blokowanie edycji EditText w trybie rejestracji
        holder.repsEditText.setEnabled(isEditable);
        holder.weightEditText.setEnabled(isEditable);

        // Przycisk usuwania serii zawsze widoczny
        holder.removeSeriesButton.setVisibility(View.VISIBLE);
        holder.removeSeriesButton.setOnClickListener(v -> {
            removeListener.onSeriesRemove(position);
        });
    }

    @Override
    public int getItemCount() {
        return seriesList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText repsEditText;
        EditText weightEditText;
        Button removeSeriesButton;

        ViewHolder(View itemView) {
            super(itemView);
            repsEditText = itemView.findViewById(R.id.repsEditText);
            weightEditText = itemView.findViewById(R.id.weightEditText);
            removeSeriesButton = itemView.findViewById(R.id.removeSeriesButton);
        }
    }
}