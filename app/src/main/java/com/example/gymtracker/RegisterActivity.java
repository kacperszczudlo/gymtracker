package com.example.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtracker.R;

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
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Hasła nie są zgodne", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.registerUser(username, password, email, surname)) {
                Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Błąd podczas rejestracji", Toast.LENGTH_SHORT).show();
            }
        });
    }
}