package com.example.gymtracker;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UserProfileActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Błąd użytkownika. Spróbuj ponownie się zalogować.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize views
        TextView usernameTextView = findViewById(R.id.usernameTextView);
        TextView progressWeightTextView = findViewById(R.id.progressWeightTextView);
        TextView progressArmCircTextView = findViewById(R.id.progressArmCircTextView);
        TextView progressBenchPressTextView = findViewById(R.id.progressBenchPressTextView);
        Button achievementsButton = findViewById(R.id.achievementsButton);
        Button accountSettingsButton = findViewById(R.id.accountSettingsButton);
        Button fullProgressButton = findViewById(R.id.fullProgressButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton profileButton = findViewById(R.id.profileButton);
        ImageButton homeButton = findViewById(R.id.homeButton);

        // Null checks for navigation buttons
        if (menuButton == null || profileButton == null) {
            Toast.makeText(this, "Błąd: Nie znaleziono przycisków nawigacji", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load user data
        loadUserData(usernameTextView, progressWeightTextView, progressArmCircTextView, progressBenchPressTextView);

        // Set click listeners
        fullProgressButton.setOnClickListener(v -> {
            Toast.makeText(this, "Pełen progres - wkrótce dostępna", Toast.LENGTH_SHORT).show();
        });

        achievementsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Osiągnięcia - wkrótce dostępne", Toast.LENGTH_SHORT).show();
        });

        accountSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, UpdateUserDataActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear().apply();
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
        });
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, TrainingMainActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            // Already in UserProfileActivity
            Toast.makeText(this, "Jesteś już w profilu", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData(TextView usernameTextView, TextView weightTextView, TextView armCircTextView, TextView benchPressTextView) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor userCursor = db.query("users", new String[]{"username"}, "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
        if (userCursor.moveToFirst()) {
            usernameTextView.setText(userCursor.getString(userCursor.getColumnIndexOrThrow("username")));
        }
        userCursor.close();

        Cursor profileCursor = db.query("profile", new String[]{"weight", "arm_circumference"}, "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
        if (profileCursor.moveToFirst()) {
            float weight = profileCursor.getFloat(profileCursor.getColumnIndexOrThrow("weight"));
            float armCirc = profileCursor.getFloat(profileCursor.getColumnIndexOrThrow("arm_circumference"));
            weightTextView.setText(weight + " kg");
            armCircTextView.setText(armCirc + " cm");
        }
        profileCursor.close();

        benchPressTextView.setText("Brak danych");
    }
}