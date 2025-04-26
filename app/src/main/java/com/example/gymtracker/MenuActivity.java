package com.example.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        // Wyczyść zapisane dane użytkownika
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Przejdź na ekran główny (MainActivity)
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Zamknij MenuActivity
        finish();
    }
}
