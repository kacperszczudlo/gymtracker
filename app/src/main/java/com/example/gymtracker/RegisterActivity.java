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

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameEditText, surnameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        usernameEditText = findViewById(R.id.registerUsernameEditText);
        surnameEditText = findViewById(R.id.registerUserSurnameEditText);
        emailEditText = findViewById(R.id.registerUserEmailEditText);
        passwordEditText = findViewById(R.id.registerPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.registerConfirmPasswordEditText);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String surname = surnameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (username.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Nieprawidłowy format adresu email", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Hasła nie są zgodne", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.checkIfEmailExists(email)) {
                Toast.makeText(this, "Podany email już istnieje", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.registerUser(username, password, email, surname)) {
                int userId = getUserIdByEmail(email);
                if (userId != -1) {
                    SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    prefs.edit().putInt("user_id", userId).apply();

                    Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Błąd podczas rejestracji użytkownika", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Błąd podczas rejestracji", Toast.LENGTH_SHORT).show();
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
