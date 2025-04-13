package com.example.gymtracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;

    private EditText heightEditText;
    private Button finishButton;

    // Jeżeli przekazujesz dane z poprzedniego ekranu:
    private String firstName, lastName, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // <-- nowy layout z ScrollView

        // Odbieranie danych z Intentu (jeśli tak robisz):
        if (getIntent() != null) {
            firstName = getIntent().getStringExtra("EXTRA_FIRST_NAME");
            lastName  = getIntent().getStringExtra("EXTRA_LAST_NAME");
            email     = getIntent().getStringExtra("EXTRA_EMAIL");
            password  = getIntent().getStringExtra("EXTRA_PASSWORD");
        }

        // Powiązanie z layoutem - UWAGA: nazwy ID z XML
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        maleRadioButton  = findViewById(R.id.maleRadioButton);
        femaleRadioButton= findViewById(R.id.femaleRadioButton);

        heightEditText   = findViewById(R.id.heightEditText);
        finishButton     = findViewById(R.id.profileFinishButton);

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
            // brak zaznaczonego przycisku - ewentualna walidacja
            Toast.makeText(this, "Wybierz płeć", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Wczytujemy wzrost z EditText
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

        // 3. Tworzymy obiekt do wysłania (User, itp.)
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setGender(genderStr);
        user.setHeight(height);

        // 4. Wywołanie do backendu przez Retrofit
        ApiService api = RetrofitClient.getApiService();
        api.registerUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this,
                            "Zarejestrowano użytkownika: " + response.body().getEmail(),
                            Toast.LENGTH_LONG).show();
                    finish(); // zamykamy aktywność, wracamy do poprzedniego ekranu
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
    }
}

