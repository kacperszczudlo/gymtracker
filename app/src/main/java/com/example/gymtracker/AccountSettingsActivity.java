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
        Button newPlanButton = findViewById(R.id.newPlanButton);
        Button trackProgressButton = findViewById(R.id.trackProgressButton);
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton profileButton = findViewById(R.id.profileButton);

        // Null checks for navigation buttons
        if (menuButton == null || profileButton == null) {
            Toast.makeText(this, "Błąd: Nie znaleziono przycisków nawigacji", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set click listeners
        trainingGoalsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Cele treningowe - wkrótce dostępne", Toast.LENGTH_SHORT).show();
        });

        updateMeasurementsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, UpdateMeasurementsActivity.class);
            startActivity(intent);
        });

        updatePlanButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, TrainingDaysActivity.class);
            startActivity(intent);
        });

        trackProgressButton.setOnClickListener(v -> {
            Toast.makeText(this, "Śledź progres - wkrótce dostępne", Toast.LENGTH_SHORT).show();
        });

        newPlanButton.setOnClickListener(v -> {
            Toast.makeText(this, "Nowy plan - wkrótce dostępne", Toast.LENGTH_SHORT).show();
        });

        menuButton.setOnClickListener(v -> {
            // Already in UserProfileActivity
            Toast.makeText(this, "Jesteś już w ustawieniach", Toast.LENGTH_SHORT).show();
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });



    }
}