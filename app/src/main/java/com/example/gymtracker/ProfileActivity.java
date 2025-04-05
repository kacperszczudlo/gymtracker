package com.example.gymtracker; // Zmień na nazwę swojego pakietu

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private RadioGroup genderRadioGroup;
    private RadioButton femaleRadioButton, maleRadioButton;
    private EditText heightEditText, circumference1EditText, circumference2EditText, circumference3EditText, weightEditText;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inicjalizacja elementów UI
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        heightEditText = findViewById(R.id.heightEditText);
        circumference1EditText = findViewById(R.id.circumference1EditText);
        circumference2EditText = findViewById(R.id.circumference2EditText);
        circumference3EditText = findViewById(R.id.circumference3EditText);
        weightEditText = findViewById(R.id.weightEditText);
        nextButton = findViewById(R.id.nextButton);

        // Obsługa kliknięcia przycisku DALEJ
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pobierz dane z pól
                String gender = "";
                if (genderRadioGroup.getCheckedRadioButtonId() == R.id.femaleRadioButton) {
                    gender = "Kobieta";
                } else if (genderRadioGroup.getCheckedRadioButtonId() == R.id.maleRadioButton) {
                    gender = "Męczyzna";
                }

                String height = heightEditText.getText().toString().trim();
                String circumference1 = circumference1EditText.getText().toString().trim();
                String circumference2 = circumference2EditText.getText().toString().trim();
                String circumference3 = circumference3EditText.getText().toString().trim();
                String weight = weightEditText.getText().toString().trim();

                // Podstawowa walidacja
                if (gender.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Wybierz płeć", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (height.isEmpty()) {
                    heightEditText.setError("Podaj wzrost");
                    return;
                }
                if (weight.isEmpty()) {
                    weightEditText.setError("Podaj masę ciała");
                    return;
                }

                // Jeśli walidacja przeszła, możesz zapisać dane lub przejść do kolejnej aktywności
                Toast.makeText(ProfileActivity.this, "Dane zapisane:\nPłeć: " + gender + "\nWzrost: " + height + " cm\nMasa: " + weight + " kg", Toast.LENGTH_LONG).show();

                // Tutaj możesz dodać kod do przejścia do kolejnej aktywności, np.:
                // Intent intent = new Intent(ProfileActivity.this, NextActivity.class);
                // startActivity(intent);
            }
        });
    }
}