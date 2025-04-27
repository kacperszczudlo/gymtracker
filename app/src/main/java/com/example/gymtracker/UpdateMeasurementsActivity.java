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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateMeasurementsActivity extends AppCompatActivity {
    private static final String TAG = "UpdateMeasurements";
    private DatabaseHelper dbHelper;
    private int userId;
    private EditText heightEditText, weightEditText, armCircEditText, waistCircEditText, hipCircEditText;

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

        // Initialize views with null checks
        heightEditText = findViewById(R.id.heightEditText);
        weightEditText = findViewById(R.id.weightEditText);
        armCircEditText = findViewById(R.id.armCircEditText);
        waistCircEditText = findViewById(R.id.waistCircEditText);
        hipCircEditText = findViewById(R.id.hipCircEditText);
        Button saveButton = findViewById(R.id.saveButton);
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton profileButton = findViewById(R.id.profileButton);

        // Check for null views
        if (heightEditText == null || weightEditText == null || armCircEditText == null ||
                waistCircEditText == null || hipCircEditText == null || saveButton == null ||
                menuButton == null || profileButton == null) {
            Log.e(TAG, "One or more views are null");
            Toast.makeText(this, "Błąd: Nie znaleziono elementów interfejsu", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Load existing measurements
        try {
            loadMeasurements();
        } catch (Exception e) {
            Log.e(TAG, "Error loading measurements: " + e.getMessage(), e);
            Toast.makeText(this, "Błąd podczas ładowania danych: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set click listeners
        saveButton.setOnClickListener(v -> {
            try {
                saveMeasurements();
            } catch (Exception e) {
                Log.e(TAG, "Error saving measurements: " + e.getMessage(), e);
                Toast.makeText(this, "Błąd podczas zapisywania danych: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateMeasurementsActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateMeasurementsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
    }

    private void loadMeasurements() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("profile", new String[]{"height", "weight", "arm_circumference", "waist_circumference", "hip_circumference"},
                    "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    heightEditText.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndexOrThrow("height"))));
                    weightEditText.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndexOrThrow("weight"))));
                    armCircEditText.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndexOrThrow("arm_circumference"))));
                    waistCircEditText.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndexOrThrow("waist_circumference"))));
                    hipCircEditText.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndexOrThrow("hip_circumference"))));
                }
                cursor.close();
            } else {
                Log.e(TAG, "Cursor is null");
                Toast.makeText(this, "Błąd: Nie można załadować danych", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Database error in loadMeasurements: " + e.getMessage(), e);
            throw e;
        }
    }

    private void saveMeasurements() {
        String heightStr = heightEditText.getText().toString().trim();
        String weightStr = weightEditText.getText().toString().trim();
        String armCircStr = armCircEditText.getText().toString().trim();
        String waistCircStr = waistCircEditText.getText().toString().trim();
        String hipCircStr = hipCircEditText.getText().toString().trim();

        if (heightStr.isEmpty() || weightStr.isEmpty() || armCircStr.isEmpty() || waistCircStr.isEmpty() || hipCircStr.isEmpty()) {
            Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            return;
        }

        float height, weight, armCirc, waistCirc, hipCirc;
        try {
            height = Float.parseFloat(heightStr);
            weight = Float.parseFloat(weightStr);
            armCirc = Float.parseFloat(armCircStr);
            waistCirc = Float.parseFloat(waistCircStr);
            hipCirc = Float.parseFloat(hipCircStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid number format: " + e.getMessage(), e);
            Toast.makeText(this, "Wprowadź poprawne wartości liczbowe", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("height", height);
        values.put("weight", weight);
        values.put("arm_circumference", armCirc);
        values.put("waist_circumference", waistCirc);
        values.put("hip_circumference", hipCirc);
        values.put("user_id", userId);
        values.put("gender", "Nieokreślona");

        try {
            db.beginTransaction();
            try {
                // Try update first
                int rowsAffected = db.update("profile", values, "user_id=?", new String[]{String.valueOf(userId)});
                Log.d(TAG, "Update rows affected: " + rowsAffected);

                // If no rows updated, insert a new record
                if (rowsAffected == 0) {
                    long insertResult = db.insertOrThrow("profile", null, values);
                    Log.d(TAG, "Insert result: " + insertResult);
                }

                db.setTransactionSuccessful();
                Toast.makeText(this, "Pomiary zaktualizowane", Toast.LENGTH_SHORT).show();
                finish();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            Log.e(TAG, "Database error in saveMeasurements: " + e.getMessage(), e);
            Toast.makeText(this, "Błąd podczas zapisywania danych: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}