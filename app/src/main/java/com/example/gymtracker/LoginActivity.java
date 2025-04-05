package com.example.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicjalizacja elementów UI
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // Sprawdzenie, czy elementy UI zostały poprawnie zainicjalizowane
        if (usernameEditText == null || passwordEditText == null || loginButton == null) {
            Toast.makeText(this, "Błąd: Nie znaleziono elementów UI", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Inicjalizacja bazy danych
        dbHelper = new DatabaseHelper(this);

        // Obsługa kliknięcia przycisku Zaloguj
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Walidacja
                if (username.isEmpty()) {
                    usernameEditText.setError("Podaj nazwę użytkownika");
                    return;
                }
                if (password.isEmpty()) {
                    passwordEditText.setError("Podaj hasło");
                    return;
                }

                // Sprawdzenie danych logowania w bazie danych
                if (dbHelper.checkUser (username, password)) {
                    Toast.makeText(LoginActivity.this, "Logowanie pomyślne!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, TrainingDaysActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Błędna nazwa użytkownika lub hasło", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}