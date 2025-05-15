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

public class UpdateMeasurementsActivity extends AppCompatActivity {
    private static final String TAG = "UpdateMeasurements";
    private DatabaseHelper dbHelper;
    private EditText heightEditText, armCircEditText, waistCircEditText, hipCircEditText, weightEditText;
    private int userId;
    private String loadedGender; // Variable to store the loaded gender

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

        // Initialize views
        heightEditText = findViewById(R.id.heightEditText);
        armCircEditText = findViewById(R.id.armCircEditText);
        waistCircEditText = findViewById(R.id.waistCircEditText);
        hipCircEditText = findViewById(R.id.hipCircEditText);
        weightEditText = findViewById(R.id.weightEditText);
        Button saveButton = findViewById(R.id.saveButton);
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton homeButton = findViewById(R.id.homeButton);
        ImageButton profileButton = findViewById(R.id.profileButton);

        // Detailed null checks for debugging
        if (heightEditText == null) Log.e(TAG, "heightEditText is null");
        if (armCircEditText == null) Log.e(TAG, "armCircEditText is null");
        if (waistCircEditText == null) Log.e(TAG, "waistCircEditText is null");
        if (hipCircEditText == null) Log.e(TAG, "hipCircEditText is null");
        if (weightEditText == null) Log.e(TAG, "weightEditText is null");
        if (saveButton == null) Log.e(TAG, "saveButton is null");
        if (menuButton == null) Log.e(TAG, "menuButton is null");
        if (homeButton == null) Log.e(TAG, "homeButton is null");
        if (profileButton == null) Log.e(TAG, "profileButton is null");

        // Null checks for critical views
        if (heightEditText == null || armCircEditText == null || waistCircEditText == null ||
                hipCircEditText == null || weightEditText == null || saveButton == null ||
                menuButton == null || homeButton == null || profileButton == null) {
            Log.e(TAG, "One or more views not found in layout");
            Toast.makeText(this, "Błąd: Nie znaleziono elementów interfejsu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load existing profile data
        loadProfileData();

        // Set click listeners
        saveButton.setOnClickListener(v -> saveMeasurements());

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateMeasurementsActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
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
        // Columns to retrieve, including gender
        String[] projection = {
                DatabaseHelper.COLUMN_GENDER, // Make sure COLUMN_GENDER is correctly defined in DatabaseHelper
                DatabaseHelper.COLUMN_HEIGHT,
                DatabaseHelper.COLUMN_ARM_CIRC,
                DatabaseHelper.COLUMN_WAIST_CIRC,
                DatabaseHelper.COLUMN_HIP_CIRC,
                DatabaseHelper.COLUMN_WEIGHT
        };
        String selection = DatabaseHelper.COLUMN_PROFILE_USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_PROFILE, projection, selection, selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                // Retrieve gender
                loadedGender = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENDER));
                if (loadedGender == null || loadedGender.isEmpty()){
                    loadedGender = "Not specified"; // Default value if gender is not set
                }

                float height = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HEIGHT));
                float armCirc = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ARM_CIRC));
                float waistCirc = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WAIST_CIRC));
                float hipCirc = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIP_CIRC));
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT));

                // Populate EditText fields
                heightEditText.setText(height > 0 ? String.format("%.1f", height) : "");
                armCircEditText.setText(armCirc > 0 ? String.format("%.1f", armCirc) : "");
                waistCircEditText.setText(waistCirc > 0 ? String.format("%.1f", waistCirc) : "");
                hipCircEditText.setText(hipCirc > 0 ? String.format("%.1f", hipCirc) : "");
                weightEditText.setText(weight > 0 ? String.format("%.1f", weight) : "");

                Log.d(TAG, "Loaded profile: gender=" + loadedGender + ", height=" + height + ", armCirc=" + armCirc +
                        ", waistCirc=" + waistCirc + ", hipCirc=" + hipCirc + ", weight=" + weight);
            } else {
                Log.w(TAG, "No profile data found for userId: " + userId);
                loadedGender = "Not specified"; // Default if no profile exists
                Toast.makeText(this, "Brak zapisanych danych. Wprowadź nowe pomiary.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading profile data: " + e.getMessage(), e);
            loadedGender = "Not specified"; // Default on error
            Toast.makeText(this, "Błąd podczas ładowania danych", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMeasurements() {
        String heightStr = heightEditText.getText().toString().trim();
        String armCircStr = armCircEditText.getText().toString().trim();
        String waistCircStr = waistCircEditText.getText().toString().trim();
        String hipCircStr = hipCircEditText.getText().toString().trim();
        String weightStr = weightEditText.getText().toString().trim();

        if (heightStr.isEmpty() || armCircStr.isEmpty() || waistCircStr.isEmpty() ||
                hipCircStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            return;
        }

        float height, armCirc, waistCirc, hipCirc, weight;
        try {
            height = Float.parseFloat(heightStr);
            armCirc = Float.parseFloat(armCircStr);
            waistCirc = Float.parseFloat(waistCircStr);
            hipCirc = Float.parseFloat(hipCircStr);
            weight = Float.parseFloat(weightStr);
            if (height <= 0 || armCirc <= 0 || waistCirc <= 0 || hipCirc <= 0 || weight <= 0) {
                Toast.makeText(this, "Wprowadź prawidłowe wartości (większe od 0)", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Wprowadź prawidłowe liczby", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure loadedGender has a value (it should from loadProfileData)
        String genderToSave = (this.loadedGender != null && !this.loadedGender.isEmpty()) ? this.loadedGender : "Not specified";

        // Save profile
        try {
            // Pass the genderToSave to the saveProfile method
            if (dbHelper.saveProfile(userId, genderToSave, height, armCirc, waistCirc, hipCirc, weight)) {
                Toast.makeText(this, "Pomiary zapisane", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Saved profile: gender=" + genderToSave + ", height=" + height + ", armCirc=" + armCirc +
                        ", waistCirc=" + waistCirc + ", hipCirc=" + hipCirc + ", weight=" + weight);
                finish(); // Go back to the previous activity or a relevant screen
            } else {
                Toast.makeText(this, "Błąd podczas zapisywania pomiarów", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to save profile");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving measurements: " + e.getMessage(), e);
            Toast.makeText(this, "Błąd: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
