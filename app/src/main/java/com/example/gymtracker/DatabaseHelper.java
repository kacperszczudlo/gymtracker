package com.example.gymtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    // Nowa tabela: ćwiczenia przypisane do dni
    private static final String TABLE_DAY_EXERCISES = "day_exercises";
    private static final String COLUMN_DAY_EXERCISE_ID = "day_exercise_id";
    private static final String COLUMN_DAY_EXERCISE_DAY_ID = "day_id";
    private static final String COLUMN_DAY_EXERCISE_NAME = "exercise_name";
    private static final String COLUMN_DAY_EXERCISE_SETS = "sets";
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

        // Tworzenie tabeli ćwiczeń przypisanych do dni
        String createDayExercisesTable = "CREATE TABLE " + TABLE_DAY_EXERCISES + " (" +
                COLUMN_DAY_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY_EXERCISE_DAY_ID + " INTEGER, " +
                COLUMN_DAY_EXERCISE_NAME + " TEXT, " +
                COLUMN_DAY_EXERCISE_SETS + " INTEGER, " +
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

    // Zapis ćwiczenia dla danego dnia
    public boolean saveDayExercise(long dayId, Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_EXERCISE_DAY_ID, dayId);
        values.put(COLUMN_DAY_EXERCISE_NAME, exercise.getName());
        values.put(COLUMN_DAY_EXERCISE_SETS, exercise.getSets());
        values.put(COLUMN_DAY_EXERCISE_REPS, exercise.getReps());
        values.put(COLUMN_DAY_EXERCISE_WEIGHT, exercise.getWeight());
        long result = db.insert(TABLE_DAY_EXERCISES, null, values);
        return result != -1;
    }

    // Pobieranie ćwiczeń dla danego dnia
    public Cursor getDayExercises(long dayId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_DAY_EXERCISES,
                new String[]{COLUMN_DAY_EXERCISE_NAME, COLUMN_DAY_EXERCISE_SETS, COLUMN_DAY_EXERCISE_REPS, COLUMN_DAY_EXERCISE_WEIGHT},
                COLUMN_DAY_EXERCISE_DAY_ID + "=?",
                new String[]{String.valueOf(dayId)},
                null, null, null);
    }

    // Usuwanie ćwiczenia dla danego dnia
    public boolean deleteDayExercise(long dayId, String exerciseName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_DAY_EXERCISES,
                COLUMN_DAY_EXERCISE_DAY_ID + "=? AND " + COLUMN_DAY_EXERCISE_NAME + "=?",
                new String[]{String.valueOf(dayId), exerciseName});
        return result > 0;
    }

    // Usuwanie wszystkich ćwiczeń dla danego dnia
    public boolean deleteDayExercises(long dayId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_DAY_EXERCISES, COLUMN_DAY_EXERCISE_DAY_ID + "=?", new String[]{String.valueOf(dayId)});
        return result > 0;
    }

    // Usuwanie dnia treningowego
    public boolean deleteTrainingDay(long dayId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Najpierw usuń wszystkie ćwiczenia dla tego dnia
        db.delete(TABLE_DAY_EXERCISES, COLUMN_DAY_EXERCISE_DAY_ID + "=?", new String[]{String.valueOf(dayId)});
        // Następnie usuń dzień
        int result = db.delete(TABLE_TRAINING_DAYS, COLUMN_DAY_ID + "=?", new String[]{String.valueOf(dayId)});
        return result > 0;
    }
}