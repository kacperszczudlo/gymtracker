package com.example.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.gymtracker.databinding.ActivityUserProfileBinding;

import java.util.List;
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

        dbHelper.debugDatabase(userId);

        String username = dbHelper.getUsername(userId);
        binding.usernameTextView.setText(username != null ? username : "Brak danych");

        displayProgressData();

        binding.fullProgressButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FullProgressActivity.class);
            startActivity(intent);
        });

        binding.achievementsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AchievementsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SETTINGS);
        });

        binding.accountSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateUserDataActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SETTINGS);
        });

        binding.logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

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
            displayProgressData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayProgressData();
    }

    private void displayProgressData() {
        float[] latestData = dbHelper.getLatestProfileData(userId); // [weight, armCirc, waistCirc, hipCirc, height]
        float latestBenchPress = dbHelper.getLatestBenchPress(userId);
        float initialBenchPress = dbHelper.getInitialBenchPress(userId);

        float initialWeight = 0f;
        Cursor goalsCursor = dbHelper.getUserGoals(userId);
        if (goalsCursor.moveToFirst()) {
            initialWeight = goalsCursor.getFloat(goalsCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_WEIGHT));
        }
        goalsCursor.close();

        float[] initialData = dbHelper.getInitialProfileData(userId); // [weight, armCirc]
        float initialArmCirc = initialData[1];

        float weightProgress = latestData[0] - initialWeight;
        float benchPressProgress = latestBenchPress - initialBenchPress;

        Log.d("UserProfileActivity", "Initial Weight: " + initialWeight + ", Latest Weight: " + latestData[0] + ", Weight Progress: " + weightProgress);
        Log.d("UserProfileActivity", "Initial Arm: " + initialArmCirc + ", Latest Arm: " + latestData[1]);
        Log.d("UserProfileActivity", "Initial Bench: " + initialBenchPress + ", Latest Bench: " + latestBenchPress + ", Bench Progress: " + benchPressProgress);

        // Weight



        // Arm Circumference (from history)
        List<DatabaseHelper.BodyStatEntry> history = dbHelper.getBodyStatHistory(userId);
        if (history.size() >= 2) {
            float firstArm = history.get(0).armCirc;
            float lastArm = history.get(history.size() - 1).armCirc;
            float armDelta = lastArm - firstArm;

            String armText = String.format(Locale.US, "%.1f cm", lastArm);
            String progressText = String.format(Locale.US, " (%s%.1f cm)", armDelta >= 0 ? "+" : "-", Math.abs(armDelta));
            SpannableString spannableArm = new SpannableString(armText + progressText);
            int start = armText.length();
            int end = (armText + progressText).length();
            spannableArm.setSpan(
                    new ForegroundColorSpan(getResources().getColor(armDelta >= 0 ? R.color.green : R.color.red, getTheme())),
                    start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            binding.progressArmCircTextView.setText(spannableArm);
        } else if (history.size() == 1) {
            float arm = history.get(0).armCirc;
            binding.progressArmCircTextView.setText(String.format(Locale.US, "%.1f cm", arm));
        } else {
            binding.progressArmCircTextView.setText("Brak danych");
        }


        // Weight progress (based on history table)
        if (history.size() >= 2) {
            float firstWeight = history.get(0).weight;
            float lastWeight = history.get(history.size() - 1).weight;
            float weightDelta = lastWeight - firstWeight;

            String weightText = String.format(Locale.US, "%.1f kg", lastWeight);
            String progressText = String.format(Locale.US, " (%s%.1f kg)", weightDelta >= 0 ? "+" : "-", Math.abs(weightDelta));
            SpannableString spannableWeight = new SpannableString(weightText + progressText);
            int start = weightText.length();
            int end = (weightText + progressText).length();
            spannableWeight.setSpan(
                    new ForegroundColorSpan(getResources().getColor(weightDelta >= 0 ? R.color.green : R.color.red, getTheme())),
                    start, end,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            binding.progressWeightTextView.setText(spannableWeight);
            Log.d("UserProfileActivity", "History weight progress: " + weightDelta);
        } else if (history.size() == 1) {
            float weight = history.get(0).weight;
            binding.progressWeightTextView.setText(String.format(Locale.US, "%.1f kg", weight));
            Log.d("UserProfileActivity", "Only one weight entry: " + weight);
        } else {
            binding.progressWeightTextView.setText("Brak danych");
            Log.d("UserProfileActivity", "No weight history data");
        }

        // Bench Press
        if (initialBenchPress == 0 || latestBenchPress == 0) {
            binding.progressBenchPressTextView.setText("Brak danych");
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
