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
import java.util.Locale;

public class TrainingGoalsActivity extends AppCompatActivity {
    private static final String TAG = "TrainingGoalsActivity";
    private DatabaseHelper dbHelper;
    private int userId;
    private EditText targetWeightEditText, targetTrainingDaysEditText;
    private TextView weightProgressTextView, trainingDaysProgressTextView;
    private ProgressBar weightProgressBar, trainingDaysProgressBar;
    private float currentWeight, targetWeight, startWeight;
    private int currentTrainingDays, targetTrainingDays;
    private static final int DEFAULT_TARGET_TRAINING_DAYS = 3;
    private static final int REQUEST_CODE_MEASUREMENTS = 1;

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

        if (targetWeightEditText == null || weightProgressTextView == null || weightProgressBar == null ||
                targetTrainingDaysEditText == null || trainingDaysProgressTextView == null ||
                trainingDaysProgressBar == null || saveGoalsButton == null ||
                menuButton == null || homeButton == null || profileButton == null) {
            Log.e(TAG, "One or more views not found in layout");
            Toast.makeText(this, "Błąd: Nie znaleziono elementów interfejsu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        saveGoalsButton.setOnClickListener(v -> saveGoals());

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(TrainingGoalsActivity.this, AccountSettingsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_MEASUREMENTS);
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(TrainingGoalsActivity.this, TrainingMainActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(TrainingGoalsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        loadWeightData();
        loadTrainingDaysData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MEASUREMENTS && resultCode == RESULT_OK) {
            loadWeightData();
            loadTrainingDaysData();
            Log.d(TAG, "Refreshed data after UpdateMeasurementsActivity via AccountSettingsActivity");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWeightData();
        loadTrainingDaysData();
    }

    private void loadWeightData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Cursor profileCursor = db.query(DatabaseHelper.TABLE_PROFILE,
                    new String[]{DatabaseHelper.COLUMN_WEIGHT, DatabaseHelper.COLUMN_DATE},
                    DatabaseHelper.COLUMN_PROFILE_USER_ID + "=?", new String[]{String.valueOf(userId)},
                    null, null, DatabaseHelper.COLUMN_DATE + " DESC", "1");
            if (profileCursor.moveToFirst()) {
                currentWeight = profileCursor.getFloat(profileCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT));
                Log.d(TAG, "Loaded currentWeight: " + currentWeight);
            } else {
                currentWeight = 0f;
                Log.w(TAG, "No weight data found in profile for userId: " + userId);
                Toast.makeText(this, "Brak danych o wadze. Uzupełnij profil.", Toast.LENGTH_SHORT).show();
            }
            profileCursor.close();

            Cursor goalsCursor = dbHelper.getUserGoals(userId);
            if (goalsCursor.moveToFirst()) {
                targetWeight = goalsCursor.getFloat(goalsCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TARGET_WEIGHT));
                startWeight = goalsCursor.getFloat(goalsCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_WEIGHT));
                targetTrainingDays = goalsCursor.getInt(goalsCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TARGET_TRAINING_DAYS));
                targetWeightEditText.setText(String.format(Locale.US, "%.1f", targetWeight));
                targetTrainingDaysEditText.setText(String.valueOf(targetTrainingDays));
                Log.d(TAG, "Loaded from user_goals: targetWeight=" + targetWeight + ", startWeight=" + startWeight + ", targetTrainingDays=" + targetTrainingDays);
            } else {
                targetWeight = currentWeight > 0 ? currentWeight : 0f;
                startWeight = currentWeight;
                targetTrainingDays = DEFAULT_TARGET_TRAINING_DAYS; // Domyślnie 3
                if (currentWeight > 0) {
                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_USER_ID, userId);
                    values.put(DatabaseHelper.COLUMN_TARGET_WEIGHT, targetWeight);
                    values.put(DatabaseHelper.COLUMN_START_WEIGHT, startWeight);
                    values.put(DatabaseHelper.COLUMN_TARGET_TRAINING_DAYS, targetTrainingDays);
                    boolean success = dbHelper.saveUserGoals(userId, values, db);
                    if (success) {
                        targetWeightEditText.setText(String.format(Locale.US, "%.1f", targetWeight));
                        targetTrainingDaysEditText.setText(String.valueOf(targetTrainingDays));
                        Log.d(TAG, "Initialized user_goals: targetWeight=" + targetWeight + ", startWeight=" + startWeight + ", targetTrainingDays=" + targetTrainingDays);
                    } else {
                        Log.e(TAG, "Failed to initialize user_goals");
                        Toast.makeText(this, "Błąd podczas inicjalizacji celów", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    targetWeightEditText.setText("");
                    targetTrainingDaysEditText.setText(String.valueOf(DEFAULT_TARGET_TRAINING_DAYS));
                    Log.w(TAG, "No goal data and no valid currentWeight for userId: " + userId);
                }
            }
            goalsCursor.close();

            db.setTransactionSuccessful();
            updateWeightProgress();
        } catch (Exception e) {
            Log.e(TAG, "Error loading weight data: " + e.getMessage(), e);
            Toast.makeText(this, "Błąd podczas ładowania danych", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private void loadTrainingDaysData() {
        try {
            currentTrainingDays = dbHelper.getActiveTrainingDaysCount(userId);
            Log.d(TAG, "Loaded currentTrainingDays: " + currentTrainingDays);

            // Opcjonalnie: Loguj liczbę dni w planie dla informacji
            int planTrainingDays = dbHelper.getTargetTrainingDaysFromPlan(userId);
            Log.d(TAG, "Training days in plan for userId " + userId + ": " + planTrainingDays);

            // targetTrainingDays jest już wczytane w loadWeightData z user_goals
            if (targetTrainingDays == 0) {
                targetTrainingDays = DEFAULT_TARGET_TRAINING_DAYS;
                targetTrainingDaysEditText.setText(String.valueOf(targetTrainingDays));
                Log.w(TAG, "No targetTrainingDays in user_goals, using default: " + targetTrainingDays);
                // Zapisz domyślną wartość do bazy
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_USER_ID, userId);
                values.put(DatabaseHelper.COLUMN_TARGET_WEIGHT, targetWeight);
                values.put(DatabaseHelper.COLUMN_START_WEIGHT, startWeight);
                values.put(DatabaseHelper.COLUMN_TARGET_TRAINING_DAYS, targetTrainingDays);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    boolean success = dbHelper.saveUserGoals(userId, values, db);
                    if (success) {
                        Log.d(TAG, "Saved default targetTrainingDays to user_goals: " + targetTrainingDays);
                    } else {
                        Log.e(TAG, "Failed to save default targetTrainingDays");
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                    db.close();
                }
            }

            updateTrainingDaysProgress();
        } catch (Exception e) {
            Log.e(TAG, "Error loading training days data: " + e.getMessage(), e);
            Toast.makeText(this, "Błąd podczas ładowania danych treningowych", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveGoals() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            String targetWeightStr = targetWeightEditText.getText().toString().trim();
            float newTargetWeight = 0f;
            if (!targetWeightStr.isEmpty()) {
                newTargetWeight = Float.parseFloat(targetWeightStr);
                if (newTargetWeight <= 0) {
                    Toast.makeText(this, "Waga musi być większa od 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String targetTrainingDaysStr = targetTrainingDaysEditText.getText().toString().trim();
            int newTargetTrainingDays = targetTrainingDays; // Zachowaj bieżącą wartość z user_goals
            if (!targetTrainingDaysStr.isEmpty()) {
                newTargetTrainingDays = Integer.parseInt(targetTrainingDaysStr);
                if (newTargetTrainingDays <= 0 || newTargetTrainingDays > 7) {
                    Toast.makeText(this, "Liczba dni treningowych musi być między 1 a 7", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_ID, userId);
            values.put(DatabaseHelper.COLUMN_TARGET_WEIGHT, newTargetWeight > 0 ? newTargetWeight : targetWeight);
            values.put(DatabaseHelper.COLUMN_START_WEIGHT, newTargetWeight > 0 ? currentWeight : startWeight);
            values.put(DatabaseHelper.COLUMN_TARGET_TRAINING_DAYS, newTargetTrainingDays);

            boolean success = dbHelper.saveUserGoals(userId, values, db);
            if (success) {
                db.setTransactionSuccessful();
                Toast.makeText(this, "Cele zapisane", Toast.LENGTH_SHORT).show();
                if (newTargetWeight > 0) {
                    targetWeight = newTargetWeight;
                    startWeight = currentWeight;
                }
                targetTrainingDays = newTargetTrainingDays;
                updateWeightProgress();
                updateTrainingDaysProgress();
                Log.d(TAG, "Saved goals: targetWeight=" + targetWeight + ", startWeight=" + startWeight + ", targetTrainingDays=" + newTargetTrainingDays);
            } else {
                Toast.makeText(this, "Błąd podczas zapisywania celów", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to save user_goals: values=" + values.toString());
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
