package com.example.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, registerButton;
    private final String TAG = "GymTrackerTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicjalizacja przycisków
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // ⬇️ TEST REJESTRACJI UŻYTKOWNIKA — tylko raz, na starcie
        testUserRegistration();

        // Obsługa kliknięcia przycisku Zaloguj
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class); // Jeśli masz LoginActivity
                startActivity(intent);
            }
        });

        // Obsługa kliknięcia przycisku Zarejestruj
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class); // Jeśli masz RegisterActivity
                startActivity(intent);
            }
        });
    }

    private void testUserRegistration() {
        User testUser = new User();
        testUser.firstName = "Testowy21";
        testUser.lastName = "Użytkownik37";
        testUser.email = "norbert" + System.currentTimeMillis() + "@sigma.com";
        testUser.passwordHash = "haslo1663";
        testUser.gender = "male";
        testUser.height = 180;

        // 💡 Loguj JSON, żeby sprawdzić, co faktycznie wysyłasz
        String json = new com.google.gson.Gson().toJson(testUser);
        Log.d("JSON_TEST", "Wysyłam JSON: " + json);

        // 🛰️ Wywołanie Retrofit
        RetrofitClient.getApiService().registerUser(testUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "✅ Rejestracja OK: " + response.body().email);
                    Toast.makeText(MainActivity.this, "✅ Zarejestrowano!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "❌ Błąd rejestracji: " + response.code());
                    Toast.makeText(MainActivity.this, "❌ Błąd: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "🚫 Brak połączenia: " + t.getMessage());
                Toast.makeText(MainActivity.this, "🚫 Brak połączenia z backendem", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
