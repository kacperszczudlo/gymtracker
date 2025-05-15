package com.example.gymtracker;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TrainingGoalsActivity extends AppCompatActivity {
    private static final String TAG = "TrainingGoalsActivity";
    private DatabaseHelper dbHelper;
    private int userId;
    private EditText targetWeightEditText;
    private TextView weightProgressTextView;
    private ProgressBar weightProgressBar;
    private float currentWeight;
    private float targetWeight;
    private float startWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_goals);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Log.e(TAG, "Invalid userId: " + userId);
            Toast.makeText(this, "Błąd użytkownika. Spróbuj ponownie się zalogować.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize views
        targetWeightEditText = findViewById(R.id.targetWeightEditText);
        weightProgressTextView = findViewById(R.id.weightProgressTextView);
        weightProgressBar = findViewById(R.id.weightProgressBar);
        Button saveGoalsButton = findViewById(R.id.saveGoalsButton);
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton homeButton = findViewById(R.id.homeButton);
        ImageButton profileButton = findViewById(R.id.profileButton);

        // Null checks for critical views
        if (targetWeightEditText == null || weightProgressTextView == null || weightProgressBar == null ||
                saveGoalsButton == null || menuButton == null || homeButton == null || profileButton == null) {
            Log.e(TAG, "One or more views not found in layout");
            Toast.makeText(this, "Błąd: Nie znaleziono elementów interfejsu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set click listeners
        saveGoalsButton.setOnClickListener(v -> saveTargetWeight());

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(TrainingGoalsActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(TrainingGoalsActivity.this, TrainingMainActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(TrainingGoalsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        // Load initial weight data
        loadWeightData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload weight data to reflect changes in current weight
        loadWeightData();
    }

    private void loadWeightData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            // Load current weight from profile
            Cursor profileCursor = db.query("profile", new String[]{"weight"},
                    "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
            if (profileCursor.moveToFirst()) {
                currentWeight = profileCursor.getFloat(profileCursor.getColumnIndexOrThrow("weight"));
                Log.d(TAG, "Loaded currentWeight: " + currentWeight);
            } else {
                currentWeight = 0f;
                Log.w(TAG, "No weight data found in profile for userId: " + userId);
                Toast.makeText(this, "Brak danych o wadze. Uzupełnij profil.", Toast.LENGTH_SHORT).show();
            }
            profileCursor.close();

            // Load target weight and start weight from user_goals
            Cursor goalsCursor = db.query("user_goals", new String[]{"target_weight", "start_weight"},
                    "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
            if (goalsCursor.moveToFirst()) {
                targetWeight = goalsCursor.getFloat(goalsCursor.getColumnIndexOrThrow("target_weight"));
                startWeight = goalsCursor.getFloat(goalsCursor.getColumnIndexOrThrow("start_weight"));
                targetWeightEditText.setText(String.format("%.1f", targetWeight));
                Log.d(TAG, "Loaded targetWeight: " + targetWeight + ", startWeight: " + startWeight);
            } else {
                targetWeight = 0f;
                startWeight = currentWeight; // Default to current weight if no goal exists
                targetWeightEditText.setText("");
                Log.w(TAG, "No goal data found in user_goals for userId: " + userId);
            }
            goalsCursor.close();

            updateWeightProgress();
        } catch (Exception e) {
            Log.e(TAG, "Error loading weight data: " + e.getMessage(), e);
            Toast.makeText(this, "Błąd podczas ładowania danych", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTargetWeight() {
        String targetWeightStr = targetWeightEditText.getText().toString().trim();
        if (targetWeightStr.isEmpty()) {
            Toast.makeText(this, "Wprowadź docelową wagę", Toast.LENGTH_SHORT).show();
            return;
        }

        float newTargetWeight;
        try {
            newTargetWeight = Float.parseFloat(targetWeightStr);
            if (newTargetWeight <= 0) {
                Toast.makeText(this, "Waga musi być większa od 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Nieprawidłowy format wagi", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("target_weight", newTargetWeight);
        values.put("start_weight", currentWeight); // Store current weight as start weight

        try {
            db.beginTransaction();
            // Delete existing goal to avoid duplicates
            db.delete("user_goals", "user_id=?", new String[]{String.valueOf(userId)});
            // Insert new goal
            long result = db.insert("user_goals", null, values);
            if (result != -1) {
                db.setTransactionSuccessful();
                Toast.makeText(this, "Cel wagi zapisany", Toast.LENGTH_SHORT).show();
                targetWeight = newTargetWeight;
                startWeight = currentWeight;
                Log.d(TAG, "Saved targetWeight: " + targetWeight + ", startWeight: " + startWeight);
                updateWeightProgress();
            } else {
                Toast.makeText(this, "Błąd podczas zapisywania celu", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to insert into user_goals");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving target weight: " + e.getMessage(), e);
            Toast.makeText(this, "Błąd podczas zapisywania celu", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
        }
    }

    private void updateWeightProgress() {
        Log.d(TAG, "Updating progress: currentWeight=" + currentWeight + ", targetWeight=" + targetWeight + ", startWeight=" + startWeight);

        if (currentWeight == 0 || targetWeight == 0 || startWeight == 0) {
            weightProgressTextView.setText("Progres: 0%");
            weightProgressBar.setProgress(0);
            Log.w(TAG, "Progress set to 0: one or more weights are 0");
            return;
        }

        if (Math.abs(targetWeight - startWeight) < 0.1f) {
            weightProgressTextView.setText("Progres: 0%");
            weightProgressBar.setProgress(0);
            Log.w(TAG, "Progress set to 0: targetWeight and startWeight are too close");
            return;
        }

        float progress;
        if (targetWeight > startWeight) {
            // Weight gain goal
            progress = ((currentWeight - startWeight) / (targetWeight - startWeight)) * 100;
            Log.d(TAG, "Weight gain: progress = ((currentWeight - startWeight) / (targetWeight - startWeight)) * 100");
        } else {
            // Weight loss goal
            progress = ((startWeight - currentWeight) / (startWeight - targetWeight)) * 100;
            Log.d(TAG, "Weight loss: progress = ((startWeight - currentWeight) / (startWeight - targetWeight)) * 100");
        }

        // Clamp progress between 0 and 100
        if (progress < 0) progress = 0;
        if (progress > 100) progress = 100;

        if (Float.isNaN(progress) || Float.isInfinite(progress)) {
            progress = 0;
            Log.w(TAG, "Invalid progress value, setting to 0");
        }

        int progressInt = Math.round(progress);
        runOnUiThread(() -> {
            weightProgressTextView.setText("Progres: " + progressInt + "%");
            weightProgressBar.setProgress(progressInt);
            Log.d(TAG, "Updated UI: progress=" + progressInt + "%");
        });
    }
}