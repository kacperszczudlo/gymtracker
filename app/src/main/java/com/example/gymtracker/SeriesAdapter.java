package com.example.gymtracker;

import android.text.Editable;
import android.text.TextWatcher;
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
        holder.repsEditText.setText(series.getReps() > 0 ? String.valueOf(series.getReps()) : "");
        holder.weightEditText.setText(series.getWeight() > 0 ? String.valueOf(series.getWeight()) : "");

        holder.repsEditText.setEnabled(isEditable);
        holder.weightEditText.setEnabled(isEditable);
        holder.removeSeriesButton.setVisibility(isEditable ? View.VISIBLE : View.GONE);

        holder.repsEditText.setTag(position);
        holder.weightEditText.setTag(position);

        if (isEditable) {
            holder.repsEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (holder.getAdapterPosition() == (int) holder.repsEditText.getTag()) {
                        try {
                            int reps = s.length() > 0 ? Integer.parseInt(s.toString()) : 0;
                            series.setReps(reps);
                        } catch (NumberFormatException e) {
                            series.setReps(0);
                        }
                    }
                }
            });

            holder.weightEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (holder.getAdapterPosition() == (int) holder.weightEditText.getTag()) {
                        try {
                            float weight = s.length() > 0 ? Float.parseFloat(s.toString()) : 0f;
                            series.setWeight(weight);
                        } catch (NumberFormatException e) {
                            series.setWeight(0f);
                        }
                    }
                }
            });

            holder.removeSeriesButton.setOnClickListener(v -> removeListener.onSeriesRemove(position));
        } else {
            holder.repsEditText.setOnTouchListener(null);
            holder.weightEditText.setOnTouchListener(null);
            holder.removeSeriesButton.setOnClickListener(null);
        }
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