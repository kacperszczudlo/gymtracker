package com.example.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button nextButton; // dawniej "registerButton"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Powiązanie z layoutem
        firstNameEditText       = findViewById(R.id.registerUsernameEditText);         // Imię
        lastNameEditText        = findViewById(R.id.registerUserSurnameEditText);     // Nazwisko
        emailEditText           = findViewById(R.id.registerUserEmailEditText);       // E-mail
        passwordEditText        = findViewById(R.id.registerPasswordEditText);        // Hasło
        confirmPasswordEditText = findViewById(R.id.registerConfirmPasswordEditText); // Potwierdź hasło
        nextButton              = findViewById(R.id.registerButton);                  // Przycisk "Dalej"

        nextButton.setText("Dalej"); // Zmieniamy etykietę przycisku

        // Obsługa kliknięcia
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfileStep();
            }
        });
    }

    private void goToProfileStep() {
        // Pobieramy dane z EditTextów
        String firstName  = firstNameEditText.getText().toString().trim();
        String lastName   = lastNameEditText.getText().toString().trim();
        String email      = emailEditText.getText().toString().trim();
        String password   = passwordEditText.getText().toString().trim();
        String confirmPwd = confirmPasswordEditText.getText().toString().trim();

        // Walidacja podstawowa
        if (firstName.isEmpty()) {
            firstNameEditText.setError("Podaj imię");
            firstNameEditText.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            lastNameEditText.setError("Podaj nazwisko");
            lastNameEditText.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            emailEditText.setError("Podaj email");
            emailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Podaj hasło");
            passwordEditText.requestFocus();
            return;
        }
        if (!password.equals(confirmPwd)) {
            confirmPasswordEditText.setError("Hasła się nie zgadzają");
            confirmPasswordEditText.requestFocus();
            return;
        }

        // Przechodzimy do kolejnego ekranu (ActivityProfile),
        // przekazując dane: firstName, lastName, email, password
        Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);

        // Wkładamy dane do Intentu (klucze i wartości)
        intent.putExtra("EXTRA_FIRST_NAME", firstName);
        intent.putExtra("EXTRA_LAST_NAME", lastName);
        intent.putExtra("EXTRA_EMAIL", email);
        intent.putExtra("EXTRA_PASSWORD", password);

        startActivity(intent);
    }
}
