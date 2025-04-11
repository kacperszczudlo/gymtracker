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
    private static final int DATABASE_VERSION = 2; // Incremented to 2

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
    private static final String COLUMN_EXERCISES = "exercises";

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
                COLUMN_DAY + " TEXT UNIQUE, " +
                COLUMN_EXERCISES + " TEXT)";
        db.execSQL(createTrainingDaysTable);

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
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

        // Check if the table exists
        Cursor tableCheck = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{TABLE_TRAINING_DAYS});
        boolean tableExists = tableCheck.getCount() > 0;
        tableCheck.close();

        if (!tableExists) {
            String createTrainingDaysTable = "CREATE TABLE " + TABLE_TRAINING_DAYS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DAY + " TEXT UNIQUE, " +
                    COLUMN_EXERCISES + " TEXT)";
            db.execSQL(createTrainingDaysTable);
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY, day);
        values.put(COLUMN_EXERCISES, String.join(",", exercises));

        long result = -1;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRAINING_DAYS + " WHERE " +
                    COLUMN_DAY + " = ?", new String[]{day});
            boolean dayExists = cursor.getCount() > 0;
            cursor.close();

            if (dayExists) {
                result = db.update(TABLE_TRAINING_DAYS, values, COLUMN_DAY + " = ?", new String[]{day});
            } else {
                result = db.insert(TABLE_TRAINING_DAYS, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }

        return result != -1;
    }

    // Pobierz ćwiczenia dla wybranego dnia
    public ArrayList<String> getExercisesForDay(String day) {
        ArrayList<String> exercises = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRAINING_DAYS + " WHERE " +
                COLUMN_DAY + " = ?", new String[]{day});

        if (cursor.moveToFirst()) {
            String exercisesString = cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISES));
            if (exercisesString != null && !exercisesString.isEmpty()) {
                String[] exercisesArray = exercisesString.split(",");
                for (String exercise : exercisesArray) {
                    exercises.add(exercise.trim());
                }
            }
        }

        cursor.close();
        db.close();
        return exercises;
    }

    // Pobierz wszystkie dni treningowe z ćwiczeniami
    public List<TrainingDay> getAllTrainingDays() {
        List<TrainingDay> trainingDays = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRAINING_DAYS, null);

        if (cursor.moveToFirst()) {
            do {
                String day = cursor.getString(cursor.getColumnIndex(COLUMN_DAY));
                String exercisesString = cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISES));
                List<Exercise> exercises = new ArrayList<>();
                if (exercisesString != null && !exercisesString.isEmpty()) {
                    String[] exercisesArray = exercisesString.split(",");
                    for (int i = 0; i < exercisesArray.length; i++) {
                        exercises.add(new Exercise(exercisesArray[i].trim(), 0, 0));
                    }
                }
                trainingDays.add(new TrainingDay(day, exercises));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return trainingDays;
    }
}