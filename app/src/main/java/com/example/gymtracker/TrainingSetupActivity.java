package com.example.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TrainingSetupActivity extends AppCompatActivity {

    private TextView trainingTitleTextView;
    private EditText newExerciseEditText;
    private Button addExerciseButton;
    private Button nextButton;
    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exerciseList;
    private DatabaseHelper dbHelper;
    private long dayId;
    private String dayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_setup);

        trainingTitleTextView = findViewById(R.id.trainingTitleTextView);
        newExerciseEditText = findViewById(R.id.newExerciseEditText);
        addExerciseButton = findViewById(R.id.addExerciseButton);
        nextButton = findViewById(R.id.nextButton);
        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView);
        dbHelper = new DatabaseHelper(this);

        // Pobierz ID i nazwę dnia z Intent
        dayId = getIntent().getLongExtra("DAY_ID", -1);
        dayName = getIntent().getStringExtra("DAY_NAME");

        // Jeśli nie ma ID, spróbuj pobrać je na podstawie nazwy dnia
        if (dayId == -1 && dayName != null) {
            dayId = dbHelper.getTrainingDayId(dayName);
        }

        // Ustaw domyślną nazwę, jeśli nie ma dayName
        if (dayName == null) {
            dayName = "Trening " + (dayId != -1 ? dayId : "Nowy");
        }

        // Ustaw tytuł
        trainingTitleTextView.setText(dayName);

        // Inicjalizacja listy ćwiczeń
        exerciseList = new ArrayList<>();
        loadExercises();

        // Ustaw adapter dla RecyclerView
        exerciseAdapter = new ExerciseAdapter(exerciseList);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseRecyclerView.setAdapter(exerciseAdapter);

        // Obsługa przycisku "Dodaj ćwiczenie"
        addExerciseButton.setOnClickListener(v -> {
            String exerciseName = newExerciseEditText.getText().toString().trim();
            if (!exerciseName.isEmpty()) {
                // Dodaj nowe ćwiczenie z domyślnymi wartościami
                Exercise newExercise = new Exercise(exerciseName, 0, 0); //
                exerciseList.add(newExercise);
                exerciseAdapter.notifyItemInserted(exerciseList.size() - 1);

                // Zapisz ćwiczenie do bazy danych
                ArrayList<String> exercisesToSave = new ArrayList<>();
                exercisesToSave.add(exerciseName);
                boolean saved = dbHelper.saveTrainingDay(dayName, exercisesToSave);
                if (saved && dayId == -1) {
                    // Zaktualizuj dayId po zapisaniu nowego dnia
                    dayId = dbHelper.getTrainingDayId(dayName);
                }

                // Wyczyść pole tekstowe
                newExerciseEditText.setText("");
            }
        });

        // Obsługa przycisku "Dalej"
        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(TrainingSetupActivity.this, TrainingMainActivity.class);
            intent.putExtra("DAY_ID", dayId);
            intent.putExtra("DAY_NAME", dayName);
            startActivity(intent);
        });
    }

    private void loadExercises() {
        exerciseList.clear();
        if (dayName != null) {
            List<Exercise> exercises = dbHelper.getExercisesForDay(dayName);
            exerciseList.addAll(exercises);
        }
    }

    // Adapter dla ćwiczeń
    private class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
        private List<Exercise> exercises;

        public ExerciseAdapter(List<Exercise> exercises) {
            this.exercises = exercises;
        }

        @NonNull
        @Override
        public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
            return new ExerciseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
            Exercise exercise = exercises.get(position);
            holder.exerciseNameTextView.setText(exercise.getName());
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }

        class ExerciseViewHolder extends RecyclerView.ViewHolder {
            TextView exerciseNameTextView;

            public ExerciseViewHolder(@NonNull View itemView) {
                super(itemView);
                exerciseNameTextView = itemView.findViewById(R.id.exerciseNameTextView);
            }
        }
    }
}