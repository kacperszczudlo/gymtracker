package com.example.gymtracker;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
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
        holder.setsEditText.setText(exercise.getSets() > 0 ? String.valueOf(exercise.getSets()) : "");
        holder.repsEditText.setText(exercise.getReps() > 0 ? String.valueOf(exercise.getReps()) : "");
        holder.weightEditText.setText(exercise.getWeight() > 0 ? String.valueOf(exercise.getWeight()) : "");

        // Ustaw pola jako edytowalne lub tylko do odczytu
        holder.setsEditText.setEnabled(isEditable);
        holder.repsEditText.setEnabled(isEditable);
        holder.weightEditText.setEnabled(isEditable);
        holder.removeExerciseButton.setVisibility(isEditable ? View.VISIBLE : View.GONE);

        // Usuń poprzednie listenery, aby uniknąć duplikatów
        holder.setsEditText.setTag(position);
        holder.repsEditText.setTag(position);
        holder.weightEditText.setTag(position);

        // Nasłuchuj zmian w polach EditText tylko, jeśli są edytowalne
        if (isEditable) {
            holder.setsEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (holder.getAdapterPosition() == (int) holder.setsEditText.getTag()) {
                        try {
                            int sets = s.length() > 0 ? Integer.parseInt(s.toString()) : 0;
                            exercise.setSets(sets);
                        } catch (NumberFormatException e) {
                            exercise.setSets(0);
                        }
                    }
                }
            });

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
                            exercise.setReps(reps);
                        } catch (NumberFormatException e) {
                            exercise.setReps(0);
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
                            exercise.setWeight(weight);
                        } catch (NumberFormatException e) {
                            exercise.setWeight(0f);
                        }
                    }
                }
            });
        } else {
            // Wyczyść listenery, jeśli pola nie są edytowalne
            holder.setsEditText.setOnTouchListener(null);
            holder.repsEditText.setOnTouchListener(null);
            holder.weightEditText.setOnTouchListener(null);
        }

        holder.removeExerciseButton.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseNameTextView;
        EditText setsEditText;
        EditText repsEditText;
        EditText weightEditText;
        Button removeExerciseButton;

        ViewHolder(View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exerciseNameTextView);
            setsEditText = itemView.findViewById(R.id.setsEditText);
            repsEditText = itemView.findViewById(R.id.repsEditText);
            weightEditText = itemView.findViewById(R.id.weightEditText);
            removeExerciseButton = itemView.findViewById(R.id.removeExerciseButton);
        }
    }
}