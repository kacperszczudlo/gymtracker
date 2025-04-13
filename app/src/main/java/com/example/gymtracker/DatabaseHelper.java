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
    private static final int DATABASE_VERSION = 4; // Zwiększamy wersję, bo zmieniamy strukturę

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

    // Tabela training_day_exercises
    private static final String TABLE_TRAINING_DAY_EXERCISES = "training_day_exercises";
    private static final String COLUMN_TRAINING_DAY_ID = "training_day_id";
    private static final String COLUMN_EXERCISE_NAME = "exercise_name";

    // Nowa tabela training_day_exercise_sets
    private static final String TABLE_TRAINING_DAY_EXERCISE_SETS = "training_day_exercise_sets";
    private static final String COLUMN_EXERCISE_ID = "exercise_id";
    private static final String COLUMN_SETS = "sets";
    private static final String COLUMN_REPS = "reps";
    private static final String COLUMN_WEIGHT = "weight";

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
                "FOREIGN KEY(" + COLUMN_TRAINING_DAY_ID + ") REFERENCES " + TABLE_TRAINING_DAYS + "(" + COLUMN_ID + "))";
        db.execSQL(createTrainingDayExercisesTable);

        // Tworzenie tabeli training_day_exercise_sets
        String createTrainingDayExerciseSetsTable = "CREATE TABLE " + TABLE_TRAINING_DAY_EXERCISE_SETS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXERCISE_ID + " INTEGER, " +
                COLUMN_REPS + " INTEGER, " +
                COLUMN_WEIGHT + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_EXERCISE_ID + ") REFERENCES " + TABLE_TRAINING_DAY_EXERCISES + "(" + COLUMN_ID + "))";
        db.execSQL(createTrainingDayExerciseSetsTable);

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING_DAY_EXERCISE_SETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING_DAY_EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING_DAYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
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
    // Zapisanie ćwiczeń dla wybranego dnia
    public boolean saveTrainingDay(String day, ArrayList<String> exercises) {
        SQLiteDatabase db = this.getWritableDatabase();
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
            }

            // Insert exercises (without deleting existing ones)
            for (String exercise : exercises) {
                // Check if exercise already exists for this day
                Cursor exerciseCursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_TRAINING_DAY_EXERCISES + " WHERE " +
                                COLUMN_TRAINING_DAY_ID + " = ? AND " + COLUMN_EXERCISE_NAME + " = ?",
                        new String[]{String.valueOf(trainingDayId), exercise});
                long exerciseId = -1;
                if (exerciseCursor.moveToFirst()) {
                    exerciseId = exerciseCursor.getLong(exerciseCursor.getColumnIndex(COLUMN_ID));
                }
                exerciseCursor.close();

                if (exerciseId == -1) {
                    // Insert new exercise
                    ContentValues exerciseValues = new ContentValues();
                    exerciseValues.put(COLUMN_TRAINING_DAY_ID, trainingDayId);
                    exerciseValues.put(COLUMN_EXERCISE_NAME, exercise);
                    exerciseId = db.insert(TABLE_TRAINING_DAY_EXERCISES, null, exerciseValues);

                    // Add default series (e.g., 3 series with 8 reps each)
                    for (int i = 0; i < 3; i++) {
                        ContentValues setValues = new ContentValues();
                        setValues.put(COLUMN_EXERCISE_ID, exerciseId);
                        setValues.put(COLUMN_REPS, 8);
                        setValues.put(COLUMN_WEIGHT, 0);
                        db.insert(TABLE_TRAINING_DAY_EXERCISE_SETS, null, setValues);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }

        return true; // Zawsze zwracamy true, bo nawet jeśli coś się nie powiedzie, dzień zostanie utworzony
    }

    // Pobierz ćwiczenia dla wybranego dnia
    public List<Exercise> getExercisesForDay(String day) {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        if (day == null) {
            return exercises; // Zwróć pustą listę, jeśli dzień jest null
        }

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
                    long exerciseId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                    String exerciseName = cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISE_NAME));
                    Exercise exercise = new Exercise(exerciseId, exerciseName);

                    // Pobierz serie dla tego ćwiczenia
                    Cursor setCursor = db.rawQuery("SELECT * FROM " + TABLE_TRAINING_DAY_EXERCISE_SETS + " WHERE " +
                            COLUMN_EXERCISE_ID + " = ?", new String[]{String.valueOf(exerciseId)});
                    while (setCursor.moveToNext()) {
                        int reps = setCursor.getInt(setCursor.getColumnIndex(COLUMN_REPS));
                        int weight = setCursor.getInt(setCursor.getColumnIndex(COLUMN_WEIGHT));
                        exercise.addSeries(new Series(reps, weight));
                    }
                    setCursor.close();

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

        try {
            // Znajdź ID ćwiczenia
            long exerciseId = -1;
            Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_TRAINING_DAY_EXERCISES + " WHERE " +
                            COLUMN_TRAINING_DAY_ID + " = ? AND " + COLUMN_EXERCISE_NAME + " = ?",
                    new String[]{String.valueOf(trainingDayId), exerciseName});
            if (cursor.moveToFirst()) {
                exerciseId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
            }
            cursor.close();

            if (exerciseId == -1) {
                return false;
            }

            // Usuń istniejące serie
            db.delete(TABLE_TRAINING_DAY_EXERCISE_SETS, COLUMN_EXERCISE_ID + " = ?",
                    new String[]{String.valueOf(exerciseId)});

            // Dodaj nowe serie
            for (int i = 0; i < sets; i++) {
                ContentValues setValues = new ContentValues();
                setValues.put(COLUMN_EXERCISE_ID, exerciseId);
                setValues.put(COLUMN_REPS, reps);
                setValues.put(COLUMN_WEIGHT, 0); // Domyślny ciężar
                db.insert(TABLE_TRAINING_DAY_EXERCISE_SETS, null, setValues);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
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
                        long exerciseId = exerciseCursor.getLong(exerciseCursor.getColumnIndex(COLUMN_ID));
                        String exerciseName = exerciseCursor.getString(exerciseCursor.getColumnIndex(COLUMN_EXERCISE_NAME));
                        Exercise exercise = new Exercise(exerciseId, exerciseName);

                        // Pobierz serie
                        Cursor setCursor = db.rawQuery("SELECT * FROM " + TABLE_TRAINING_DAY_EXERCISE_SETS + " WHERE " +
                                COLUMN_EXERCISE_ID + " = ?", new String[]{String.valueOf(exerciseId)});
                        while (setCursor.moveToNext()) {
                            int reps = setCursor.getInt(setCursor.getColumnIndex(COLUMN_REPS));
                            int weight = setCursor.getInt(setCursor.getColumnIndex(COLUMN_WEIGHT));
                            exercise.addSeries(new Series(reps, weight));
                        }
                        setCursor.close();

                        exercises.add(exercise);
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