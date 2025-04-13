package com.example.gymtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GymTrackerDB";
    private static final int DATABASE_VERSION = 3; // Incremented to 3

    // Tabela exercises
    private static final String TABLE_EXERCISES = "exercises";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    // Tabela users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Tabela training_days
    private static final String TABLE_TRAINING_DAYS = "training_days";
    private static final String COLUMN_DAY = "day";

    // Nowa tabela training_day_exercises
    private static final String TABLE_TRAINING_DAY_EXERCISES = "training_day_exercises";
    private static final String COLUMN_TRAINING_DAY_ID = "training_day_id";
    private static final String COLUMN_EXERCISE_NAME = "exercise_name";
    private static final String COLUMN_SETS = "sets";
    private static final String COLUMN_REPS = "reps";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tworzenie tabeli exercises
        String createExercisesTable = "CREATE TABLE " + TABLE_EXERCISES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT)";
        db.execSQL(createExercisesTable);

        // Tworzenie tabeli users
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        // Tworzenie tabeli training_days
        String createTrainingDaysTable = "CREATE TABLE " + TABLE_TRAINING_DAYS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY + " TEXT UNIQUE)";
        db.execSQL(createTrainingDaysTable);

        // Tworzenie tabeli training_day_exercises
        String createTrainingDayExercisesTable = "CREATE TABLE " + TABLE_TRAINING_DAY_EXERCISES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TRAINING_DAY_ID + " INTEGER, " +
                COLUMN_EXERCISE_NAME + " TEXT, " +
                COLUMN_SETS + " INTEGER, " +
                COLUMN_REPS + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_TRAINING_DAY_ID + ") REFERENCES " + TABLE_TRAINING_DAYS + "(" + COLUMN_ID + "))";
        db.execSQL(createTrainingDayExercisesTable);

        // Dodanie przykładowych ćwiczeń
        insertExercise(db, "Wyciskanie sztangi na ławce");
        insertExercise(db, "Martwy ciąg");
        insertExercise(db, "Przysiady ze sztangą");
        insertExercise(db, "Podciąganie na drążku");
        insertExercise(db, "Wiosłowanie sztangą");

        // Dodanie domyślnego konta admin
        insertUser(db, "admin", "admin");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING_DAY_EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING_DAYS);
        onCreate(db);
    }

    // Metoda do dodawania ćwiczenia
    private void insertExercise(SQLiteDatabase db, String name) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        db.insert(TABLE_EXERCISES, null, values);
    }

    // Metoda do dodawania użytkownika
    private void insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        db.insert(TABLE_USERS, null, values);
    }

    // Pobierz listę wszystkich ćwiczeń
    public ArrayList<String> getAllExercises() {
        ArrayList<String> exercises = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EXERCISES, null);

        if (cursor.moveToFirst()) {
            do {
                exercises.add(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return exercises;
    }

    // Rejestracja nowego użytkownika
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Sprawdzenie, czy użytkownik istnieje i hasło jest poprawne
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{username, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Sprawdzenie, czy nazwa użytkownika już istnieje
    public boolean usernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USERNAME + " = ?", new String[]{username});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Zapisanie ćwiczeń dla wybranego dnia
    public boolean saveTrainingDay(String day, ArrayList<String> exercises) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the training_days table exists
        Cursor tableCheck = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{TABLE_TRAINING_DAYS});
        boolean tableExists = tableCheck.getCount() > 0;
        tableCheck.close();

        if (!tableExists) {
            String createTrainingDaysTable = "CREATE TABLE " + TABLE_TRAINING_DAYS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DAY + " TEXT UNIQUE)";
            db.execSQL(createTrainingDaysTable);
        }

        // Check if the training_day_exercises table exists
        tableCheck = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{TABLE_TRAINING_DAY_EXERCISES});
        tableExists = tableCheck.getCount() > 0;
        tableCheck.close();

        if (!tableExists) {
            String createTrainingDayExercisesTable = "CREATE TABLE " + TABLE_TRAINING_DAY_EXERCISES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TRAINING_DAY_ID + " INTEGER, " +
                    COLUMN_EXERCISE_NAME + " TEXT, " +
                    COLUMN_SETS + " INTEGER, " +
                    COLUMN_REPS + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_TRAINING_DAY_ID + ") REFERENCES " + TABLE_TRAINING_DAYS + "(" + COLUMN_ID + "))";
            db.execSQL(createTrainingDayExercisesTable);
        }

        long trainingDayId = -1;
        try {
            // Check if the day already exists
            Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_TRAINING_DAYS + " WHERE " +
                    COLUMN_DAY + " = ?", new String[]{day});
            if (cursor.moveToFirst()) {
                trainingDayId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
            }
            cursor.close();

            if (trainingDayId == -1) {
                // Insert new training day
                ContentValues dayValues = new ContentValues();
                dayValues.put(COLUMN_DAY, day);
                trainingDayId = db.insert(TABLE_TRAINING_DAYS, null, dayValues);
            } else {
                // Delete existing exercises for this day
                db.delete(TABLE_TRAINING_DAY_EXERCISES, COLUMN_TRAINING_DAY_ID + " = ?", new String[]{String.valueOf(trainingDayId)});
            }

            // Insert exercises with default sets and reps
            for (String exercise : exercises) {
                ContentValues exerciseValues = new ContentValues();
                exerciseValues.put(COLUMN_TRAINING_DAY_ID, trainingDayId);
                exerciseValues.put(COLUMN_EXERCISE_NAME, exercise);
                exerciseValues.put(COLUMN_SETS, 3); // Default sets
                exerciseValues.put(COLUMN_REPS, 8); // Default reps
                db.insert(TABLE_TRAINING_DAY_EXERCISES, null, exerciseValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }

        return trainingDayId != -1;
    }

    // Pobierz ćwiczenia dla wybranego dnia
    public List<Exercise> getExercisesForDay(String day) {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // First, get the training day ID
        long trainingDayId = -1;
        Cursor dayCursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_TRAINING_DAYS + " WHERE " +
                COLUMN_DAY + " = ?", new String[]{day});
        if (dayCursor.moveToFirst()) {
            trainingDayId = dayCursor.getLong(dayCursor.getColumnIndex(COLUMN_ID));
        }
        dayCursor.close();

        if (trainingDayId != -1) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRAINING_DAY_EXERCISES + " WHERE " +
                    COLUMN_TRAINING_DAY_ID + " = ?", new String[]{String.valueOf(trainingDayId)});
            if (cursor.moveToFirst()) {
                do {
                    String exerciseName = cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISE_NAME));
                    int sets = cursor.getInt(cursor.getColumnIndex(COLUMN_SETS));
                    int reps = cursor.getInt(cursor.getColumnIndex(COLUMN_REPS));
                    Exercise exercise = new Exercise(exerciseName, reps, sets); // Note: Using weight as sets for now
                    exercises.add(exercise);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();
        return exercises;
    }

    // Aktualizuj ćwiczenie (serie i powtórzenia)
    public boolean updateExercise(long trainingDayId, String exerciseName, int sets, int reps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SETS, sets);
        values.put(COLUMN_REPS, reps);

        long result = db.update(TABLE_TRAINING_DAY_EXERCISES, values,
                COLUMN_TRAINING_DAY_ID + " = ? AND " + COLUMN_EXERCISE_NAME + " = ?",
                new String[]{String.valueOf(trainingDayId), exerciseName});
        db.close();
        return result != -1;
    }

    // Pobierz wszystkie dni treningowe z ćwiczeniami
    public List<TrainingDay> getAllTrainingDays() {
        List<TrainingDay> trainingDays = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor dayCursor = db.rawQuery("SELECT * FROM " + TABLE_TRAINING_DAYS, null);
        if (dayCursor.moveToFirst()) {
            do {
                String day = dayCursor.getString(dayCursor.getColumnIndex(COLUMN_DAY));
                long trainingDayId = dayCursor.getLong(dayCursor.getColumnIndex(COLUMN_ID));
                List<Exercise> exercises = new ArrayList<>();

                Cursor exerciseCursor = db.rawQuery("SELECT * FROM " + TABLE_TRAINING_DAY_EXERCISES + " WHERE " +
                        COLUMN_TRAINING_DAY_ID + " = ?", new String[]{String.valueOf(trainingDayId)});
                if (exerciseCursor.moveToFirst()) {
                    do {
                        String exerciseName = exerciseCursor.getString(exerciseCursor.getColumnIndex(COLUMN_EXERCISE_NAME));
                        int sets = exerciseCursor.getInt(exerciseCursor.getColumnIndex(COLUMN_SETS));
                        int reps = exerciseCursor.getInt(exerciseCursor.getColumnIndex(COLUMN_REPS));
                        exercises.add(new Exercise(exerciseName, reps, sets));
                    } while (exerciseCursor.moveToNext());
                }
                exerciseCursor.close();

                trainingDays.add(new TrainingDay(day, exercises));
            } while (dayCursor.moveToNext());
        }
        dayCursor.close();
        db.close();
        return trainingDays;
    }

    // Pobierz ID treningu dla danego dnia
    public long getTrainingDayId(String day) {
        SQLiteDatabase db = this.getReadableDatabase();
        long trainingDayId = -1;
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_TRAINING_DAYS + " WHERE " +
                COLUMN_DAY + " = ?", new String[]{day});
        if (cursor.moveToFirst()) {
            trainingDayId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
        }
        cursor.close();
        db.close();
        return trainingDayId;
    }
}