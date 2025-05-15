package com.example.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import api.model.ApiClient;
import api.model.ApiService;
import api.model.UpdateUserProfileRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private RadioGroup genderRadioGroup;
    private EditText heightEditText, armCircEditText, waistCircEditText, hipCircEditText, weightEditText;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        heightEditText = findViewById(R.id.heightEditText);
        armCircEditText = findViewById(R.id.circumference1EditText);
        waistCircEditText = findViewById(R.id.circumference2EditText);
        hipCircEditText = findViewById(R.id.circumference3EditText);
        weightEditText = findViewById(R.id.weightEditText);
        Button finishButton = findViewById(R.id.profileFinishButton);

        finishButton.setOnClickListener(v -> {
            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            String gender = selectedGenderId == R.id.femaleRadioButton ? "female" : "male";
            String heightStr = heightEditText.getText().toString();
            String armCircStr = armCircEditText.getText().toString();
            String waistCircStr = waistCircEditText.getText().toString();
            String hipCircStr = hipCircEditText.getText().toString();
            String weightStr = weightEditText.getText().toString();

            if (heightStr.isEmpty() || armCircStr.isEmpty() || waistCircStr.isEmpty() || hipCircStr.isEmpty() || weightStr.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                return;
            }

            int height = Integer.parseInt(heightStr);
            double arm = Double.parseDouble(armCircStr);
            double waist = Double.parseDouble(waistCircStr);
            double hip = Double.parseDouble(hipCircStr);
            double weight = Double.parseDouble(weightStr);

            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);

            if (userId == -1) {
                Toast.makeText(this, "Błąd użytkownika. Spróbuj ponownie się zalogować.", Toast.LENGTH_SHORT).show();
                return;
            }

            UpdateUserProfileRequest request = new UpdateUserProfileRequest(
                    gender, height, weight, waist, arm, hip
            );

            apiService.updateUserProfile(userId, request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        startActivity(new Intent(ProfileActivity.this, TrainingDaysActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Błąd serwera przy zapisie profilu", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Błąd sieci: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
