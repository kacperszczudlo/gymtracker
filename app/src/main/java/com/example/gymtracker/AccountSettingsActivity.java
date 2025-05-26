package com.example.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AccountSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // Initialize buttons
        Button trainingGoalsButton = findViewById(R.id.trainingGoalsButton);
        Button updatePlanButton = findViewById(R.id.updatePlanButton);
        Button updateMeasurementsButton = findViewById(R.id.updateMeasurementsButton);
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton homeButton = findViewById(R.id.homeButton);
        ImageButton profileButton = findViewById(R.id.profileButton);

        // Null checks for navigation buttons
        if (menuButton == null || homeButton == null || profileButton == null) {
            Toast.makeText(this, "Błąd: Nie znaleziono przycisków nawigacji", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set click listeners
        trainingGoalsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, TrainingGoalsActivity.class);
            startActivity(intent);
        });

        updateMeasurementsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, UpdateMeasurementsActivity.class);
            startActivity(intent);
        });

        updatePlanButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, TrainingDaysActivity.class);
            startActivity(intent);
        });

        menuButton.setOnClickListener(v -> {
            Toast.makeText(this, "Jesteś już w ustawieniach", Toast.LENGTH_SHORT).show();
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, TrainingMainActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
    }
}

