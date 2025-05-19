package com.example.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtracker.R;

public class ProfileActivity extends AppCompatActivity {
    private RadioGroup genderRadioGroup;
    private EditText heightEditText, armCircEditText, waistCircEditText, hipCircEditText, weightEditText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        heightEditText = findViewById(R.id.heightEditText);
        armCircEditText = findViewById(R.id.circumference1EditText);
        waistCircEditText = findViewById(R.id.circumference2EditText);
        hipCircEditText = findViewById(R.id.circumference3EditText);
        weightEditText = findViewById(R.id.weightEditText);
        Button finishButton = findViewById(R.id.profileFinishButton);

        finishButton.setOnClickListener(v -> {
            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            String gender = selectedGenderId == R.id.femaleRadioButton ? "Kobieta" : "Mężczyzna";
            String heightStr = heightEditText.getText().toString();
            String armCircStr = armCircEditText.getText().toString();
            String waistCircStr = waistCircEditText.getText().toString();
            String hipCircStr = hipCircEditText.getText().toString();
            String weightStr = weightEditText.getText().toString();

            if (heightStr.isEmpty() || armCircStr.isEmpty() || waistCircStr.isEmpty() || hipCircStr.isEmpty() || weightStr.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                return;
            }

            float height = Float.parseFloat(heightStr);
            float armCirc = Float.parseFloat(armCircStr);
            float waistCirc = Float.parseFloat(waistCircStr);
            float hipCirc = Float.parseFloat(hipCircStr);
            float weight = Float.parseFloat(weightStr);

            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);

            if (userId == -1) {
                Toast.makeText(this, "Błąd użytkownika. Spróbuj ponownie się zalogować.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.saveProfile(userId, gender, height, armCirc, waistCirc, hipCirc, weight)) {

                dbHelper.insertBodyStatHistory(userId, weight, armCirc, waistCirc, hipCirc);
                Intent intent = new Intent(ProfileActivity.this, TrainingDaysActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Błąd podczas zapisu profilu", Toast.LENGTH_SHORT).show();
            }

        });
    }
}