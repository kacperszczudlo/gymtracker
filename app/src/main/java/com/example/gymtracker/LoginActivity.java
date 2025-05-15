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
import api.model.ApiClient;
import api.model.ApiService;
import api.model.UserProfileResponse;
import api.model.LoginRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        // Inicjalizacja Retrofit API
        apiService = ApiClient.getClient(this).create(ApiService.class);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Nieprawidłowy format email", Toast.LENGTH_SHORT).show();
            } else {
                // Wywołanie API logowania
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);

        apiService.login(request).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse user = response.body();

                    // Zapis danych do SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("user_id", user.getId());
                    editor.putString("email", user.getEmail());
                    editor.putString("first_name", user.getFirstName());
                    editor.putString("last_name", user.getLastName());
                    editor.putString("gender", user.getGender());
                    editor.putInt("height", user.getHeight() != null ? user.getHeight() : 0);

                    if (user.getWeight() != null) editor.putFloat("weight", user.getWeight().floatValue());
                    if (user.getWaistCircumference() != null) editor.putFloat("waist", user.getWaistCircumference().floatValue());
                    if (user.getArmCircumference() != null) editor.putFloat("arm", user.getArmCircumference().floatValue());
                    if (user.getHipCircumference() != null) editor.putFloat("hip", user.getHipCircumference().floatValue());

                    editor.putString("token", user.getToken());
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Zalogowano pomyślnie", Toast.LENGTH_SHORT).show();

                    // Przejście do głównej aktywności
                    Intent intent = new Intent(LoginActivity.this, TrainingMainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, "Nieprawidłowe dane logowania", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Błąd połączenia: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
