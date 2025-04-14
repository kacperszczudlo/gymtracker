package com.example.gymtracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymtracker.R;
import java.util.ArrayList;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private ArrayList<Exercise> exerciseList;
    private OnRemoveClickListener listener;

    public interface OnRemoveClickListener {
        void onRemoveClick(int position);
    }

    public ExerciseAdapter(ArrayList<Exercise> exerciseList, OnRemoveClickListener listener) {
        this.exerciseList = exerciseList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        holder.exerciseNameTextView.setText(exercise.getName());
        holder.setsEditText.setText(String.valueOf(exercise.getSets()));
        holder.repsEditText.setText(String.valueOf(exercise.getReps()));
        holder.weightEditText.setText(String.valueOf(exercise.getWeight()));

        holder.removeButton.setOnClickListener(v -> listener.onRemoveClick(position));
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseNameTextView;
        EditText setsEditText, repsEditText, weightEditText;
        Button removeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.exerciseNameTextView);
            setsEditText = itemView.findViewById(R.id.setsEditText);
            repsEditText = itemView.findViewById(R.id.repsEditText);
            weightEditText = itemView.findViewById(R.id.weightEditText);
            removeButton = itemView.findViewById(R.id.removeExerciseButton);
        }
    }
}