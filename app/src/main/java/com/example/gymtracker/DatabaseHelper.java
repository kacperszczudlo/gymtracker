package com.example.gymtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GymTrackerDB";
    private static final int DATABASE_VERSION = 1;

    // Tabela exercises
    private static final String TABLE_EXERCISES = "exercises";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    // Tabela users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

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
        return result != -1; // Zwraca true, jeśli rejestracja się powiodła
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
}