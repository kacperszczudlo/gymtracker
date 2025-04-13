package com.example.gymtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.gymtracker.BodyStatDto;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;

    private EditText heightEditText;
    private Button finishButton;

    // Nowe pola dla statystyk ciała
    private EditText weightEditText;
    private EditText circumference1EditText; // np. obwód ramion
    private EditText circumference2EditText; // np. obwód talii
    private EditText circumference3EditText; // np. obwód bioder

    // Dane przekazywane z poprzedniego ekranu
    private String firstName, lastName, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Layout powinien być rozszerzony o nowe EditTexty

        // Odbieranie danych z Intentu (jeśli są przekazywane)
        if (getIntent() != null) {
            firstName = getIntent().getStringExtra("EXTRA_FIRST_NAME");
            lastName  = getIntent().getStringExtra("EXTRA_LAST_NAME");
            email     = getIntent().getStringExtra("EXTRA_EMAIL");
            password  = getIntent().getStringExtra("EXTRA_PASSWORD");
        }

        // Powiązanie oryginalnych elementów UI
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        maleRadioButton  = findViewById(R.id.maleRadioButton);
        femaleRadioButton= findViewById(R.id.femaleRadioButton);
        heightEditText   = findViewById(R.id.heightEditText);
        finishButton     = findViewById(R.id.profileFinishButton);

        // Powiązanie nowych elementów UI z layoutem
        weightEditText = findViewById(R.id.weightEditText);
        circumference1EditText = findViewById(R.id.circumference1EditText);
        circumference2EditText = findViewById(R.id.circumference2EditText);
        circumference3EditText = findViewById(R.id.circumference3EditText);

        finishButton.setOnClickListener(v -> finishRegistration());
    }

    private void finishRegistration() {
        // 1. Ustalamy, która płeć jest zaznaczona w RadioGroup
        String genderStr = "";
        if (maleRadioButton.isChecked()) {
            genderStr = "male";
        } else if (femaleRadioButton.isChecked()) {
            genderStr = "female";
        } else {
            Toast.makeText(this, "Wybierz płeć", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Walidacja wzrostu
        String heightStr = heightEditText.getText().toString().trim();
        if (heightStr.isEmpty()) {
            heightEditText.setError("Podaj wzrost");
            heightEditText.requestFocus();
            return;
        }
        int height;
        try {
            height = Integer.parseInt(heightStr);
        } catch (NumberFormatException e) {
            heightEditText.setError("Niepoprawna liczba");
            heightEditText.requestFocus();
            return;
        }

        // 3. Pobieramy dane dotyczące statystyk ciała: waga oraz obwody
        String weightStr = weightEditText.getText().toString().trim();
        String circum1Str = circumference1EditText.getText().toString().trim();
        String circum2Str = circumference2EditText.getText().toString().trim();
        String circum3Str = circumference3EditText.getText().toString().trim();

        if (weightStr.isEmpty() || circum1Str.isEmpty() || circum2Str.isEmpty() || circum3Str.isEmpty()) {
            Toast.makeText(this, "Uzupełnij wszystkie pola dotyczące statystyk ciała", Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal weight, circum1, circum2, circum3;
        try {
            weight = new BigDecimal(weightStr);
            circum1 = new BigDecimal(circum1Str);
            circum2 = new BigDecimal(circum2Str);
            circum3 = new BigDecimal(circum3Str);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Wprowadź poprawne wartości liczbowe dla statystyk", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Tworzymy obiekt User z danymi rejestracyjnymi
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setGender(genderStr);
        user.setHeight(height);

        ApiService api = RetrofitClient.getApiService();

        // 5. Rejestracja użytkownika (zapis do tabeli user)
        api.registerUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Zakładamy, że backend zwraca identyfikator użytkownika – metoda getId() musi być dostępna
                    int userId = response.body().getId();

                    // Tworzymy obiekt BodyStatDto z danymi statystyk
                    BodyStatDto bodyStatDto = new BodyStatDto();
                    bodyStatDto.setWeight(weight);
                    bodyStatDto.setArmCircumference(circum1);
                    bodyStatDto.setWaistCircumference(circum2);
                    bodyStatDto.setHipCircumference(circum3);

                    // Wywołanie endpointu aktualizującego body_stat (PUT /api/users/{userId}/bodystat)
                    // Wywołanie endpointu aktualizującego body_stat (PUT /api/users/{userId}/bodystat)
                    api.updateBodyStat(userId, bodyStatDto).enqueue(new Callback<BodyStatDto>() {
                        @Override
                        public void onResponse(Call<BodyStatDto> call, Response<BodyStatDto> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this,
                                        "Profil uzupełniony!",
                                        Toast.LENGTH_LONG).show();
                                // Zamiast finish() przenosimy użytkownika do TrainingDaysActivity:
                                startActivity(new Intent(ProfileActivity.this, TrainingDaysActivity.class));
                                finish();
                            } else {
                                Toast.makeText(ProfileActivity.this,
                                        "Błąd aktualizacji statystyk: " + response.code(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BodyStatDto> call, Throwable t) {
                            Toast.makeText(ProfileActivity.this,
                                    "Błąd połączenia przy aktualizacji statystyk: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    Toast.makeText(ProfileActivity.this,
                            "Błąd rejestracji: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,
                        "Błąd połączenia z serwerem: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }}
