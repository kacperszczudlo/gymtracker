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
    private EditText targetWeightEditText, targetTrainingDaysEditText;
    private TextView weightProgressTextView, trainingDaysProgressTextView;
    private ProgressBar weightProgressBar, trainingDaysProgressBar;
    private float currentWeight, targetWeight, startWeight;
    private int currentTrainingDays, targetTrainingDays;
    private static final int DEFAULT_TARGET_TRAINING_DAYS = 3; // Default goal: 3 training days

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
        targetTrainingDaysEditText = findViewById(R.id.targetTrainingDaysEditText);
        weightProgressTextView = findViewById(R.id.weightProgressTextView);
        trainingDaysProgressTextView = findViewById(R.id.trainingDaysProgressTextView);
        weightProgressBar = findViewById(R.id.weightProgressBar);
        trainingDaysProgressBar = findViewById(R.id.trainingDaysProgressBar);
        Button saveGoalsButton = findViewById(R.id.saveGoalsButton);
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton homeButton = findViewById(R.id.homeButton);
        ImageButton profileButton = findViewById(R.id.profileButton);

        // Null checks for critical views
        if (targetWeightEditText == null || weightProgressTextView == null || weightProgressBar == null ||
                targetTrainingDaysEditText == null || trainingDaysProgressTextView == null ||
                trainingDaysProgressBar == null || saveGoalsButton == null ||
                menuButton == null || homeButton == null || profileButton == null) {
            Log.e(TAG, "One or more views not found in layout");
            Toast.makeText(this, "Błąd: Nie znaleziono elementów interfejsu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set click listeners
        saveGoalsButton.setOnClickListener(v -> saveGoals());

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

        // Load initial data
        loadWeightData();
        loadTrainingDaysData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data to reflect changes
        loadWeightData();
        loadTrainingDaysData();
    }

    private void loadWeightData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            // Load current weight from profile
            Cursor profileCursor = db.query(DatabaseHelper.TABLE_PROFILE, new String[]{DatabaseHelper.COLUMN_WEIGHT},
                    DatabaseHelper.COLUMN_PROFILE_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
            if (profileCursor.moveToFirst()) {
                currentWeight = profileCursor.getFloat(profileCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT));
                Log.d(TAG, "Loaded currentWeight: " + currentWeight);
            } else {
                currentWeight = 0f;
                Log.w(TAG, "No weight data found in profile for userId: " + userId);
                Toast.makeText(this, "Brak danych o wadze. Uzupełnij profil.", Toast.LENGTH_SHORT).show();
            }
            profileCursor.close();

            // Load target weight and start weight from user_goals
            Cursor goalsCursor = dbHelper.getUserGoals(userId);
            if (goalsCursor.moveToFirst()) {
                targetWeight = goalsCursor.getFloat(goalsCursor.getColumnIndexOrThrow("target_weight"));
                startWeight = goalsCursor.getFloat(goalsCursor.getColumnIndexOrThrow("start_weight"));
                targetWeightEditText.setText(String.format("%.1f", targetWeight));
                Log.d(TAG, "Loaded targetWeight: " + targetWeight + ", startWeight: " + startWeight);
            } else {
                targetWeight = 0f;
                startWeight = currentWeight;
                targetWeightEditText.setText("");
                Log.w(TAG, "No goal data found in user_goals for userId: " + userId);
            }
            goalsCursor.close();

            updateWeightProgress();
        } catch (Exception e) {
            Log.e(TAG, "Error loading weight data: " + e.getMessage(), e);
            Toast.makeText(this, "Błąd podczas ładowania danych", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    private void loadTrainingDaysData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            // Count active training days
            currentTrainingDays = dbHelper.getActiveTrainingDaysCount(userId);
            Log.d(TAG, "Loaded currentTrainingDays: " + currentTrainingDays);

            // Load target training days from user_goals
            Cursor goalsCursor = dbHelper.getUserGoals(userId);
            if (goalsCursor.moveToFirst()) {
                targetTrainingDays = goalsCursor.getInt(goalsCursor.getColumnIndexOrThrow("target_training_days"));
                targetTrainingDaysEditText.setText(String.valueOf(targetTrainingDays));
                Log.d(TAG, "Loaded targetTrainingDays: " + targetTrainingDays);
            } else {
                targetTrainingDays = DEFAULT_TARGET_TRAINING_DAYS;
                targetTrainingDaysEditText.setText(String.valueOf(targetTrainingDays));
                Log.w(TAG, "No training days goal found, using default: " + targetTrainingDays);
            }
            goalsCursor.close();

            updateTrainingDaysProgress();
        } catch (Exception e) {
            Log.e(TAG, "Error loading training days data: " + e.getMessage(), e);
            Toast.makeText(this, "Błąd podczas ładowania danych treningowych", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    private void saveGoals() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Save weight goal
            String targetWeightStr = targetWeightEditText.getText().toString().trim();
            float newTargetWeight = 0f;
            if (!targetWeightStr.isEmpty()) {
                newTargetWeight = Float.parseFloat(targetWeightStr);
                if (newTargetWeight <= 0) {
                    Toast.makeText(this, "Waga musi być większa od 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Save training days goal
            String targetTrainingDaysStr = targetTrainingDaysEditText.getText().toString().trim();
            int newTargetTrainingDays = DEFAULT_TARGET_TRAINING_DAYS;
            if (!targetTrainingDaysStr.isEmpty()) {
                newTargetTrainingDays = Integer.parseInt(targetTrainingDaysStr);
                if (newTargetTrainingDays <= 0 || newTargetTrainingDays > 7) {
                    Toast.makeText(this, "Liczba dni treningowych musi być między 1 a 7", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_ID, userId);
            if (newTargetWeight > 0) {
                values.put("target_weight", newTargetWeight);
                values.put("start_weight", currentWeight);
            }
            values.put("target_training_days", newTargetTrainingDays);

            // Save goals using DatabaseHelper method, passing the open database
            boolean success = dbHelper.saveUserGoals(userId, values, db);
            if (success) {
                db.setTransactionSuccessful();
                Toast.makeText(this, "Cele zapisane", Toast.LENGTH_SHORT).show();
                targetWeight = newTargetWeight;
                startWeight = currentWeight;
                targetTrainingDays = newTargetTrainingDays;
                updateWeightProgress();
                updateTrainingDaysProgress();
                Log.d(TAG, "Saved goals: targetWeight=" + targetWeight + ", startWeight=" + startWeight + ", targetTrainingDays=" + targetTrainingDays);
            } else {
                Toast.makeText(this, "Błąd podczas zapisywania celów", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to save user_goals");
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Nieprawidłowy format danych", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Invalid input format: " + e.getMessage(), e);
        } catch (Exception e) {
            Toast.makeText(this, "Błąd podczas zapisywania celów", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error saving goals: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private void updateWeightProgress() {
        Log.d(TAG, "Updating weight progress: currentWeight=" + currentWeight + ", targetWeight=" + targetWeight + ", startWeight=" + startWeight);

        if (currentWeight == 0 || targetWeight == 0 || startWeight == 0) {
            weightProgressTextView.setText("Progres: 0%");
            weightProgressBar.setProgress(0);
            Log.w(TAG, "Weight progress set to 0: one or more weights are 0");
            return;
        }

        if (Math.abs(targetWeight - startWeight) < 0.1f) {
            weightProgressTextView.setText("Progres: 0%");
            weightProgressBar.setProgress(0);
            Log.w(TAG, "Weight progress set to 0: targetWeight and startWeight are too close");
            return;
        }

        float progress;
        if (targetWeight > startWeight) {
            progress = ((currentWeight - startWeight) / (targetWeight - startWeight)) * 100;
        } else {
            progress = ((startWeight - currentWeight) / (startWeight - targetWeight)) * 100;
        }

        progress = Math.max(0, Math.min(100, progress));
        if (Float.isNaN(progress) || Float.isInfinite(progress)) {
            progress = 0;
            Log.w(TAG, "Invalid weight progress value, setting to 0");
        }

        int progressInt = Math.round(progress);
        runOnUiThread(() -> {
            weightProgressTextView.setText("Progres: " + progressInt + "%");
            weightProgressBar.setProgress(progressInt);
            Log.d(TAG, "Updated weight UI: progress=" + progressInt + "%");
        });
    }

    private void updateTrainingDaysProgress() {
        Log.d(TAG, "Updating training days progress: currentTrainingDays=" + currentTrainingDays + ", targetTrainingDays=" + targetTrainingDays);

        if (targetTrainingDays == 0) {
            trainingDaysProgressTextView.setText("Progres: 0/0");
            trainingDaysProgressBar.setProgress(0);
            Log.w(TAG, "Training days progress set to 0: targetTrainingDays is 0");
            return;
        }

        float progress = ((float) currentTrainingDays / targetTrainingDays) * 100;
        progress = Math.max(0, Math.min(100, progress));
        int progressInt = Math.round(progress);

        runOnUiThread(() -> {
            trainingDaysProgressTextView.setText("Progres: " + currentTrainingDays + "/" + targetTrainingDays);
            trainingDaysProgressBar.setProgress(progressInt);
            Log.d(TAG, "Updated training days UI: progress=" + progressInt + "% (" + currentTrainingDays + "/" + targetTrainingDays + ")");
        });
    }
}