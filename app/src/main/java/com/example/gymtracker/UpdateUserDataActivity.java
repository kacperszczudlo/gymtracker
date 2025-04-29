package com.example.gymtracker;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateUserDataActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private int userId;
    private EditText usernameEditText, surnameEditText, emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_data);

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
        usernameEditText = findViewById(R.id.usernameEditText);
        surnameEditText = findViewById(R.id.surnameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button saveButton = findViewById(R.id.saveButton);
        ImageButton menuButton = findViewById(R.id.menuButton);
        ImageButton profileButton = findViewById(R.id.profileButton);
        ImageButton homeButton = findViewById(R.id.homeButton);

        // Null checks for navigation buttons
        if (menuButton == null || profileButton == null) {
            Toast.makeText(this, "Błąd: Nie znaleziono przycisków nawigacji", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load existing data
        loadUserData();

        // Set click listeners
        saveButton.setOnClickListener(v -> saveUserData());

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateUserDataActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateUserDataActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateUserDataActivity.this, TrainingMainActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"username", "surname", "email"}, "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor.moveToFirst()) {
            usernameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("username")));
            surnameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("surname")));
            emailEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        }
        cursor.close();
    }

    private void saveUserData() {
        String username = usernameEditText.getText().toString().trim();
        String surname = surnameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || surname.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Nieprawidłowy format email", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("surname", surname);
        values.put("email", email);
        if (!password.isEmpty()) {
            values.put("password", password);
        }

        long result = db.update("users", values, "user_id=?", new String[]{String.valueOf(userId)});
        if (result > 0) {
            Toast.makeText(this, "Dane zaktualizowane", Toast.LENGTH_SHORT).show();
            // Przejdź do UserProfileActivity
            Intent intent = new Intent(UpdateUserDataActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish(); // Zamknij bieżącą aktywność
        } else {
            Toast.makeText(this, "Błąd podczas aktualizacji", Toast.LENGTH_SHORT).show();
        }
    }
}