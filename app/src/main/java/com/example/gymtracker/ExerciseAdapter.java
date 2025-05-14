package com.example.gymtracker;

import android.util.Log;
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
                isEditable, // Przekazanie isEditable do SeriesAdapter, aby kontrolować edytowalność EditText
                seriesPosition -> {
                    // Usuwanie serii
                    exercise.removeSeries(seriesPosition);
                    notifyItemChanged(position);
                    Log.d("DEBUG_SERIES", "Usunięto serię na pozycji: " + seriesPosition + " z ćwiczenia: " + exercise.getName());
                    // Usuwanie serii z bazy danych w trybie planowania
                    if (holder.itemView.getContext() instanceof TrainingSetupRegisterActivity) {
                        long dayId = ((TrainingSetupRegisterActivity) holder.itemView.getContext()).getIntent().getLongExtra("DAY_ID", -1);
                        if (dayId != -1) {
                            DatabaseHelper dbHelper = new DatabaseHelper(holder.itemView.getContext());
                            dbHelper.deleteDayExercise(dayId, exercise.getName(), seriesPosition);
                        }
                    }
                }
        );
        holder.seriesRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.seriesRecyclerView.setAdapter(seriesAdapter);

        // Przyciski zawsze widoczne dla usuwania i dodawania serii oraz ćwiczeń
        holder.addSeriesButton.setVisibility(View.VISIBLE);
        holder.removeExerciseButton.setVisibility(View.VISIBLE);

        holder.addSeriesButton.setOnClickListener(v -> {
            // Domyślne wartości serii (możesz dostosować)
            exercise.addSeries(new Series(0, 0)); // Ustawiam domyślne reps=0, weight=0
            notifyItemChanged(position);
            Log.d("DEBUG_SERIES", "Dodano serię do ćwiczenia: " + exercise.getName() + ", Nowe serie: " + exercise.getSeriesList().size());
        });

        holder.removeExerciseButton.setOnClickListener(v -> {
            listener.onItemClick(position);
        });
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