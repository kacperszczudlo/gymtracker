package com.example.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class UpdateMeasurementsActivity extends AppCompatActivity {
    private static final String TAG = "UpdateMeasurements";
    private DatabaseHelper dbHelper;
    private EditText heightEditText, armCircEditText, waistCircEditText, hipCircEditText, weightEditText;
    private int userId;
    private String loadedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_measurements);

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

        heightEditText = findViewById(R.id.heightEditText);
        armCircEditText = findViewById(R.id.armCircEditText);
        waistCircEditText = findViewById(R.id.waistCircEditText);
        hipCircEditText = findViewById(R.id.hipCircEditText);
        weightEditText = findViewById(R.id.weightEditText);
        Button saveButton = findViewById(R.id.saveButton);
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton homeButton = findViewById(R.id.homeButton);
        ImageButton profileButton = findViewById(R.id.profileButton);

        if (heightEditText == null) Log.e(TAG, "heightEditText is null");
        if (armCircEditText == null) Log.e(TAG, "armCircEditText is null");
        if (waistCircEditText == null) Log.e(TAG, "waistCircEditText is null");
        if (hipCircEditText == null) Log.e(TAG, "hipCircEditText is null");
        if (weightEditText == null) Log.e(TAG, "weightEditText is null");
        if (saveButton == null) Log.e(TAG, "saveButton is null");
        if (menuButton == null) Log.e(TAG, "menuButton is null");
        if (homeButton == null) Log.e(TAG, "homeButton is null");
        if (profileButton == null) Log.e(TAG, "profileButton is null");

        if (heightEditText == null || armCircEditText == null || waistCircEditText == null ||
                hipCircEditText == null || weightEditText == null || saveButton == null ||
                menuButton == null || homeButton == null || profileButton == null) {
            Log.e(TAG, "One or more views not found in layout");
            Toast.makeText(this, "Błąd: Nie znaleziono elementów interfejsu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadProfileData();

        saveButton.setOnClickListener(v -> saveMeasurements());

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateMeasurementsActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
            setResult(RESULT_OK);
            finish();
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateMeasurementsActivity.this, TrainingMainActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateMeasurementsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
    }

    private void loadProfileData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseHelper.COLUMN_GENDER,
                DatabaseHelper.COLUMN_HEIGHT,
                DatabaseHelper.COLUMN_ARM_CIRC,
                DatabaseHelper.COLUMN_WAIST_CIRC,
                DatabaseHelper.COLUMN_HIP_CIRC,
                DatabaseHelper.COLUMN_WEIGHT,
                DatabaseHelper.COLUMN_DATE
        };
        String selection = DatabaseHelper.COLUMN_PROFILE_USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_PROFILE, projection, selection, selectionArgs,
                null, null, DatabaseHelper.COLUMN_DATE + " DESC", "1")) {
            if (cursor.moveToFirst()) {
                loadedGender = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENDER));
                if (loadedGender == null || loadedGender.isEmpty()) {
                    loadedGender = "Not specified";
                }

                float height = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HEIGHT));
                float armCirc = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ARM_CIRC));
                float waistCirc = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WAIST_CIRC));
                float hipCirc = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIP_CIRC));
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT));

                heightEditText.setText(height > 0 ? String.format(Locale.US, "%.1f", height) : "");
                armCircEditText.setText(armCirc > 0 ? String.format(Locale.US, "%.1f", armCirc) : "");
                waistCircEditText.setText(waistCirc > 0 ? String.format(Locale.US, "%.1f", waistCirc) : "");
                hipCircEditText.setText(hipCirc > 0 ? String.format(Locale.US, "%.1f", hipCirc) : "");
                weightEditText.setText(weight > 0 ? String.format(Locale.US, "%.1f", weight) : "");

                Log.d(TAG, "Loaded profile: gender=" + loadedGender + ", height=" + height + ", armCirc=" + armCirc +
                        ", waistCirc=" + waistCirc + ", hipCirc=" + hipCirc + ", weight=" + weight);
            } else {
                Log.w(TAG, "No profile data found for userId: " + userId);
                loadedGender = "Not specified";
                Toast.makeText(this, "Brak zapisanych danych. Wprowadź nowe pomiary.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading profile data: " + e.getMessage(), e);
            loadedGender = "Not specified";
            Toast.makeText(this, "Błąd podczas ładowania danych: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    private void saveMeasurements() {
        String heightStr = heightEditText.getText().toString().trim();
        String armCircStr = armCircEditText.getText().toString().trim();
        String waistCircStr = waistCircEditText.getText().toString().trim();
        String hipCircStr = hipCircEditText.getText().toString().trim();
        String weightStr = weightEditText.getText().toString().trim();

        float height, armCirc, waistCirc, hipCirc, weight;

        try {
            height = heightStr.isEmpty() ? 0f : Float.parseFloat(heightStr);
            armCirc = armCircStr.isEmpty() ? 0f : Float.parseFloat(armCircStr);
            waistCirc = waistCircStr.isEmpty() ? 0f : Float.parseFloat(waistCircStr);
            hipCirc = hipCircStr.isEmpty() ? 0f : Float.parseFloat(hipCircStr);
            weight = weightStr.isEmpty() ? 0f : Float.parseFloat(weightStr);

            if (height < 0 || armCirc < 0 || waistCirc < 0 || hipCirc < 0 || weight < 0) {
                Toast.makeText(this, "Pomiary nie mogą być ujemne", Toast.LENGTH_SHORT).show();
                return;
            }

            if (weight == 0) {
                Toast.makeText(this, "Waga jest wymagana", Toast.LENGTH_SHORT).show();
                return;
            }

            // Use updateProfile instead of saveProfile to update existing profile
            boolean success = dbHelper.updateProfile(userId, loadedGender, height, armCirc, waistCirc, hipCirc, weight);

            if (success) {
                dbHelper.insertBodyStatHistory(userId, weight, armCirc, waistCirc, hipCirc);
                Toast.makeText(this, "Pomiary zapisane", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Measurements updated: userId=" + userId + ", height=" + height + ", armCirc=" + armCirc +
                        ", waistCirc=" + waistCirc + ", hipCirc=" + hipCirc + ", weight=" + weight);
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Błąd podczas zapisywania pomiarów", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to update measurements for userId=" + userId);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Nieprawidłowy format danych", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Invalid input format: " + e.getMessage(), e);
        } catch (Exception e) {
            Toast.makeText(this, "Błąd podczas zapisywania danych", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error saving measurements: " + e.getMessage(), e);
        }
    }
}