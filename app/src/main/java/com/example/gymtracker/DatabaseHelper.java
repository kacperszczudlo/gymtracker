package com.example.gymtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GymTracker.db";
    private static final int DATABASE_VERSION = 5;

    // Tabela użytkowników
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_SURNAME = "surname";

    // Tabela profilu
    public static final String TABLE_PROFILE = "profile";
    public static final String COLUMN_PROFILE_USER_ID = "user_id";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_ARM_CIRC = "arm_circumference";
    public static final String COLUMN_WAIST_CIRC = "waist_circumference";
    public static final String COLUMN_HIP_CIRC = "hip_circumference";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_DATE = "date";

    // Tabela celów użytkownika
    public static final String TABLE_USER_GOALS = "user_goals";
    public static final String COLUMN_TARGET_WEIGHT = "target_weight";
    public static final String COLUMN_START_WEIGHT = "start_weight";
    public static final String COLUMN_TARGET_TRAINING_DAYS = "target_training_days";

    // Tabela ćwiczeń
    public static final String TABLE_EXERCISES = "exercises";
    public static final String COLUMN_EXERCISE_ID = "exercise_id";
    public static final String COLUMN_EXERCISE_NAME = "name";

    // Tabela dni treningowych
    private static final String TABLE_TRAINING_DAYS = "training_days";
    private static final String COLUMN_DAY_ID = "day_id";
    private static final String COLUMN_DAY_NAME = "day_name";
    private static final String COLUMN_DAY_USER_ID = "user_id";

    // Tabela serii dla ćwiczeń w danym dniu
    private static final String TABLE_DAY_EXERCISES = "day_exercises";
    private static final String COLUMN_DAY_EXERCISE_ID = "day_exercise_id";
    private static final String COLUMN_DAY_EXERCISE_DAY_ID = "day_id";
    private static final String COLUMN_DAY_EXERCISE_NAME = "exercise_name";
    private static final String COLUMN_DAY_EXERCISE_REPS = "reps";
    private static final String COLUMN_DAY_EXERCISE_WEIGHT = "weight";

    // Tabele planów
    private static final String TABLE_TRAINING_PLAN = "training_plan";
    private static final String TABLE_PLAN_EXERCISE = "plan_exercise";
    private static final String COLUMN_PLAN_ID = "plan_id";
    private static final String COLUMN_PLAN_USER_ID = "user_id";
    private static final String COLUMN_PLAN_DAY_NAME = "day_name";
    private static final String COLUMN_PLAN_EXERCISE_ID = "plan_exercise_id";
    private static final String COLUMN_PLAN_EXERCISE_NAME = "exercise_name";
    private static final String COLUMN_PLAN_SERIES_COUNT = "series_count";

    private static final String COLUMN_PLAN_VALID_FROM = "plan_valid_from"; // <--- NOWA


    // Tabele dziennika treningowego
    private static final String TABLE_TRAINING_LOG = "training_log";
    private static final String TABLE_LOG_EXERCISE = "log_exercise";
    private static final String TABLE_LOG_SERIES = "log_series";
    private static final String COLUMN_LOG_ID = "log_id";
    private static final String COLUMN_LOG_USER_ID = "user_id";
    private static final String COLUMN_LOG_DATE = "date";
    private static final String COLUMN_LOG_DAY_NAME = "day_name";
    private static final String COLUMN_LOG_EXERCISE_ID = "log_exercise_id";
    private static final String COLUMN_LOG_SERIES_ID = "log_series_id";
    private static final String COLUMN_LOG_REPS = "reps";
    private static final String COLUMN_LOG_WEIGHT = "weight";


    // Tabela historii pomiarów
    public static final String TABLE_BODY_STAT_HISTORY = "body_stat_history";
    public static final String COLUMN_HISTORY_ID = "history_id";
    public static final String COLUMN_HISTORY_USER_ID = "user_id";
    public static final String COLUMN_HISTORY_DATE = "date";
    public static final String COLUMN_HISTORY_WEIGHT = "weight";
    public static final String COLUMN_HISTORY_ARM_CIRC = "arm_circumference";
    public static final String COLUMN_HISTORY_WAIST_CIRC = "waist_circumference";
    public static final String COLUMN_HISTORY_HIP_CIRC = "hip_circumference";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_SURNAME + " TEXT)";
        db.execSQL(createUsersTable);

        String createProfileTable = "CREATE TABLE " + TABLE_PROFILE + " (" +
                COLUMN_PROFILE_USER_ID + " INTEGER, " +
                COLUMN_GENDER + " TEXT, " +
                COLUMN_HEIGHT + " REAL, " +
                COLUMN_ARM_CIRC + " REAL, " +
                COLUMN_WAIST_CIRC + " REAL, " +
                COLUMN_HIP_CIRC + " REAL, " +
                COLUMN_WEIGHT + " REAL, " +
                COLUMN_DATE + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_PROFILE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createProfileTable);

        String createUserGoalsTable = "CREATE TABLE " + TABLE_USER_GOALS + " (" +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_TARGET_WEIGHT + " REAL, " +
                COLUMN_START_WEIGHT + " REAL, " +
                COLUMN_TARGET_TRAINING_DAYS + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createUserGoalsTable);

        String createExercisesTable = "CREATE TABLE " + TABLE_EXERCISES + " (" +
                COLUMN_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXERCISE_NAME + " TEXT)";
        db.execSQL(createExercisesTable);

        String createTrainingDaysTable = "CREATE TABLE " + TABLE_TRAINING_DAYS + " (" +
                COLUMN_DAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY_NAME + " TEXT, " +
                COLUMN_DAY_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_DAY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createTrainingDaysTable);

        String createDayExercisesTable = "CREATE TABLE " + TABLE_DAY_EXERCISES + " (" +
                COLUMN_DAY_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY_EXERCISE_DAY_ID + " INTEGER, " +
                COLUMN_DAY_EXERCISE_NAME + " TEXT, " +
                COLUMN_DAY_EXERCISE_REPS + " INTEGER, " +
                COLUMN_DAY_EXERCISE_WEIGHT + " REAL, " +
                "FOREIGN KEY(" + COLUMN_DAY_EXERCISE_DAY_ID + ") REFERENCES " + TABLE_TRAINING_DAYS + "(" + COLUMN_DAY_ID + "))";
        db.execSQL(createDayExercisesTable);

        db.execSQL("CREATE TABLE " + TABLE_TRAINING_PLAN + " (" +
                COLUMN_PLAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PLAN_USER_ID + " INTEGER NOT NULL," +
                COLUMN_PLAN_DAY_NAME + " TEXT NOT NULL," +
                COLUMN_PLAN_VALID_FROM + " TEXT NOT NULL," + // NOWA KOLUMNA
                "FOREIGN KEY(" + COLUMN_PLAN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))");


        db.execSQL("CREATE TABLE " + TABLE_PLAN_EXERCISE + " (" +
                COLUMN_PLAN_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PLAN_ID + " INTEGER NOT NULL," +
                COLUMN_PLAN_EXERCISE_NAME + " TEXT NOT NULL," +
                COLUMN_PLAN_SERIES_COUNT + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + COLUMN_PLAN_ID + ") REFERENCES " + TABLE_TRAINING_PLAN + "(" + COLUMN_PLAN_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_TRAINING_LOG + " (" +
                COLUMN_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_LOG_USER_ID + " INTEGER NOT NULL," +
                COLUMN_LOG_DATE + " TEXT NOT NULL," +
                COLUMN_LOG_DAY_NAME + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_LOG_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_LOG_EXERCISE + " (" +
                COLUMN_LOG_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_LOG_ID + " INTEGER NOT NULL," +
                COLUMN_PLAN_EXERCISE_NAME + " TEXT NOT NULL," +
                "FOREIGN KEY (" + COLUMN_LOG_ID + ") REFERENCES " + TABLE_TRAINING_LOG + "(" + COLUMN_LOG_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_LOG_SERIES + " (" +
                COLUMN_LOG_SERIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_LOG_EXERCISE_ID + " INTEGER NOT NULL," +
                COLUMN_LOG_REPS + " INTEGER," +
                COLUMN_LOG_WEIGHT + " REAL," +
                "FOREIGN KEY (" + COLUMN_LOG_EXERCISE_ID + ") REFERENCES " + TABLE_LOG_EXERCISE + "(" + COLUMN_LOG_EXERCISE_ID + "))");


        String createBodyStatHistoryTable = "CREATE TABLE " + TABLE_BODY_STAT_HISTORY + " (" +
                COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HISTORY_USER_ID + " INTEGER, " +
                COLUMN_HISTORY_DATE + " TEXT, " +
                COLUMN_HISTORY_WEIGHT + " REAL, " +
                COLUMN_HISTORY_ARM_CIRC + " REAL, " +
                COLUMN_HISTORY_WAIST_CIRC + " REAL, " +
                COLUMN_HISTORY_HIP_CIRC + " REAL, " +
                "FOREIGN KEY(" + COLUMN_HISTORY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createBodyStatHistoryTable);



        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USERNAME, "admin");
        adminValues.put(COLUMN_PASSWORD, hashPassword("admin"));
        adminValues.put(COLUMN_EMAIL, "admin@example.com");
        adminValues.put(COLUMN_SURNAME, "Admin");
        db.insert(TABLE_USERS, null, adminValues);

        String[] exercises = {"Przysiady", "Wyciskanie sztangi", "Martwy ciąg", "Rumuński martwy ciąg",
                "Wyprosty na maszynie", "Łydkownica", "Rozpiętki", "Wyciskanie hantli",
                "Wyciskanie francuskie", "Prostowanie ramion", "Wznosy bokiem",
                "Wiosłowanie sztangą", "T-Bar", "Uginanie przedramion", "Młotki"};
        for (String exercise : exercises) {
            ContentValues exerciseValues = new ContentValues();
            exerciseValues.put(COLUMN_EXERCISE_NAME, exercise);
            db.insert(TABLE_EXERCISES, null, exerciseValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USER_GOALS + " ADD COLUMN " + COLUMN_TARGET_TRAINING_DAYS + " INTEGER");
        }
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_PROFILE + " ADD COLUMN " + COLUMN_DATE + " TEXT");
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Error adding date column (v3): " + e.getMessage(), e);
            }
        }
        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_PROFILE + " ADD COLUMN " + COLUMN_DATE + " TEXT");
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Error adding date column (v4): " + e.getMessage(), e);
            }
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.query(TABLE_USERS,
                    new String[]{COLUMN_USER_ID},
                    COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                    new String[]{username, hashPassword(password)},
                    null, null, null);
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            return exists;
        } finally {
            db.close();
        }
    }

    public boolean checkUserByEmail(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.query(TABLE_USERS,
                    new String[]{COLUMN_USER_ID},
                    COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                    new String[]{email, hashPassword(password)},
                    null, null, null);
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            return exists;
        } finally {
            db.close();
        }
    }

    public boolean registerUser(String username, String password, String email, String surname) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_PASSWORD, hashPassword(password));
            values.put(COLUMN_EMAIL, email);
            values.put(COLUMN_SURNAME, surname);
            long result = db.insert(TABLE_USERS, null, values);
            return result != -1;
        } finally {
            db.close();
        }
    }

    public Cursor getUserGoals(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USER_GOALS, new String[]{"target_weight", "start_weight", COLUMN_TARGET_TRAINING_DAYS},
                COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
    }

    public Cursor getExercises() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_EXERCISES, new String[]{COLUMN_EXERCISE_ID, COLUMN_EXERCISE_NAME},
                null, null, null, null, null);
    }

    public long saveTrainingDay(int userId, String dayName) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DAY_USER_ID, userId);
            values.put(COLUMN_DAY_NAME, dayName);
            return db.insert(TABLE_TRAINING_DAYS, null, values);
        } finally {
            db.close();
        }
    }

    public long getTrainingDayId(int userId, String dayName) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.query(TABLE_TRAINING_DAYS,
                    new String[]{COLUMN_DAY_ID},
                    COLUMN_DAY_USER_ID + "=? AND " + COLUMN_DAY_NAME + "=?",
                    new String[]{String.valueOf(userId), dayName},
                    null, null, null);
            long dayId = -1;
            if (cursor.moveToFirst()) {
                dayId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DAY_ID));
            }
            cursor.close();
            return dayId;
        } finally {
            db.close();
        }
    }

    public boolean saveDayExercise(long dayId, Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            boolean success = true;
            for (Series series : exercise.getSeriesList()) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_DAY_EXERCISE_DAY_ID, dayId);
                values.put(COLUMN_DAY_EXERCISE_NAME, exercise.getName());
                values.put(COLUMN_DAY_EXERCISE_REPS, series.getReps());
                values.put(COLUMN_DAY_EXERCISE_WEIGHT, series.getWeight());
                if (db.insert(TABLE_DAY_EXERCISES, null, values) == -1) {
                    success = false;
                }
            }
            return success;
        } finally {
            db.close();
        }
    }

    public ArrayList<Exercise> getDayExercises(long dayId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            ArrayList<Exercise> exerciseList = new ArrayList<>();
            Cursor cursor = db.query(TABLE_DAY_EXERCISES,
                    new String[]{COLUMN_DAY_EXERCISE_NAME, COLUMN_DAY_EXERCISE_REPS, COLUMN_DAY_EXERCISE_WEIGHT},
                    COLUMN_DAY_EXERCISE_DAY_ID + "=?",
                    new String[]{String.valueOf(dayId)},
                    null, null, COLUMN_DAY_EXERCISE_NAME);

            String currentExerciseName = null;
            ArrayList<Series> seriesList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String exerciseName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY_EXERCISE_NAME));
                int reps = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DAY_EXERCISE_REPS));
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_DAY_EXERCISE_WEIGHT));

                if (currentExerciseName != null && !exerciseName.equals(currentExerciseName)) {
                    exerciseList.add(new Exercise(currentExerciseName, new ArrayList<>(seriesList)));
                    seriesList.clear();
                }
                seriesList.add(new Series(reps, weight));
                currentExerciseName = exerciseName;
            }
            if (currentExerciseName != null && !seriesList.isEmpty()) {
                exerciseList.add(new Exercise(currentExerciseName, seriesList));
            }
            cursor.close();
            return exerciseList;
        } finally {
            db.close();
        }
    }

    public boolean deleteDayExercise(long dayId, String exerciseName, int seriesIndex) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.query(TABLE_DAY_EXERCISES,
                    new String[]{COLUMN_DAY_EXERCISE_ID},
                    COLUMN_DAY_EXERCISE_DAY_ID + "=? AND " + COLUMN_DAY_EXERCISE_NAME + "=?",
                    new String[]{String.valueOf(dayId), exerciseName},
                    null, null, null);

            int count = 0;
            if (cursor.moveToPosition(seriesIndex)) {
                long seriesIdToDelete = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DAY_EXERCISE_ID));
                count = db.delete(TABLE_DAY_EXERCISES,
                        COLUMN_DAY_EXERCISE_ID + "=?",
                        new String[]{String.valueOf(seriesIdToDelete)});
            }
            cursor.close();
            return count > 0;
        } finally {
            db.close();
        }
    }

    public boolean deleteDayExercises(long dayId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int result = db.delete(TABLE_DAY_EXERCISES, COLUMN_DAY_EXERCISE_DAY_ID + "=?",
                    new String[]{String.valueOf(dayId)});
            return result > 0;
        } finally {
            db.close();
        }
    }

    public boolean checkIfEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.query(TABLE_USERS,
                    new String[]{COLUMN_USER_ID},
                    COLUMN_EMAIL + "=?",
                    new String[]{email},
                    null, null, null);
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            return exists;
        } finally {
            db.close();
        }
    }

    public long saveTrainingPlan(int userId, String dayName, List<Exercise> exerciseList, String planValidFrom) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long planId = -1;

        try {
            // 🟢 Usuń stare plany dla tego dnia i użytkownika
            int deletedPlans = db.delete(TABLE_TRAINING_PLAN,
                    COLUMN_PLAN_USER_ID + "=? AND " + COLUMN_PLAN_DAY_NAME + "=?",
                    new String[]{String.valueOf(userId), dayName});
            Log.d("DEBUG_PLAN", "Usunięto " + deletedPlans + " stare plany dla userId=" + userId + " i dnia=" + dayName);

            // 🟢 Dodaj nowy plan
            ContentValues planValues = new ContentValues();
            planValues.put(COLUMN_PLAN_USER_ID, userId);
            planValues.put(COLUMN_PLAN_DAY_NAME, dayName);
            planValues.put(COLUMN_PLAN_VALID_FROM, planValidFrom);

            planId = db.insert(TABLE_TRAINING_PLAN, null, planValues);
            Log.d("DEBUG_PLAN", "Dodano nowy planId=" + planId + " dla dnia=" + dayName + ", valid_from=" + planValidFrom);

            // 🟢 Dodaj wszystkie ćwiczenia do planu
            if (planId != -1) {
                for (Exercise ex : exerciseList) {
                    ContentValues exValues = new ContentValues();
                    exValues.put(COLUMN_PLAN_ID, planId);
                    exValues.put(COLUMN_PLAN_EXERCISE_NAME, ex.getName());
                    exValues.put(COLUMN_PLAN_SERIES_COUNT, ex.getSeriesList().size());

                    long inserted = db.insert(TABLE_PLAN_EXERCISE, null, exValues);
                    if (inserted == -1) {
                        planId = -1;
                        Log.e("DEBUG_PLAN", "Błąd podczas dodawania ćwiczenia: " + ex.getName());
                        break;
                    }
                    Log.d("DEBUG_PLAN", "  + dodano ćwiczenie: " + ex.getName() + " (serii=" + ex.getSeriesList().size() + ")");
                }
            }

            if (planId != -1) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Błąd przy zapisywaniu planu: " + e.getMessage());
            planId = -1;
        } finally {
            db.endTransaction();
            db.close();
        }

        return planId;
    }



    public boolean trainingLogExists(int userId, String date, String dayName) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.query(TABLE_TRAINING_LOG, new String[]{COLUMN_LOG_ID},
                    COLUMN_LOG_USER_ID + "=? AND " + COLUMN_LOG_DATE + "=? AND " + COLUMN_LOG_DAY_NAME + "=?",
                    new String[]{String.valueOf(userId), date, dayName},
                    null, null, null);
            boolean exists = cursor.moveToFirst();
            cursor.close();
            return exists;
        } finally {
            db.close();
        }
    }

    public boolean createEmptyTrainingLogFromPlan(int userId, String dayName, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        boolean success = false;
        try {
            Cursor planCursor = db.query(TABLE_TRAINING_PLAN, new String[]{COLUMN_PLAN_ID},
                    COLUMN_PLAN_USER_ID + "=? AND " + COLUMN_PLAN_DAY_NAME + "=?",
                    new String[]{String.valueOf(userId), dayName}, null, null, null);

            if (!planCursor.moveToFirst()) {
                planCursor.close();
                return false;
            }
            long planId = planCursor.getLong(planCursor.getColumnIndexOrThrow(COLUMN_PLAN_ID));
            planCursor.close();

            ContentValues logValues = new ContentValues();
            logValues.put(COLUMN_LOG_USER_ID, userId);
            logValues.put(COLUMN_LOG_DATE, date);
            logValues.put(COLUMN_LOG_DAY_NAME, dayName);
            long logId = db.insert(TABLE_TRAINING_LOG, null, logValues);

            if (logId == -1) return false;

            Cursor exCursor = db.query(TABLE_PLAN_EXERCISE,
                    new String[]{COLUMN_PLAN_EXERCISE_NAME, COLUMN_PLAN_SERIES_COUNT},
                    COLUMN_PLAN_ID + "=?", new String[]{String.valueOf(planId)},
                    null, null, null);

            while (exCursor.moveToNext()) {
                String exerciseName = exCursor.getString(exCursor.getColumnIndexOrThrow(COLUMN_PLAN_EXERCISE_NAME));
                int seriesCount = exCursor.getInt(exCursor.getColumnIndexOrThrow(COLUMN_PLAN_SERIES_COUNT));

                ContentValues logExValues = new ContentValues();
                logExValues.put(COLUMN_LOG_ID, logId);
                logExValues.put(COLUMN_PLAN_EXERCISE_NAME, exerciseName);
                long logExerciseId = db.insert(TABLE_LOG_EXERCISE, null, logExValues);

                if (logExerciseId == -1) {
                    exCursor.close();
                    return false;
                }

                for (int i = 0; i < seriesCount; i++) {
                    ContentValues s = new ContentValues();
                    s.put(COLUMN_LOG_EXERCISE_ID, logExerciseId);
                    s.put(COLUMN_LOG_REPS, 0);
                    s.put(COLUMN_LOG_WEIGHT, 0.0f);
                    if (db.insert(TABLE_LOG_SERIES, null, s) == -1) {
                        exCursor.close();
                        return false;
                    }
                }
            }
            exCursor.close();
            db.setTransactionSuccessful();
            success = true;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error creating empty training log from plan: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
            db.close();
        }
        return success;
    }

    public ArrayList<Exercise> getLogExercises(int userId, String date, String dayName) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Exercise> list = new ArrayList<>();
        long logId = -1;

        try {
            Cursor logCursor = db.query(TABLE_TRAINING_LOG, new String[]{COLUMN_LOG_ID},
                    COLUMN_LOG_USER_ID + "=? AND " + COLUMN_LOG_DATE + "=? AND " + COLUMN_LOG_DAY_NAME + "=?",
                    new String[]{String.valueOf(userId), date, dayName}, null, null, null);

            if (logCursor.moveToFirst()) {
                logId = logCursor.getLong(logCursor.getColumnIndexOrThrow(COLUMN_LOG_ID));
            }
            logCursor.close();

            if (logId == -1) {
                return list;
            }

            Cursor exCursor = db.query(TABLE_LOG_EXERCISE,
                    new String[]{COLUMN_LOG_EXERCISE_ID, COLUMN_PLAN_EXERCISE_NAME},
                    COLUMN_LOG_ID + "=?", new String[]{String.valueOf(logId)}, null, null, null);

            while (exCursor.moveToNext()) {
                long logExerciseId = exCursor.getLong(exCursor.getColumnIndexOrThrow(COLUMN_LOG_EXERCISE_ID));
                String name = exCursor.getString(exCursor.getColumnIndexOrThrow(COLUMN_PLAN_EXERCISE_NAME));
                ArrayList<Series> seriesList = new ArrayList<>();

                Cursor sCursor = db.query(TABLE_LOG_SERIES,
                        new String[]{COLUMN_LOG_REPS, COLUMN_LOG_WEIGHT},
                        COLUMN_LOG_EXERCISE_ID + "=?", new String[]{String.valueOf(logExerciseId)},
                        null, null, null);
                while (sCursor.moveToNext()) {
                    int reps = sCursor.getInt(sCursor.getColumnIndexOrThrow(COLUMN_LOG_REPS));
                    float weight = sCursor.getFloat(sCursor.getColumnIndexOrThrow(COLUMN_LOG_WEIGHT));
                    seriesList.add(new Series(reps, weight));
                }
                sCursor.close();
                list.add(new Exercise(name, seriesList));
            }
            exCursor.close();
            return list;
        } finally {
            db.close();
        }
    }

    public boolean saveLogSeries(int userId, String date, String dayName, List<Exercise> exercises) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        boolean success = false;
        try {
            long logId;
            Cursor c = db.query(TABLE_TRAINING_LOG, new String[]{COLUMN_LOG_ID},
                    COLUMN_LOG_USER_ID + "=? AND " + COLUMN_LOG_DATE + "=? AND " + COLUMN_LOG_DAY_NAME + "=?",
                    new String[]{String.valueOf(userId), date, dayName},
                    null, null, null);

            if (c.moveToFirst()) {
                logId = c.getLong(c.getColumnIndexOrThrow(COLUMN_LOG_ID));
            } else {
                ContentValues v = new ContentValues();
                v.put(COLUMN_LOG_USER_ID, userId);
                v.put(COLUMN_LOG_DATE, date);
                v.put(COLUMN_LOG_DAY_NAME, dayName);
                logId = db.insert(TABLE_TRAINING_LOG, null, v);
                if (logId == -1) {
                    c.close();
                    return false;
                }
            }
            c.close();

            Cursor exCur = db.query(TABLE_LOG_EXERCISE,
                    new String[]{COLUMN_LOG_EXERCISE_ID, COLUMN_PLAN_EXERCISE_NAME},
                    COLUMN_LOG_ID + "=?",
                    new String[]{String.valueOf(logId)},
                    null, null, null);

            List<Long> exercisesToDelete = new ArrayList<>();
            while (exCur.moveToNext()) {
                long currentLogExerciseId = exCur.getLong(exCur.getColumnIndexOrThrow(COLUMN_LOG_EXERCISE_ID));
                exercisesToDelete.add(currentLogExerciseId);
            }
            exCur.close();

            for (Long logExerciseId : exercisesToDelete) {
                db.delete(TABLE_LOG_SERIES, COLUMN_LOG_EXERCISE_ID + "=?",
                        new String[]{String.valueOf(logExerciseId)});
                db.delete(TABLE_LOG_EXERCISE, COLUMN_LOG_EXERCISE_ID + "=?",
                        new String[]{String.valueOf(logExerciseId)});
            }

            for (Exercise ex : exercises) {
                ContentValues ev = new ContentValues();
                ev.put(COLUMN_LOG_ID, logId);
                ev.put(COLUMN_PLAN_EXERCISE_NAME, ex.getName());
                long logExerciseId = db.insert(TABLE_LOG_EXERCISE, null, ev);
                if (logExerciseId == -1) {
                    return false;
                }

                for (Series s : ex.getSeriesList()) {
                    ContentValues sv = new ContentValues();
                    sv.put(COLUMN_LOG_EXERCISE_ID, logExerciseId);
                    sv.put(COLUMN_LOG_REPS, s.getReps());
                    sv.put(COLUMN_LOG_WEIGHT, s.getWeight());
                    if (db.insert(TABLE_LOG_SERIES, null, sv) == -1) {
                        return false;
                    }
                }
            }
            db.setTransactionSuccessful();
            success = true;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error saving log series: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
            db.close();
        }
        return success;
    }

    public long getLogId(int userId, String date, String dayName) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.query(TABLE_TRAINING_LOG, new String[]{COLUMN_LOG_ID},
                    COLUMN_LOG_USER_ID + "=? AND " + COLUMN_LOG_DATE + "=? AND " + COLUMN_LOG_DAY_NAME + "=?",
                    new String[]{String.valueOf(userId), date, dayName}, null, null, null);
            long logId = -1;
            if (cursor.moveToFirst()) {
                logId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LOG_ID));
            }
            cursor.close();
            return logId;
        } finally {
            db.close();
        }
    }

    public void deleteLogExercise(long logId, String exerciseName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            Cursor cursor = db.query(TABLE_LOG_EXERCISE, new String[]{COLUMN_LOG_EXERCISE_ID},
                    COLUMN_LOG_ID + "=? AND " + COLUMN_PLAN_EXERCISE_NAME + "=?",
                    new String[]{String.valueOf(logId), exerciseName}, null, null, null);

            if (cursor.moveToFirst()) {
                long logExerciseId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LOG_EXERCISE_ID));
                db.delete(TABLE_LOG_SERIES, COLUMN_LOG_EXERCISE_ID + "=?",
                        new String[]{String.valueOf(logExerciseId)});
                db.delete(TABLE_LOG_EXERCISE, COLUMN_LOG_EXERCISE_ID + "=?",
                        new String[]{String.valueOf(logExerciseId)});
            }
            cursor.close();
            if (countExercisesInLog(logId) == 0) {
                db.delete(TABLE_TRAINING_LOG, COLUMN_LOG_ID + "=?", new String[]{String.valueOf(logId)});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error deleting log exercise: " + e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public long createEmptyTrainingLog(int userId, String dayName, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues logVals = new ContentValues();
            logVals.put(COLUMN_LOG_USER_ID, userId);
            logVals.put(COLUMN_LOG_DATE, date);
            logVals.put(COLUMN_LOG_DAY_NAME, dayName);
            long result = db.insert(TABLE_TRAINING_LOG, null, logVals);
            return result;
        } finally {
            db.close();
        }
    }

    private int countExercisesInLog(long logId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.rawQuery(
                    "SELECT COUNT(*) FROM " + TABLE_LOG_EXERCISE + " WHERE " + COLUMN_LOG_ID + "=?",
                    new String[]{String.valueOf(logId)});
            int cnt = 0;
            if (c.moveToFirst()) {
                cnt = c.getInt(0);
            }
            c.close();
            return cnt;
        } finally {
            db.close();
        }
    }

    public void deleteEmptyLogIfNeeded(long logId) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            if (countExercisesInLog(logId) == 0) {
                db.delete(TABLE_TRAINING_LOG, COLUMN_LOG_ID + "=?", new String[]{String.valueOf(logId)});
            }
        } finally {
            db.close();
        }
    }

    public String getUsername(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String username = null;
            Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME},
                    COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor.moveToFirst()) {
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
            }
            cursor.close();
            return username;
        } finally {
            db.close();
        }
    }

    public float[] getInitialProfileData(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            float[] data = new float[]{0f, 0f};
            Cursor cursor = db.query(TABLE_PROFILE, new String[]{COLUMN_WEIGHT, COLUMN_ARM_CIRC},
                    COLUMN_PROFILE_USER_ID + "=?", new String[]{String.valueOf(userId)},
                    null, null, "IFNULL(" + COLUMN_DATE + ", '1970-01-01') ASC", "1");
            if (cursor.moveToFirst()) {
                data[0] = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT));
                data[1] = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_ARM_CIRC));
                Log.d("DatabaseHelper", "Initial Weight for user " + userId + ": " + data[0]);
            } else {
                Log.d("DatabaseHelper", "No initial profile entry for user " + userId);
            }
            cursor.close();
            return data;
        } finally {
            db.close();
        }
    }

    public float getLatestBenchPress(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            float maxWeight = 0f;
            String query = "SELECT MAX(" + COLUMN_LOG_WEIGHT + ") as max_weight " +
                    "FROM " + TABLE_LOG_SERIES + " ls " +
                    "JOIN " + TABLE_LOG_EXERCISE + " le ON ls." + COLUMN_LOG_EXERCISE_ID + " = le." + COLUMN_LOG_EXERCISE_ID + " " +
                    "JOIN " + TABLE_TRAINING_LOG + " tl ON le." + COLUMN_LOG_ID + " = tl." + COLUMN_LOG_ID + " " +
                    "WHERE tl." + COLUMN_LOG_USER_ID + " = ? AND le." + COLUMN_PLAN_EXERCISE_NAME + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), "Wyciskanie sztangi"});
            if (cursor.moveToFirst()) {
                maxWeight = cursor.getFloat(cursor.getColumnIndexOrThrow("max_weight"));
                Log.d("DatabaseHelper", "Latest Bench Press for user " + userId + ": " + maxWeight);
            } else {
                Log.d("DatabaseHelper", "No bench press logs for user " + userId);
            }
            cursor.close();
            return maxWeight;
        } finally {
            db.close();
        }
    }

    public float getInitialBenchPress(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            float firstWeight = 0f;
            String query = "SELECT ls." + COLUMN_LOG_WEIGHT + " as weight " +
                    "FROM " + TABLE_LOG_SERIES + " ls " +
                    "JOIN " + TABLE_LOG_EXERCISE + " le ON ls." + COLUMN_LOG_EXERCISE_ID + " = le." + COLUMN_LOG_EXERCISE_ID + " " +
                    "JOIN " + TABLE_TRAINING_LOG + " tl ON le." + COLUMN_LOG_ID + " = tl." + COLUMN_LOG_ID + " " +
                    "WHERE tl." + COLUMN_LOG_USER_ID + " = ? AND le." + COLUMN_PLAN_EXERCISE_NAME + " = ? " +
                    "ORDER BY tl." + COLUMN_LOG_DATE + " ASC LIMIT 1";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), "Wyciskanie sztangi"});
            if (cursor.moveToFirst()) {
                firstWeight = cursor.getFloat(cursor.getColumnIndexOrThrow("weight"));
                Log.d("DatabaseHelper", "Initial Bench Press for user " + userId + ": " + firstWeight);
            } else {
                Log.d("DatabaseHelper", "No initial bench press log for user " + userId);
            }
            cursor.close();
            return firstWeight;
        } finally {
            db.close();
        }
    }

    public void debugDatabase(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.query(TABLE_USER_GOALS, null, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
            Log.d("DatabaseHelper", "user_goals entries for user " + userId + ": " + cursor.getCount());
            while (cursor.moveToNext()) {
                Log.d("DatabaseHelper", "user_goals: start_weight=" + cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_START_WEIGHT)) +
                        ", target_weight=" + cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_TARGET_WEIGHT)) +
                        ", target_training_days=" + cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TARGET_TRAINING_DAYS)));
            }
            cursor.close();

            cursor = db.query(TABLE_PROFILE, null, COLUMN_PROFILE_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
            Log.d("DatabaseHelper", "profile entries for user " + userId + ": " + cursor.getCount());
            while (cursor.moveToNext()) {
                Log.d("DatabaseHelper", "profile: weight=" + cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)) +
                        ", arm_circ=" + cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_ARM_CIRC)) +
                        ", waist_circ=" + cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WAIST_CIRC)) +
                        ", hip_circ=" + cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HIP_CIRC)) +
                        ", height=" + cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)) +
                        ", date=" + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
            }
            cursor.close();

            String query = "SELECT tl." + COLUMN_LOG_DATE + ", ls." + COLUMN_LOG_WEIGHT +
                    " FROM " + TABLE_LOG_SERIES + " ls " +
                    "JOIN " + TABLE_LOG_EXERCISE + " le ON ls." + COLUMN_LOG_EXERCISE_ID + " = le." + COLUMN_LOG_EXERCISE_ID + " " +
                    "JOIN " + TABLE_TRAINING_LOG + " tl ON le." + COLUMN_LOG_ID + " = tl." + COLUMN_LOG_ID + " " +
                    "WHERE tl." + COLUMN_LOG_USER_ID + " = ? AND le." + COLUMN_PLAN_EXERCISE_NAME + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId), "Wyciskanie sztangi"});
            Log.d("DatabaseHelper", "bench press entries for user " + userId + ": " + cursor.getCount());
            while (cursor.moveToNext()) {
                Log.d("DatabaseHelper", "bench press: date=" + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOG_DATE)) +
                        ", weight=" + cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_LOG_WEIGHT)));
            }
            cursor.close();

            cursor = db.query(TABLE_TRAINING_LOG, new String[]{COLUMN_LOG_DATE},
                    COLUMN_LOG_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
            Log.d("DatabaseHelper", "training_log entries for user " + userId + ": " + cursor.getCount());
            while (cursor.moveToNext()) {
                Log.d("DatabaseHelper", "training_log: date=" + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOG_DATE)));
            }
            cursor.close();
        } finally {
            db.close();
        }
    }

    public boolean saveProfile(int userId, String gender, float height, float armCirc, float waistCirc, float hipCirc, float weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PROFILE_USER_ID, userId);
            values.put(COLUMN_GENDER, gender);
            values.put(COLUMN_HEIGHT, height);
            values.put(COLUMN_ARM_CIRC, armCirc);
            values.put(COLUMN_WAIST_CIRC, waistCirc);
            values.put(COLUMN_HIP_CIRC, hipCirc);
            values.put(COLUMN_WEIGHT, weight);
            values.put(COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()));

            long result = db.insert(TABLE_PROFILE, null, values);
            if (result == -1) {
                Log.e("DB_ERROR", "Failed to insert profile: userId=" + userId + ", height=" + height +
                        ", armCirc=" + armCirc + ", waistCirc=" + waistCirc + ", hipCirc=" + hipCirc + ", weight=" + weight);
                return false;
            }
            Log.d("DB_PROFILE", "Inserted profile: userId=" + userId + ", height=" + height +
                    ", armCirc=" + armCirc + ", waistCirc=" + waistCirc + ", hipCirc=" + hipCirc + ", weight=" + weight);
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error inserting profile: " + e.getMessage(), e);
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // New method to update existing profile data
    public boolean updateProfile(int userId, String gender, float height, float armCirc, float waistCirc, float hipCirc, float weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_GENDER, gender);
            values.put(COLUMN_HEIGHT, height);
            values.put(COLUMN_ARM_CIRC, armCirc);
            values.put(COLUMN_WAIST_CIRC, waistCirc);
            values.put(COLUMN_HIP_CIRC, hipCirc);
            values.put(COLUMN_WEIGHT, weight);
            values.put(COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()));

            // Check if a profile exists for the user
            Cursor cursor = db.query(TABLE_PROFILE, new String[]{COLUMN_PROFILE_USER_ID},
                    COLUMN_PROFILE_USER_ID + "=?", new String[]{String.valueOf(userId)},
                    null, null, null);
            boolean profileExists = cursor.getCount() > 0;
            cursor.close();

            int result;
            if (profileExists) {
                // Update existing profile
                result = db.update(TABLE_PROFILE, values,
                        COLUMN_PROFILE_USER_ID + "=?", new String[]{String.valueOf(userId)});
            } else {
                // Insert new profile if none exists
                values.put(COLUMN_PROFILE_USER_ID, userId);
                result = (int) db.insert(TABLE_PROFILE, null, values);
            }

            if (result == -1 || (profileExists && result == 0)) {
                Log.e("DB_ERROR", "Failed to update profile: userId=" + userId + ", height=" + height +
                        ", armCirc=" + armCirc + ", waistCirc=" + waistCirc + ", hipCirc=" + hipCirc + ", weight=" + weight);
                return false;
            }
            Log.d("DB_PROFILE", "Updated profile: userId=" + userId + ", height=" + height +
                    ", armCirc=" + armCirc + ", waistCirc=" + waistCirc + ", hipCirc=" + hipCirc + ", weight=" + weight);
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error updating profile: " + e.getMessage(), e);
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean saveUserGoals(int userId, ContentValues values, SQLiteDatabase db) {
        try {
            db.delete(TABLE_USER_GOALS, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
            values.put(COLUMN_USER_ID, userId);
            long result = db.insert(TABLE_USER_GOALS, null, values);
            if (result == -1) {
                Log.e("DB_ERROR", "Failed to save user goals for userId=" + userId + ", values=" + values.toString());
                return false;
            }
            Log.d("DatabaseHelper", "Saved user goals: userId=" + userId + ", values=" + values.toString());
            return true;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error saving user goals: " + e.getMessage(), e);
            return false;
        }
    }

    public float[] getLatestProfileData(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        float[] data = new float[]{0f, 0f, 0f, 0f, 0f};
        try {
            Cursor cursor = db.query(TABLE_PROFILE,
                    new String[]{COLUMN_WEIGHT, COLUMN_ARM_CIRC, COLUMN_WAIST_CIRC, COLUMN_HIP_CIRC, COLUMN_HEIGHT, COLUMN_DATE},
                    COLUMN_PROFILE_USER_ID + "=?", new String[]{String.valueOf(userId)},
                    null, null, COLUMN_DATE + " DESC", "1");
            if (cursor.moveToFirst()) {
                data[0] = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT));
                data[1] = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_ARM_CIRC));
                data[2] = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WAIST_CIRC));
                data[3] = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HIP_CIRC));
                data[4] = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                Log.d("DatabaseHelper", "Latest Profile for user " + userId + ": weight=" + data[0] +
                        ", arm=" + data[1] + ", waist=" + data[2] + ", hip=" + data[3] + ", height=" + data[4] + ", date=" + date);
            } else {
                Log.d("DatabaseHelper", "No latest profile entry for user " + userId);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching latest profile data: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        return data;
    }

    public int getActiveTrainingDaysCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        try {
            // 🔎 Oblicz daty początku i końca tygodnia
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

            Log.d("DEBUG", "Start of week: " + startDate);
            Log.d("DEBUG", "End of week: " + endDate);

            // 🔎 Sprawdź ile masz logów w tym tygodniu
            Cursor logCursor = db.rawQuery("SELECT log_id, date, day_name FROM " + TABLE_TRAINING_LOG +
                            " WHERE " + COLUMN_LOG_USER_ID + "=? AND date BETWEEN ? AND ?",
                    new String[]{String.valueOf(userId), startDate, endDate});

            Log.d("DEBUG", "Found " + logCursor.getCount() + " logs in training_log for this week.");

            while (logCursor.moveToNext()) {
                long logId = logCursor.getLong(logCursor.getColumnIndexOrThrow(COLUMN_LOG_ID));
                String date = logCursor.getString(logCursor.getColumnIndexOrThrow(COLUMN_LOG_DATE));
                String dayName = logCursor.getString(logCursor.getColumnIndexOrThrow(COLUMN_LOG_DAY_NAME));
                Log.d("DEBUG", "Checking logId=" + logId + ", date=" + date + ", dayName=" + dayName);

                // 🔎 Sprawdź, czy ten log ma ćwiczenia
                Cursor exerciseCursor = db.rawQuery("SELECT COUNT(*) as cnt FROM " + TABLE_LOG_EXERCISE +
                                " WHERE " + COLUMN_LOG_ID + "=?",
                        new String[]{String.valueOf(logId)});
                if (exerciseCursor.moveToFirst()) {
                    int exerciseCount = exerciseCursor.getInt(exerciseCursor.getColumnIndexOrThrow("cnt"));
                    Log.d("DEBUG", "  -> Exercises count: " + exerciseCount);

                    // 🔴 Tu jest warunek, czy liczymy ten dzień
                    if (exerciseCount > 0) {
                        Log.d("DEBUG", "  -> This day will be counted!");
                        count++;
                    } else {
                        Log.d("DEBUG", "  -> No exercises, skipping this day.");
                    }
                }
                exerciseCursor.close();
            }
            logCursor.close();

            Log.d("DEBUG", "Total active training days this week: " + count);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error counting active training days: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        return count;
    }


    public void clearTestData(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_PROFILE, COLUMN_PROFILE_USER_ID + "=?", new String[]{String.valueOf(userId)});
            db.delete(TABLE_USER_GOALS, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
            db.delete(TABLE_TRAINING_LOG, COLUMN_LOG_USER_ID + "=?", new String[]{String.valueOf(userId)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error clearing test data: " + e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void debugProfileTableSchema() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("PRAGMA table_info(" + TABLE_PROFILE + ")", null);
            Log.d("DatabaseHelper", "Profile table schema:");
            while (cursor.moveToNext()) {
                String columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String columnType = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                Log.d("DatabaseHelper", "Column: " + columnName + ", Type: " + columnType);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking profile schema: " + e.getMessage(), e);
        } finally {
            db.close();
        }
    }

    public void getProfileEntries(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.query(TABLE_PROFILE,
                    new String[]{COLUMN_WEIGHT, COLUMN_ARM_CIRC, COLUMN_WAIST_CIRC, COLUMN_HIP_CIRC, COLUMN_HEIGHT, COLUMN_DATE},
                    COLUMN_PROFILE_USER_ID + "=?", new String[]{String.valueOf(userId)},
                    null, null, COLUMN_DATE + " DESC");
            Log.d("DatabaseHelper", "Profile entries for user " + userId + ": " + cursor.getCount());
            while (cursor.moveToNext()) {
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT));
                float armCirc = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_ARM_CIRC));
                float waistCirc = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WAIST_CIRC));
                float hipCirc = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HIP_CIRC));
                float height = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                Log.d("DatabaseHelper", "Profile entry: date=" + date + ", weight=" + weight +
                        ", armCirc=" + armCirc + ", waistCirc=" + waistCirc + ", hipCirc=" + hipCirc + ", height=" + height);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching profile entries: " + e.getMessage(), e);
        } finally {
            db.close();
        }
    }

    public int getTargetTrainingDaysFromPlan(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT COUNT(DISTINCT " + COLUMN_PLAN_DAY_NAME + ") as count " +
                            "FROM " + TABLE_TRAINING_PLAN + " WHERE " + COLUMN_PLAN_USER_ID + "=?",
                    new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
            }
            cursor.close();
            Log.d("DatabaseHelper", "Target training days from plan for user " + userId + ": " + count);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error counting target training days: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        return count;
    }


    //historia pomiarów

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }


    public void insertBodyStatHistory(int userId, float weight, float armCirc, float waistCirc, float hipCirc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HISTORY_USER_ID, userId);
        values.put(COLUMN_HISTORY_DATE, getCurrentDate());
        values.put(COLUMN_HISTORY_WEIGHT, weight);
        values.put(COLUMN_HISTORY_ARM_CIRC, armCirc);
        values.put(COLUMN_HISTORY_WAIST_CIRC, waistCirc);
        values.put(COLUMN_HISTORY_HIP_CIRC, hipCirc);
        db.insert(TABLE_BODY_STAT_HISTORY, null, values);
    }

    public static class BodyStatEntry {
        public String date;
        public float weight;
        public float armCirc;
        public float waistCirc;
        public float hipCirc;

        public BodyStatEntry(String date, float weight, float armCirc, float waistCirc, float hipCirc) {
            this.date = date;
            this.weight = weight;
            this.armCirc = armCirc;
            this.waistCirc = waistCirc;
            this.hipCirc = hipCirc;
        }
    }


    public List<BodyStatEntry> getBodyStatHistory(int userId) {
        List<BodyStatEntry> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " +
                        COLUMN_HISTORY_DATE + ", " +
                        COLUMN_HISTORY_WEIGHT + ", " +
                        COLUMN_HISTORY_ARM_CIRC + ", " +
                        COLUMN_HISTORY_WAIST_CIRC + ", " +
                        COLUMN_HISTORY_HIP_CIRC +
                        " FROM " + TABLE_BODY_STAT_HISTORY +
                        " WHERE " + COLUMN_HISTORY_USER_ID + " = ?" +
                        " ORDER BY " + COLUMN_HISTORY_DATE + " ASC",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(0);
                float weight = cursor.getFloat(1);
                float arm = cursor.getFloat(2);
                float waist = cursor.getFloat(3);
                float hip = cursor.getFloat(4);
                historyList.add(new BodyStatEntry(date, weight, arm, waist, hip));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("DBHelper", "Loaded " + historyList.size() + " body stat entries for user " + userId);

        return historyList;
    }

    public float getBestLoggedWeightForExercise(int userId, String exerciseName) {
        SQLiteDatabase db = this.getReadableDatabase();
        float maxWeight = 0f;
        try {
            String query = "SELECT MAX(" + COLUMN_LOG_WEIGHT + ") as max_weight " +
                    "FROM " + TABLE_LOG_SERIES + " ls " +
                    "JOIN " + TABLE_LOG_EXERCISE + " le ON ls." + COLUMN_LOG_EXERCISE_ID + " = le." + COLUMN_LOG_EXERCISE_ID + " " +
                    "JOIN " + TABLE_TRAINING_LOG + " tl ON le." + COLUMN_LOG_ID + " = tl." + COLUMN_LOG_ID + " " +
                    "WHERE tl." + COLUMN_LOG_USER_ID + " = ? AND le." + COLUMN_PLAN_EXERCISE_NAME + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), exerciseName});
            if (cursor.moveToFirst()) {
                maxWeight = cursor.getFloat(cursor.getColumnIndexOrThrow("max_weight"));
                Log.d("DatabaseHelper", "Max weight for " + exerciseName + " (user " + userId + "): " + maxWeight);
            } else {
                Log.d("DatabaseHelper", "No logs found for " + exerciseName + " and user " + userId);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching best weight for " + exerciseName + ": " + e.getMessage());
        } finally {
            db.close();
        }
        return maxWeight;
    }

    public List<Entry> getExerciseProgressEntries(int userId, String exerciseName) {
        List<Entry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT tl." + COLUMN_LOG_DATE + " AS date, MAX(ls." + COLUMN_LOG_WEIGHT + ") as max_weight " +
                "FROM " + TABLE_LOG_SERIES + " ls " +
                "JOIN " + TABLE_LOG_EXERCISE + " le ON ls." + COLUMN_LOG_EXERCISE_ID + " = le." + COLUMN_LOG_EXERCISE_ID + " " +
                "JOIN " + TABLE_TRAINING_LOG + " tl ON le." + COLUMN_LOG_ID + " = tl." + COLUMN_LOG_ID + " " +
                "WHERE tl." + COLUMN_LOG_USER_ID + " = ? AND le." + COLUMN_PLAN_EXERCISE_NAME + " = ? AND ls." + COLUMN_LOG_WEIGHT + " > 0 " +
                "GROUP BY tl." + COLUMN_LOG_DATE + " ORDER BY tl." + COLUMN_LOG_DATE + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), exerciseName});
        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow("max_weight"));
                entries.add(new Entry(index, weight));
                index++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }


    public long getActivePlanIdForDay(int userId, String dayName, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        long planId = -1;
        try {
            // Szukamy planu, który jest ważny od daty <= date
            Cursor cursor = db.rawQuery(
                    "SELECT " + COLUMN_PLAN_ID + " FROM " + TABLE_TRAINING_PLAN +
                            " WHERE " + COLUMN_PLAN_USER_ID + " = ? AND " + COLUMN_PLAN_DAY_NAME + " = ? " +
                            "AND date(" + COLUMN_PLAN_VALID_FROM + ") <= date(?) " +
                            "ORDER BY date(" + COLUMN_PLAN_VALID_FROM + ") DESC LIMIT 1",
                    new String[]{String.valueOf(userId), dayName, date});

            if (cursor.moveToFirst()) {
                planId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PLAN_ID));
            }
            cursor.close();

            // 🚀 Kluczowe: sprawdzamy, czy w tym dniu są ćwiczenia w planie (czyli plan jest aktywny)
            if (planId != -1) {
                Cursor exCursor = db.query(TABLE_PLAN_EXERCISE,
                        new String[]{COLUMN_PLAN_EXERCISE_ID},
                        COLUMN_PLAN_ID + "=?",
                        new String[]{String.valueOf(planId)},
                        null, null, null);

                // Jeśli nie ma ćwiczeń w planie (plan pusty) – to nie ma sensu go pokazywać
                if (exCursor.getCount() == 0) {
                    planId = -1;
                }
                exCursor.close();
            }
        } finally {
            db.close();
        }
        return planId;
    }



    public boolean isLogConsistentWithPlan(long logId, long planId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM " + TABLE_LOG_EXERCISE + " le " +
                            "LEFT JOIN " + TABLE_PLAN_EXERCISE + " pe ON le." + COLUMN_PLAN_EXERCISE_NAME + " = pe." + COLUMN_PLAN_EXERCISE_NAME +
                            " WHERE le." + COLUMN_LOG_ID + "=? AND pe." + COLUMN_PLAN_ID + "!=?",
                    new String[]{String.valueOf(logId), String.valueOf(planId)}
            );
            boolean inconsistent = false;
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                inconsistent = count > 0;
            }
            cursor.close();
            return !inconsistent;
        } finally {
            db.close();
        }
    }

    public void deleteLog(long logId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_LOG_SERIES, COLUMN_LOG_EXERCISE_ID + " IN (SELECT " + COLUMN_LOG_EXERCISE_ID + " FROM " + TABLE_LOG_EXERCISE + " WHERE " + COLUMN_LOG_ID + "=?)",
                    new String[]{String.valueOf(logId)});
            db.delete(TABLE_LOG_EXERCISE, COLUMN_LOG_ID + "=?", new String[]{String.valueOf(logId)});
            db.delete(TABLE_TRAINING_LOG, COLUMN_LOG_ID + "=?", new String[]{String.valueOf(logId)});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }










}