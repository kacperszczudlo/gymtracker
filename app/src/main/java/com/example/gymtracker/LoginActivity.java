package com.example.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Nieprawidłowy format email", Toast.LENGTH_SHORT).show();
            }
            else if (dbHelper.checkUserByEmail(email, password)) {
                int userId = getUserIdByEmail(email); // Pobieramy userId JEDEN raz
                if (userId != -1) {
                    SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    prefs.edit().putInt("user_id", userId).apply();

                    Toast.makeText(this, "Zalogowano: ID = " + userId, Toast.LENGTH_LONG).show();


                    Intent intent = new Intent(LoginActivity.this, TrainingMainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Błąd podczas logowania użytkownika", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Nieprawidłowe dane logowania", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getUserIdByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"user_id"},
                "email=?", new String[]{email}, null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        }
        cursor.close();
        return userId;
    }
}
