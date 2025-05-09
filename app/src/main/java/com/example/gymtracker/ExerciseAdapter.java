package com.example.gymtracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private final ArrayList<Exercise> exerciseList;
    private final OnItemClickListener listener;
    private final boolean isEditable;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ExerciseAdapter(ArrayList<Exercise> exerciseList, OnItemClickListener listener, boolean isEditable) {
        this.exerciseList = exerciseList;
        this.listener = listener;
        this.isEditable = isEditable;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        holder.exerciseNameTextView.setText(exercise.getName());

        // Inicjalizacja SeriesAdapter
        SeriesAdapter seriesAdapter = new SeriesAdapter(
                exercise.getSeriesList(),
                isEditable,
                seriesPosition -> {
                    exercise.removeSeries(seriesPosition);
                    notifyItemChanged(position);
                    // Opcjonalnie: usuń z bazy danych
                    long dayId = ((TrainingSetupActivity) holder.itemView.getContext()).getDayId();
                    if (dayId != -1) {
                        DatabaseHelper dbHelper = new DatabaseHelper(holder.itemView.getContext());
                        dbHelper.deleteDayExercise(dayId, exercise.getName(), seriesPosition);
                    }
                }
        );
        holder.seriesRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.seriesRecyclerView.setAdapter(seriesAdapter);

        holder.addSeriesButton.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        holder.removeExerciseButton.setVisibility(isEditable ? View.VISIBLE : View.GONE);

        if (isEditable) {
            holder.addSeriesButton.setOnClickListener(v -> {
                exercise.addSeries(new Series(0, 0));
                notifyItemChanged(position);
            });
            holder.removeExerciseButton.setOnClickListener(v -> listener.onItemClick(position));
        } else {
            holder.addSeriesButton.setOnClickListener(null);
            holder.removeExerciseButton.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseNameTextView;
        Button addSeriesButton;
        RecyclerView seriesRecyclerView;
        Button removeExerciseButton;

        ViewHolder(View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exerciseNameTextView);
            addSeriesButton = itemView.findViewById(R.id.addSeriesButton);
            seriesRecyclerView = itemView.findViewById(R.id.seriesRecyclerView);
            removeExerciseButton = itemView.findViewById(R.id.removeExerciseButton);
        }
    }
}