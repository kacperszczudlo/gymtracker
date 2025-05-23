package com.example.gymtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList; // Added import

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GymTracker.db";
    private static final int DATABASE_VERSION = 1;

    // Tabela użytkowników
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_SURNAME = "surname";

    // Tabela profilu
    private static final String TABLE_PROFILE = "profile";
    private static final String COLUMN_PROFILE_USER_ID = "user_id";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_HEIGHT = "height";
    private static final String COLUMN_ARM_CIRC = "arm_circumference";
    private static final String COLUMN_WAIST_CIRC = "waist_circumference";
    private static final String COLUMN_HIP_CIRC = "hip_circumference";
    private static final String COLUMN_WEIGHT = "weight";

    // Tabela ćwiczeń
    private static final String TABLE_EXERCISES = "exercises";
    private static final String COLUMN_EXERCISE_ID = "exercise_id";
    private static final String COLUMN_EXERCISE_NAME = "name";

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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tworzenie tabeli użytkowników
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_SURNAME + " TEXT)";
        db.execSQL(createUsersTable);

        // Tworzenie tabeli profilu
        String createProfileTable = "CREATE TABLE " + TABLE_PROFILE + " (" +
                COLUMN_PROFILE_USER_ID + " INTEGER, " +
                COLUMN_GENDER + " TEXT, " +
                COLUMN_HEIGHT + " REAL, " +
                COLUMN_ARM_CIRC + " REAL, " +
                COLUMN_WAIST_CIRC + " REAL, " +
                COLUMN_HIP_CIRC + " REAL, " +
                COLUMN_WEIGHT + " REAL, " +
                "FOREIGN KEY(" + COLUMN_PROFILE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createProfileTable);

        // Tworzenie tabeli ćwiczeń
        String createExercisesTable = "CREATE TABLE " + TABLE_EXERCISES + " (" +
                COLUMN_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXERCISE_NAME + " TEXT)";
        db.execSQL(createExercisesTable);

        // Tworzenie tabeli dni treningowych
        String createTrainingDaysTable = "CREATE TABLE " + TABLE_TRAINING_DAYS + " (" +
                COLUMN_DAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY_NAME + " TEXT, " +
                COLUMN_DAY_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_DAY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createTrainingDaysTable);

        // Tworzenie tabeli serii dla ćwiczeń
        String createDayExercisesTable = "CREATE TABLE " + TABLE_DAY_EXERCISES + " (" +
                COLUMN_DAY_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY_EXERCISE_DAY_ID + " INTEGER, " +
                COLUMN_DAY_EXERCISE_NAME + " TEXT, " +
                COLUMN_DAY_EXERCISE_REPS + " INTEGER, " +
                COLUMN_DAY_EXERCISE_WEIGHT + " REAL, " +
                "FOREIGN KEY(" + COLUMN_DAY_EXERCISE_DAY_ID + ") REFERENCES " + TABLE_TRAINING_DAYS + "(" + COLUMN_DAY_ID + "))";
        db.execSQL(createDayExercisesTable);

        // Dodanie domyślnego konta admin
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USERNAME, "admin");
        adminValues.put(COLUMN_PASSWORD, "admin");
        adminValues.put(COLUMN_EMAIL, "admin@example.com");
        adminValues.put(COLUMN_SURNAME, "Admin");
        db.insert(TABLE_USERS, null, adminValues);

        // Dodanie przykładowych ćwiczeń
        String[] exercises = {"Przysiady", "Wyciskanie sztangi", "Martwy ciąg", "Pompki", "Podciąganie"};
        for (String exercise : exercises) {
            ContentValues exerciseValues = new ContentValues();
            exerciseValues.put(COLUMN_EXERCISE_NAME, exercise);
            db.insert(TABLE_EXERCISES, null, exerciseValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING_DAYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAY_EXERCISES);
        onCreate(db);
    }

    // Metoda logowania
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // nowe logowanie z emailem -N
    public boolean checkUserByEmail(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }


    // Rejestracja użytkownika
    public boolean registerUser(String username, String password, String email, String surname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_SURNAME, surname);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Zapis profilu
    public boolean saveProfile(int userId, String gender, float height, float armCirc, float waistCirc, float hipCirc, float weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_USER_ID, userId);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_ARM_CIRC, armCirc);
        values.put(COLUMN_WAIST_CIRC, waistCirc);
        values.put(COLUMN_HIP_CIRC, hipCirc);
        values.put(COLUMN_WEIGHT, weight);
        long result = db.insert(TABLE_PROFILE, null, values);
        return result != -1;
    }

    // Pobieranie ćwiczeń
    public Cursor getExercises() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_EXERCISES, new String[]{COLUMN_EXERCISE_ID, COLUMN_EXERCISE_NAME},
                null, null, null, null, null);
    }

    // Zapis dni treningowych
    public long saveTrainingDay(int userId, String dayName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_USER_ID, userId);
        values.put(COLUMN_DAY_NAME, dayName);
        return db.insert(TABLE_TRAINING_DAYS, null, values);
    }

    // Pobieranie ID dnia na podstawie nazwy i ID użytkownika
    public long getTrainingDayId(int userId, String dayName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRAINING_DAYS,
                new String[]{COLUMN_DAY_ID},
                COLUMN_DAY_USER_ID + "=? AND " + COLUMN_DAY_NAME + "=?",
                new String[]{String.valueOf(userId), dayName},
                null, null, null);
        if (cursor.moveToFirst()) {
            long dayId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DAY_ID));
            cursor.close();
            return dayId;
        }
        cursor.close();
        return -1;
    }

    // Zapis serii dla ćwiczenia w danym dniu
    public boolean saveDayExercise(long dayId, Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = true;
        for (Series series : exercise.getSeriesList()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DAY_EXERCISE_DAY_ID, dayId);
            values.put(COLUMN_DAY_EXERCISE_NAME, exercise.getName());
            values.put(COLUMN_DAY_EXERCISE_REPS, series.getReps());
            values.put(COLUMN_DAY_EXERCISE_WEIGHT, series.getWeight());
            long result = db.insert(TABLE_DAY_EXERCISES, null, values);
            if (result == -1) {
                success = false;
            }
        }
        return success;
    }

    // Pobieranie ćwiczeń z seriami dla danego dnia
    public ArrayList<Exercise> getDayExercises(long dayId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Exercise> exerciseList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_DAY_EXERCISES,
                new String[]{COLUMN_DAY_EXERCISE_NAME, COLUMN_DAY_EXERCISE_REPS, COLUMN_DAY_EXERCISE_WEIGHT},
                COLUMN_DAY_EXERCISE_DAY_ID + "=?",
                new String[]{String.valueOf(dayId)},
                null, null, null);

        String currentExerciseName = null;
        ArrayList<Series> seriesList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String exerciseName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY_EXERCISE_NAME));
            int reps = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DAY_EXERCISE_REPS));
            float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_DAY_EXERCISE_WEIGHT));

            if (!exerciseName.equals(currentExerciseName) && currentExerciseName != null) {
                exerciseList.add(new Exercise(currentExerciseName, new ArrayList<>(seriesList)));
                seriesList.clear();
            }
            seriesList.add(new Series(reps, weight));
            currentExerciseName = exerciseName;
        }
        // Dodaj ostatnie ćwiczenie, jeśli istnieje
        if (currentExerciseName != null) {
            exerciseList.add(new Exercise(currentExerciseName, new ArrayList<>(seriesList)));
        }
        cursor.close();
        return exerciseList;
    }

    // Usuwanie serii dla ćwiczenia w danym dniu
    public boolean deleteDayExercise(long dayId, String exerciseName, int seriesIndex) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Pobierz wszystkie serie dla danego ćwiczenia
        Cursor cursor = db.query(TABLE_DAY_EXERCISES,
                new String[]{COLUMN_DAY_EXERCISE_ID},
                COLUMN_DAY_EXERCISE_DAY_ID + "=? AND " + COLUMN_DAY_EXERCISE_NAME + "=?",
                new String[]{String.valueOf(dayId), exerciseName},
                null, null, null);
        int count = 0;
        if (cursor.moveToPosition(seriesIndex)) {
            long seriesId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DAY_EXERCISE_ID));
            count = db.delete(TABLE_DAY_EXERCISES,
                    COLUMN_DAY_EXERCISE_ID + "=?",
                    new String[]{String.valueOf(seriesId)});
        }
        cursor.close();
        return count > 0;
    }

    // Usuwanie wszystkich serii dla danego dnia
    public boolean deleteDayExercises(long dayId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_DAY_EXERCISES, COLUMN_DAY_EXERCISE_DAY_ID + "=?", new String[]{String.valueOf(dayId)});
        return result > 0;
    }

    public boolean checkIfEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


}