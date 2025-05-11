package com.example.gymtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList; // Added import
import java.util.HashSet;
import java.util.List;

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

    //tabele logów

    // Tabele planów
    private static final String TABLE_TRAINING_PLAN = "training_plan";
    private static final String TABLE_PLAN_EXERCISE = "plan_exercise";

    private static final String COLUMN_PLAN_ID = "plan_id";
    private static final String COLUMN_PLAN_USER_ID = "user_id";
    private static final String COLUMN_PLAN_DAY_NAME = "day_name";
    private static final String COLUMN_PLAN_EXERCISE_ID = "plan_exercise_id";
    private static final String COLUMN_PLAN_EXERCISE_NAME = "exercise_name";
    private static final String COLUMN_PLAN_SERIES_COUNT = "series_count";

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





        // Tabela planów treningowych
        db.execSQL("CREATE TABLE training_plan (" +
                "plan_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "day_name TEXT NOT NULL)");

        db.execSQL("CREATE TABLE plan_exercise (" +
                "plan_exercise_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "plan_id INTEGER NOT NULL," +
                "exercise_name TEXT NOT NULL," +
                "series_count INTEGER NOT NULL," +
                "FOREIGN KEY (plan_id) REFERENCES training_plan(plan_id))");

// Tabela dziennika wykonanych treningów
        db.execSQL("CREATE TABLE training_log (" +
                "log_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "date TEXT NOT NULL," +
                "day_name TEXT NOT NULL)");

        db.execSQL("CREATE TABLE log_exercise (" +
                "log_exercise_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "log_id INTEGER NOT NULL," +
                "exercise_name TEXT NOT NULL," +
                "FOREIGN KEY (log_id) REFERENCES training_log(log_id))");

        db.execSQL("CREATE TABLE log_series (" +
                "log_series_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "log_exercise_id INTEGER NOT NULL," +
                "reps INTEGER," +
                "weight REAL," +
                "FOREIGN KEY (log_exercise_id) REFERENCES log_exercise(log_exercise_id))");





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

    public long saveTrainingPlan(int userId, String dayName, List<Exercise> exerciseList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues planValues = new ContentValues();
        planValues.put("user_id", userId);
        planValues.put("day_name", dayName);
        // usuń stary plan dla tego usera i dnia (jeśli istniał)
        db.delete("plan_exercise",
                "plan_id IN (SELECT plan_id FROM training_plan WHERE user_id=? AND day_name=?)",
                new String[]{String.valueOf(userId), dayName});
        db.delete("training_plan",
                "user_id=? AND day_name=?",
                new String[]{String.valueOf(userId), dayName});

        long planId = db.insert("training_plan", null, planValues);

        //  👇  TU wklej log
        Log.d("DEBUG_PLAN", "insert -> planId=" + planId
                + ", userId=" + userId + ", day=" + dayName);

        if (planId == -1) return -1;

        for (Exercise ex : exerciseList) {
            ContentValues exValues = new ContentValues();
            exValues.put("plan_id", planId);
            exValues.put("exercise_name", ex.getName());
            exValues.put("series_count", ex.getSeriesList().size());

            long rowId = db.insert("plan_exercise", null, exValues);

            //  👇  pomocniczo możesz też logować każdą wstawkę do plan_exercise
            Log.d("DEBUG_PLAN", "  + exercise '" + ex.getName()
                    + "' (series=" + ex.getSeriesList().size()
                    + ") rowId=" + rowId);
        }

        // jeśli istnieje pusty log dla "dziś" – usuń go, aby odtworzyć wg nowego planu
        db.delete("training_log",
                "user_id=? AND date=date('now','localtime') AND day_name=?",
                new String[]{String.valueOf(userId), dayName});

        return planId;
    }


    public boolean trainingLogExists(int userId, String date, String dayName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("training_log", new String[]{"log_id"},
                "user_id=? AND date=? AND day_name=?",
                new String[]{String.valueOf(userId), date, dayName},
                null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }


    public boolean createEmptyTrainingLogFromPlan(int userId, String dayName, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor planCursor = db.query("training_plan", new String[]{"plan_id"},
                "user_id=? AND day_name=?",
                new String[]{String.valueOf(userId), dayName}, null, null, null);
        if (!planCursor.moveToFirst()) return false;
        long planId = planCursor.getLong(planCursor.getColumnIndexOrThrow("plan_id"));
        planCursor.close();

        ContentValues logValues = new ContentValues();
        logValues.put("user_id", userId);
        logValues.put("date", date);
        logValues.put("day_name", dayName);
        long logId = db.insert("training_log", null, logValues);
        if (logId == -1) return false;

        Cursor exCursor = db.query("plan_exercise",
                new String[]{"exercise_name", "series_count"},
                "plan_id=?", new String[]{String.valueOf(planId)},
                null, null, null);

        while (exCursor.moveToNext()) {
            String exerciseName = exCursor.getString(0);
            int seriesCount = exCursor.getInt(1);

            ContentValues logExValues = new ContentValues();
            logExValues.put("log_id", logId);
            logExValues.put("exercise_name", exerciseName);
            long logExerciseId = db.insert("log_exercise", null, logExValues);

            for (int i = 0; i < seriesCount; i++) {
                ContentValues s = new ContentValues();
                s.put("log_exercise_id", logExerciseId);
                s.put("reps", 0);
                s.put("weight", 0.0f);
                db.insert("log_series", null, s);
            }
        }
        exCursor.close();
        return true;
    }

    public ArrayList<Exercise> getLogExercises(int userId, String date, String dayName) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Exercise> list = new ArrayList<>();
        Cursor logCursor = db.query("training_log", new String[]{"log_id"},
                "user_id=? AND date=? AND day_name=?",
                new String[]{String.valueOf(userId), date, dayName}, null, null, null);
        if (!logCursor.moveToFirst()) return list;
        long logId = logCursor.getLong(0);
        logCursor.close();

        Cursor exCursor = db.query("log_exercise",
                new String[]{"log_exercise_id", "exercise_name"},
                "log_id=?", new String[]{String.valueOf(logId)}, null, null, null);

        while (exCursor.moveToNext()) {
            long logExerciseId = exCursor.getLong(0);
            String name = exCursor.getString(1);
            ArrayList<Series> seriesList = new ArrayList<>();

            Cursor sCursor = db.query("log_series",
                    new String[]{"reps", "weight"},
                    "log_exercise_id=?", new String[]{String.valueOf(logExerciseId)},
                    null, null, null);
            while (sCursor.moveToNext()) {
                int reps = sCursor.getInt(0);
                float weight = sCursor.getFloat(1);
                seriesList.add(new Series(reps, weight));
            }
            sCursor.close();
            list.add(new Exercise(name, seriesList));
        }
        exCursor.close();
        return list;
    }

    /**
     * Zapisuje (lub nadpisuje) wszystkie serie z ekranu edycji treningu.
     * Jeżeli log (training_log) dla danego dnia jeszcze nie istnieje – tworzy go.
     *
     * @return true jeżeli operacja przebiegła bez błędów.
     */
    public boolean saveLogSeries(int userId,
                                 String date,
                                 String dayName,
                                 List<Exercise> exercises) {

        SQLiteDatabase db = this.getWritableDatabase();

        /*──────────────────── 1. zapewnij istnienie LOGU ────────────────────*/
        long logId;
        Cursor c = db.query("training_log", new String[]{"log_id"},
                "user_id=? AND date=? AND day_name=?",
                new String[]{String.valueOf(userId), date, dayName},
                null, null, null);

        if (c.moveToFirst()) {
            logId = c.getLong(0);
        } else {                                    // ⇢ brak logu – tworzymy
            ContentValues v = new ContentValues();
            v.put("user_id", userId);
            v.put("date",    date);
            v.put("day_name",dayName);
            logId = db.insert("training_log", null, v);
            if (logId == -1) { c.close(); return false; }
        }
        c.close();

        /*──────────────────── 2. usuń ćwiczenia, których NIE ma w liście ────*/
        HashSet<String> newNames = new HashSet<>();
        for (Exercise ex : exercises) newNames.add(ex.getName());

        Cursor exCur = db.query("log_exercise",
                new String[]{"log_exercise_id", "exercise_name"},
                "log_id=?",
                new String[]{String.valueOf(logId)},
                null, null, null);

        while (exCur.moveToNext()) {
            long   logExId = exCur.getLong(0);
            String name    = exCur.getString(1);

            if (!newNames.contains(name)) {
                db.delete("log_series",   "log_exercise_id=?",
                        new String[]{String.valueOf(logExId)});
                db.delete("log_exercise", "log_exercise_id=?",
                        new String[]{String.valueOf(logExId)});
            }
        }
        exCur.close();

        /*──────────────────── 3. up-sert aktualną listę ćwiczeń ─────────────*/
        for (Exercise ex : exercises) {

            // znajdź (lub wstaw) wiersz log_exercise
            Cursor find = db.query("log_exercise", new String[]{"log_exercise_id"},
                    "log_id=? AND exercise_name=?",
                    new String[]{String.valueOf(logId), ex.getName()},
                    null, null, null);

            long logExerciseId;
            if (find.moveToFirst()) {                   // ⇢ był – czyścimy serie
                logExerciseId = find.getLong(0);
                db.delete("log_series", "log_exercise_id=?",
                        new String[]{String.valueOf(logExerciseId)});
            } else {                                   // ⇢ nie było – INSERT
                ContentValues ev = new ContentValues();
                ev.put("log_id", logId);
                ev.put("exercise_name", ex.getName());
                logExerciseId = db.insert("log_exercise", null, ev);
            }
            find.close();

            // wstaw serie
            for (Series s : ex.getSeriesList()) {
                ContentValues sv = new ContentValues();
                sv.put("log_exercise_id", logExerciseId);
                sv.put("reps",   s.getReps());
                sv.put("weight", s.getWeight());
                db.insert("log_series", null, sv);
            }
        }
        return true;
    }



    public long getLogId(int userId, String date, String dayName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("training_log", new String[]{"log_id"},
                "user_id=? AND date=? AND day_name=?",
                new String[]{String.valueOf(userId), date, dayName}, null, null, null);
        if (cursor.moveToFirst()) {
            long logId = cursor.getLong(0);
            cursor.close();
            return logId;
        }
        cursor.close();
        db.close();
        return -1;
    }

    public void deleteLogExercise(long logId, String exerciseName) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query("log_exercise", new String[]{"log_exercise_id"},
                "log_id=? AND exercise_name=?",
                new String[]{String.valueOf(logId), exerciseName}, null, null, null);

        if (cursor.moveToFirst()) {
            long logExerciseId = cursor.getLong(0);
            // Usuń serie
            db.delete("log_series", "log_exercise_id=?", new String[]{String.valueOf(logExerciseId)});
            // Usuń ćwiczenie
            db.delete("log_exercise", "log_exercise_id=?", new String[]{String.valueOf(logExerciseId)});
        }
        // sprawdź, czy nie został pusty log
        purgeEmptyLog(logId);

        cursor.close();
    }

    /**  Tworzy pusty log (bez powiązania z planem)  */
    public long createEmptyTrainingLog(int userId, String dayName, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues logVals = new ContentValues();
        logVals.put("user_id",  userId);
        logVals.put("date",     date);
        logVals.put("day_name", dayName);

        return db.insert("training_log", null, logVals);   // -1 w razie błędu
    }

    /** Liczba ćwiczeń, które pozostały w logu */
    private int countExercisesInLog(long logId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM log_exercise WHERE log_id=?",
                new String[]{String.valueOf(logId)});
        int cnt = 0;
        if (c.moveToFirst()) cnt = c.getInt(0);
        c.close();
        return cnt;
    }

    /** Skasuj cały log, jeżeli nie ma już w nim ćwiczeń */
    private void purgeEmptyLog(long logId) {
        if (countExercisesInLog(logId) == 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("training_log", "log_id=?", new String[]{String.valueOf(logId)});
        }
    }

    public void deleteEmptyLogIfNeeded(long logId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM log_exercise WHERE log_id=?",
                new String[]{String.valueOf(logId)});
        if (c.moveToFirst() && c.getInt(0) == 0) {
            db.delete("training_log", "log_id=?", new String[]{String.valueOf(logId)});
        }
        c.close();
    }











}