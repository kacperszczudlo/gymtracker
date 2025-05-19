package com.example.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AchievementsActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Błąd użytkownika. Spróbuj ponownie się zalogować.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize TextViews
        TextView benchPressTextView = findViewById(R.id.benchPressTextView);
        TextView squatsTextView = findViewById(R.id.squatsTextView);
        TextView deadliftTextView = findViewById(R.id.deadliftTextView);

        // Fetch best results from logs
        float benchPressMax = dbHelper.getBestLoggedWeightForExercise(userId, "Wyciskanie sztangi");
        float squatsMax = dbHelper.getBestLoggedWeightForExercise(userId, "Przysiady");
        float deadliftMax = dbHelper.getBestLoggedWeightForExercise(userId, "Martwy ciąg");

        // Display results
        benchPressTextView.setText(benchPressMax > 0 ? String.format("%.1f kg", benchPressMax) : "Brak danych");
        squatsTextView.setText(squatsMax > 0 ? String.format("%.1f kg", squatsMax) : "Brak danych");
        deadliftTextView.setText(deadliftMax > 0 ? String.format("%.1f kg", deadliftMax) : "Brak danych");

        // Set up navigation
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton homeButton = findViewById(R.id.homeButton);
        ImageButton profileButton = findViewById(R.id.profileButton);

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(AchievementsActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(AchievementsActivity.this, TrainingMainActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AchievementsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
    }
}