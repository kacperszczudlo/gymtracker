package com.example.gymtracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerUsernameEditText, registerPasswordEditText;
    private Button registerButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicjalizacja elementów UI
        registerUsernameEditText = findViewById(R.id.registerUsernameEditText);
        registerPasswordEditText = findViewById(R.id.registerPasswordEditText);
        registerButton = findViewById(R.id.registerButton);

        // Sprawdzenie, czy elementy UI zostały poprawnie zainicjalizowane
        if (registerUsernameEditText == null || registerPasswordEditText == null || registerButton == null) {
            Toast.makeText(this, "Błąd: Nie znaleziono elementów UI", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Inicjalizacja bazy danych
        dbHelper = new DatabaseHelper(this);

        // Obsługa kliknięcia przycisku Zarejestruj
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = registerUsernameEditText.getText().toString().trim();
                String password = registerPasswordEditText.getText().toString().trim();

                // Walidacja
                if (username.isEmpty()) {
                    registerUsernameEditText.setError("Podaj nazwę użytkownika");
                    return;
                }
                if (password.isEmpty()) {
                    registerPasswordEditText.setError("Podaj hasło");
                    return;
                }

                // Sprawdzenie, czy nazwa użytkownika już istnieje
                if (dbHelper.usernameExists(username)) {
                    registerUsernameEditText.setError("Nazwa użytkownika już istnieje");
                    return;
                }

                // Rejestracja użytkownika
                if (dbHelper.registerUser(username, password)) {
                    Toast.makeText(RegisterActivity.this, "Rejestracja pomyślna! Możesz się zalogować.", Toast.LENGTH_SHORT).show();
                    finish(); // Wróć do MainActivity (ekran logowania)
                } else {
                    Toast.makeText(RegisterActivity.this, "Błąd podczas rejestracji", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}