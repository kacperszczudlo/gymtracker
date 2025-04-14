package com.example.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtracker.R;
import java.util.HashSet;
import java.util.Set;

public class TrainingDaysActivity extends AppCompatActivity {
    private Set<String> selectedDays;
    private DatabaseHelper dbHelper;
    private static final int REQUEST_CODE_ADD_EXERCISES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_days);

        dbHelper = new DatabaseHelper(this);
        selectedDays = new HashSet<>();

        Button mondayButton = findViewById(R.id.mondayButton);
        Button tuesdayButton = findViewById(R.id.tuesdayButton);
        Button wednesdayButton = findViewById(R.id.wednesdayButton);
        Button thursdayButton = findViewById(R.id.thursdayButton);
        Button fridayButton = findViewById(R.id.fridayButton);
        Button saturdayButton = findViewById(R.id.saturdayButton);
        Button sundayButton = findViewById(R.id.sundayButton);
        Button nextButton = findViewById(R.id.nextButton);

        setupDayButton(mondayButton, "Poniedziałek");
        setupDayButton(tuesdayButton, "Wtorek");
        setupDayButton(wednesdayButton, "Środa");
        setupDayButton(thursdayButton, "Czwartek");
        setupDayButton(fridayButton, "Piątek");
        setupDayButton(saturdayButton, "Sobota");
        setupDayButton(sundayButton, "Niedziela");

        nextButton.setOnClickListener(v -> {
            if (selectedDays.isEmpty()) {
                Toast.makeText(this, "Wybierz przynajmniej jeden dzień", Toast.LENGTH_SHORT).show();
            } else {
                int userId = 1; // Zakładamy, że userId to 1 (admin), w przyszłości pobierz dynamicznie
                for (String day : selectedDays) {
                    dbHelper.saveTrainingDay(userId, day);
                }
                Toast.makeText(this, "Dni zapisane", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupDayButton(Button button, String dayName) {
        button.setOnClickListener(v -> {
            boolean isSelected = selectedDays.contains(dayName);
            if (!isSelected) {
                selectedDays.add(dayName);
                button.setBackgroundTintList(getResources().getColorStateList(R.color.green, null));
                // Zapisujemy dzień w bazie i przechodzimy do TrainingSetupActivity
                int userId = 1; // Zakładamy, że userId to 1 (admin), w przyszłości pobierz dynamicznie
                long dayId = dbHelper.getTrainingDayId(userId, dayName);
                if (dayId == -1) {
                    dayId = dbHelper.saveTrainingDay(userId, dayName);
                }
                Intent intent = new Intent(TrainingDaysActivity.this, TrainingSetupActivity.class);
                intent.putExtra("DAY_NAME", dayName);
                intent.putExtra("DAY_ID", dayId);
                startActivityForResult(intent, REQUEST_CODE_ADD_EXERCISES);
            } else {
                selectedDays.remove(dayName);
                button.setBackgroundTintList(getResources().getColorStateList(R.color.grey, null));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_EXERCISES && resultCode == RESULT_OK) {
            Toast.makeText(this, "Ćwiczenia zapisane dla wybranego dnia", Toast.LENGTH_SHORT).show();
        }
    }
}