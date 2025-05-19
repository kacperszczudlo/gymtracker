package com.example.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtracker.databinding.ActivityUserProfileBinding;

import java.util.Locale;

public class UserProfileActivity extends AppCompatActivity {
    private ActivityUserProfileBinding binding;
    private DatabaseHelper dbHelper;
    private int userId;
    private static final int REQUEST_CODE_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        Log.d("UserProfileActivity", "User ID: " + userId);

        if (userId == -1) {
            Log.e("UserProfileActivity", "Invalid userId, redirecting to LoginActivity");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Debug database contents
        dbHelper.debugDatabase(userId);

        // Set username
        String username = dbHelper.getUsername(userId);
        binding.usernameTextView.setText(username != null ? username : "Brak danych");

        // Fetch and display progress data
        displayProgressData();

        // Button listeners
        binding.fullProgressButton.setOnClickListener(v -> {
            // TODO: Implement full progress view
        });
        binding.achievementsButton.setOnClickListener(v -> {
            // TODO: Implement achievements activity
        });
        binding.accountSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountSettingsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SETTINGS);
        });
        binding.logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Bottom navigation
        binding.menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountSettingsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SETTINGS);
        });
        binding.homeButton.setOnClickListener(v -> {
            startActivity(new Intent(this, TrainingMainActivity.class));
        });
        binding.profileButton.setOnClickListener(v -> {
            // Already on profile
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTINGS && resultCode == RESULT_OK) {
            displayProgressData(); // Refresh data after profile update
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayProgressData(); // Refresh data when activity resumes
    }

    private void displayProgressData() {
        // Fetch latest profile data
        float[] latestData = dbHelper.getLatestProfileData(userId); // [weight, armCirc, waistCirc, hipCirc, height]
        float latestBenchPress = dbHelper.getLatestBenchPress(userId);
        float initialBenchPress = dbHelper.getInitialBenchPress(userId);

        // Fetch initial weight from user_goals
        float initialWeight = 0f;
        Cursor goalsCursor = dbHelper.getUserGoals(userId);
        if (goalsCursor.moveToFirst()) {
            initialWeight = goalsCursor.getFloat(goalsCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_WEIGHT));
        }
        goalsCursor.close();

        // Since arm circumference isn't in user_goals, use initial profile data or 0 as fallback
        float[] initialData = dbHelper.getInitialProfileData(userId); // [weight, armCirc]
        float initialArmCirc = initialData[1]; // Use 0 if no initial arm data

        // Calculate progress
        float weightProgress = latestData[0] - initialWeight; // Latest weight - Initial weight from user_goals
        float armProgress = latestData[1] - initialArmCirc;   // Latest arm circ - Initial arm circ
        float benchPressProgress = latestBenchPress - initialBenchPress;

        Log.d("UserProfileActivity", "Initial Weight (user_goals): " + initialWeight + ", Latest Weight: " + latestData[0] + ", Weight Progress: " + weightProgress);
        Log.d("UserProfileActivity", "Initial Arm: " + initialArmCirc + ", Latest Arm: " + latestData[1] + ", Arm Progress: " + armProgress);
        Log.d("UserProfileActivity", "Initial Bench: " + initialBenchPress + ", Latest Bench: " + latestBenchPress + ", Bench Progress: " + benchPressProgress);

        // Weight progress
        if (initialWeight == 0 || latestData[0] == 0) {
            binding.progressWeightTextView.setText("Brak danych");
            Log.d("UserProfileActivity", "Weight: Missing initial or latest data");
        } else {
            String weightText = String.format(Locale.US, "%.1f kg", latestData[0]);
            String progressText = String.format(Locale.US, " (%s%.1f kg)", weightProgress >= 0 ? "+" : "-", Math.abs(weightProgress));
            SpannableString spannableWeight = new SpannableString(weightText + progressText);
            int start = weightText.length();
            int end = (weightText + progressText).length();
            spannableWeight.setSpan(
                    new ForegroundColorSpan(getResources().getColor(weightProgress >= 0 ? R.color.green : R.color.red, getTheme())),
                    start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            binding.progressWeightTextView.setText(spannableWeight);
        }

        // Arm circumference progress
        if (initialArmCirc == 0 || latestData[1] == 0) {
            binding.progressArmCircTextView.setText("Brak danych");
            Log.d("UserProfileActivity", "Arm: Missing initial or latest data");
        } else {
            String armText = String.format(Locale.US, "%.1f cm", latestData[1]);
            String progressText = String.format(Locale.US, " (%s%.1f cm)", armProgress >= 0 ? "+" : "-", Math.abs(armProgress));
            SpannableString spannableArm = new SpannableString(armText + progressText);
            int start = armText.length();
            int end = (armText + progressText).length();
            spannableArm.setSpan(
                    new ForegroundColorSpan(getResources().getColor(armProgress >= 0 ? R.color.green : R.color.red, getTheme())),
                    start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            binding.progressArmCircTextView.setText(spannableArm);
        }

        // Bench press progress (unchanged)
        if (initialBenchPress == 0 || latestBenchPress == 0) {
            binding.progressBenchPressTextView.setText("Brak danych");
            Log.d("UserProfileActivity", "Bench: Missing initial or latest data");
        } else {
            String benchText = String.format(Locale.US, "%.1f kg", latestBenchPress);
            String progressText = String.format(Locale.US, " (%s%.1f kg)", benchPressProgress >= 0 ? "+" : "-", Math.abs(benchPressProgress));
            SpannableString spannableBench = new SpannableString(benchText + progressText);
            int start = benchText.length();
            int end = (benchText + progressText).length();
            spannableBench.setSpan(
                    new ForegroundColorSpan(getResources().getColor(benchPressProgress >= 0 ? R.color.green : R.color.red, getTheme())),
                    start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            binding.progressBenchPressTextView.setText(spannableBench);
        }
    }
}