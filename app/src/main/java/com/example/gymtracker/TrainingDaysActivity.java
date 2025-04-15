package com.example.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TrainingDaysActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TrainingDayAdapter adapter;
    private ArrayList<String> dayList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_days);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.trainingDaysRecyclerView);
        Button addDayButton = findViewById(R.id.addDayButton);

        dayList = new ArrayList<>();
        loadTrainingDays();

        adapter = new TrainingDayAdapter(dayList, position -> {
            String dayName = dayList.get(position);
            long dayId = dbHelper.getTrainingDayId(1, dayName); // Assuming userId = 1
            if (dayId != -1 && dbHelper.getDayExercises(dayId).size() == 0) {
                Intent intent = new Intent(TrainingDaysActivity.this, TrainingSetupActivity.class);
                intent.putExtra("DAY_NAME", dayName);
                intent.putExtra("DAY_ID", dayId);
                startActivity(intent);
            } else {
                Intent intent = new Intent(TrainingDaysActivity.this, TrainingMainActivity.class);
                intent.putExtra("DAY_NAME", dayName);
                intent.putExtra("DAY_ID", dayId);
                startActivity(intent);
            }
        }, position -> {
            String dayName = dayList.get(position);
            long dayId = dbHelper.getTrainingDayId(1, dayName);
            if (dayId != -1) {
                dbHelper.deleteTrainingDay(dayId);
                dayList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addDayButton.setOnClickListener(v -> {
            String newDay = "Dzień " + (dayList.size() + 1);
            long dayId = dbHelper.saveTrainingDay(1, newDay); // Assuming userId = 1
            if (dayId != -1) {
                dayList.add(newDay);
                adapter.notifyItemInserted(dayList.size() - 1);
            }
        });
    }

    private void loadTrainingDays() {
        // Example static days; replace with database query if needed
        String[] days = {"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek"};
        for (String day : days) {
            long dayId = dbHelper.getTrainingDayId(1, day);
            if (dayId == -1) {
                dbHelper.saveTrainingDay(1, day);
            }
            dayList.add(day);
        }
    }
}