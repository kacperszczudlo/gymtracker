package com.example.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        if (prefs.contains("user_id")) {
            startActivity(new Intent(MainActivity.this, TrainingMainActivity.class));
            finish();
            return; // <- ważne, żeby nie wykonać dalej setContentView!
        }

        setContentView(R.layout.activity_main);

        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}