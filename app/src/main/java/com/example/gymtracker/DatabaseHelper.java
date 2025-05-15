package com.example.gymtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GymTracker.db";
    private static final int DATABASE_VERSION = 1; // Increment if schema changes

    // Tabela użytkowników
    public static final String TABLE_USERS = "users"; // Made public
    public static final String COLUMN_USER_ID = "user_id"; // Made public (if needed by other classes)
    public static final String COLUMN_USERNAME = "username"; // Made public (if needed by other classes)
    public static final String COLUMN_PASSWORD = "password"; // Made public (if needed by other classes)
    public static final String COLUMN_EMAIL = "email"; // Made public (if needed by other classes)
    public static final String COLUMN_SURNAME = "surname"; // Made public (if needed by other classes)

    // Tabela profilu
    public static final String TABLE_PROFILE = "profile"; // Made public
    public static final String COLUMN_PROFILE_USER_ID = "user_id"; // Made public (it's the foreign key)
    public static final String COLUMN_GENDER = "gender"; // Made public
    public static final String COLUMN_HEIGHT = "height"; // Made public
    public static final String COLUMN_ARM_CIRC = "arm_circumference"; // Made public
    public static final String COLUMN_WAIST_CIRC = "waist_circumference"; // Made public
    public static final String COLUMN_HIP_CIRC = "hip_circumference"; // Made public
    public static final String COLUMN_WEIGHT = "weight"; // Made public

    // Tabela celów użytkownika
    private static final String TABLE_USER_GOALS = "user_goals";
    // Assuming these are not directly accessed by UpdateMeasurementsActivity, keeping private
    private static final String COLUMN_TARGET_WEIGHT = "target_weight";
    private static final String COLUMN_START_WEIGHT = "start_weight";


    // Tabela ćwiczeń
    public static final String TABLE_EXERCISES = "exercises"; // Made public
    public static final String COLUMN_EXERCISE_ID = "exercise_id"; // Made public
    public static final String COLUMN_EXERCISE_NAME = "name"; // Made public


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
        // Note: COLUMN_PROFILE_USER_ID is the same as COLUMN_USER_ID in terms of name,
        // but it's good to have a distinct constant if their roles are conceptually different.
        String createProfileTable = "CREATE TABLE " + TABLE_PROFILE + " (" +
                COLUMN_PROFILE_USER_ID + " INTEGER, " + // This should reference users.user_id
                COLUMN_GENDER + " TEXT, " +
                COLUMN_HEIGHT + " REAL, " +
                COLUMN_ARM_CIRC + " REAL, " +
                COLUMN_WAIST_CIRC + " REAL, " +
                COLUMN_HIP_CIRC + " REAL, " +
                COLUMN_WEIGHT + " REAL, " +
                "FOREIGN KEY(" + COLUMN_PROFILE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createProfileTable);

        // Tworzenie tabeli celów użytkownika
        String createUserGoalsTable = "CREATE TABLE " + TABLE_USER_GOALS + " (" +
                COLUMN_USER_ID + " INTEGER, " + // This should reference users.user_id
                COLUMN_TARGET_WEIGHT + " REAL, " +
                COLUMN_START_WEIGHT + " REAL, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createUserGoalsTable);

        // Tworzenie tabeli ćwiczeń
        String createExercisesTable = "CREATE TABLE " + TABLE_EXERCISES + " (" +
                COLUMN_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXERCISE_NAME + " TEXT)";
        db.execSQL(createExercisesTable);

        // Tworzenie tabeli dni treningowych
        String createTrainingDaysTable = "CREATE TABLE " + TABLE_TRAINING_DAYS + " (" +
                COLUMN_DAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY_NAME + " TEXT, " +
                COLUMN_DAY_USER_ID + " INTEGER, " + // This should reference users.user_id
                "FOREIGN KEY(" + COLUMN_DAY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createTrainingDaysTable);

        // Tworzenie tabeli serii dla ćwiczeń
        String createDayExercisesTable = "CREATE TABLE " + TABLE_DAY_EXERCISES + " (" +
                COLUMN_DAY_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY_EXERCISE_DAY_ID + " INTEGER, " +
                COLUMN_DAY_EXERCISE_NAME + " TEXT, " + // Consider referencing exercises.exercise_id
                COLUMN_DAY_EXERCISE_REPS + " INTEGER, " +
                COLUMN_DAY_EXERCISE_WEIGHT + " REAL, " +
                "FOREIGN KEY(" + COLUMN_DAY_EXERCISE_DAY_ID + ") REFERENCES " + TABLE_TRAINING_DAYS + "(" + COLUMN_DAY_ID + "))";
        db.execSQL(createDayExercisesTable);

        // Tabela planów treningowych
        db.execSQL("CREATE TABLE " + TABLE_TRAINING_PLAN + " (" +
                COLUMN_PLAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PLAN_USER_ID + " INTEGER NOT NULL," + // This should reference users.user_id
                COLUMN_PLAN_DAY_NAME + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_PLAN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))");


        db.execSQL("CREATE TABLE " + TABLE_PLAN_EXERCISE + " (" +
                COLUMN_PLAN_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PLAN_ID + " INTEGER NOT NULL," +
                COLUMN_PLAN_EXERCISE_NAME + " TEXT NOT NULL," + // Consider referencing exercises.exercise_id
                COLUMN_PLAN_SERIES_COUNT + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + COLUMN_PLAN_ID + ") REFERENCES " + TABLE_TRAINING_PLAN + "(" + COLUMN_PLAN_ID + "))");

        // Tabela dziennika wykonanych treningów
        db.execSQL("CREATE TABLE " + TABLE_TRAINING_LOG + " (" +
                COLUMN_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_LOG_USER_ID + " INTEGER NOT NULL," + // This should reference users.user_id
                COLUMN_LOG_DATE + " TEXT NOT NULL," +
                COLUMN_LOG_DAY_NAME + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_LOG_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))");


        db.execSQL("CREATE TABLE " + TABLE_LOG_EXERCISE + " (" +
                COLUMN_LOG_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_LOG_ID + " INTEGER NOT NULL," +
                COLUMN_PLAN_EXERCISE_NAME + " TEXT NOT NULL," + // Re-used COLUMN_PLAN_EXERCISE_NAME, ensure this is intended or create specific one
                "FOREIGN KEY (" + COLUMN_LOG_ID + ") REFERENCES " + TABLE_TRAINING_LOG + "(" + COLUMN_LOG_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_LOG_SERIES + " (" +
                COLUMN_LOG_SERIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_LOG_EXERCISE_ID + " INTEGER NOT NULL," +
                COLUMN_LOG_REPS + " INTEGER," +
                COLUMN_LOG_WEIGHT + " REAL," +
                "FOREIGN KEY (" + COLUMN_LOG_EXERCISE_ID + ") REFERENCES " + TABLE_LOG_EXERCISE + "(" + COLUMN_LOG_EXERCISE_ID + "))");


        // Dodanie domyślnego konta admin
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USERNAME, "admin");
        adminValues.put(COLUMN_PASSWORD, "admin"); // Consider hashing passwords in a real app
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
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG_SERIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG_EXERCISE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAN_EXERCISE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING_PLAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAY_EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING_DAYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_GOALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // Create tables again
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
        db.close();
        return count > 0;
    }

    // Logowanie z emailem
    public boolean checkUserByEmail(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // Rejestracja użytkownika
    public boolean registerUser(String username, String password, String email, String surname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password); // Again, consider hashing
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_SURNAME, surname);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
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

        // Check if profile exists to update, otherwise insert
        Cursor cursor = db.query(TABLE_PROFILE, new String[]{COLUMN_PROFILE_USER_ID}, COLUMN_PROFILE_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
        long result;
        if (cursor.getCount() > 0) {
            // Profile exists, update it
            result = db.update(TABLE_PROFILE, values, COLUMN_PROFILE_USER_ID + "=?", new String[]{String.valueOf(userId)});
        } else {
            // Profile does not exist, insert new one
            result = db.insert(TABLE_PROFILE, null, values);
        }
        cursor.close();
        db.close();
        return result != -1 && result !=0; // For update, result is number of rows affected. For insert, it's row ID or -1.
    }


    // Pobieranie ćwiczeń
    public Cursor getExercises() {
        SQLiteDatabase db = this.getReadableDatabase();
        // db.close() should not be called here if cursor is returned and used outside
        return db.query(TABLE_EXERCISES, new String[]{COLUMN_EXERCISE_ID, COLUMN_EXERCISE_NAME},
                null, null, null, null, null);
    }

    // Zapis dni treningowych
    public long saveTrainingDay(int userId, String dayName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_USER_ID, userId);
        values.put(COLUMN_DAY_NAME, dayName);
        long result = db.insert(TABLE_TRAINING_DAYS, null, values);
        db.close();
        return result;
    }

    // Pobieranie ID dnia na podstawie nazwy i ID użytkownika
    public long getTrainingDayId(int userId, String dayName) {
        SQLiteDatabase db = this.getReadableDatabase();
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
        db.close();
        return dayId;
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
            if (db.insert(TABLE_DAY_EXERCISES, null, values) == -1) {
                success = false;
            }
        }
        db.close();
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
                null, null, COLUMN_DAY_EXERCISE_NAME); // Order by name to group series

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
        // Add the last exercise
        if (currentExerciseName != null && !seriesList.isEmpty()) {
            exerciseList.add(new Exercise(currentExerciseName, seriesList));
        }
        cursor.close();
        db.close();
        return exerciseList;
    }

    // Usuwanie serii dla ćwiczenia w danym dniu
    public boolean deleteDayExercise(long dayId, String exerciseName, int seriesIndex) {
        SQLiteDatabase db = this.getWritableDatabase();
        // This logic is a bit tricky as it relies on seriesIndex without a stable series ID.
        // A more robust way would be to have a primary key for each series entry.
        // For now, assuming the order is consistent.
        Cursor cursor = db.query(TABLE_DAY_EXERCISES,
                new String[]{COLUMN_DAY_EXERCISE_ID},
                COLUMN_DAY_EXERCISE_DAY_ID + "=? AND " + COLUMN_DAY_EXERCISE_NAME + "=?",
                new String[]{String.valueOf(dayId), exerciseName},
                null, null, null); // Potentially add an ORDER BY if series order matters for deletion by index

        int count = 0;
        if (cursor.moveToPosition(seriesIndex)) { // seriesIndex is 0-based
            long seriesIdToDelete = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DAY_EXERCISE_ID));
            count = db.delete(TABLE_DAY_EXERCISES,
                    COLUMN_DAY_EXERCISE_ID + "=?",
                    new String[]{String.valueOf(seriesIdToDelete)});
        }
        cursor.close();
        db.close();
        return count > 0;
    }

    // Usuwanie wszystkich serii dla danego dnia
    public boolean deleteDayExercises(long dayId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_DAY_EXERCISES, COLUMN_DAY_EXERCISE_DAY_ID + "=?", new String[]{String.valueOf(dayId)});
        db.close();
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
        db.close();
        return exists;
    }

    public long saveTrainingPlan(int userId, String dayName, List<Exercise> exerciseList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction(); // Use transactions for multiple operations
        long planId = -1;
        try {
            // Usuń stary plan dla tego usera i dnia (jeśli istniał)
            // First, get plan_id(s) to delete from plan_exercise
            Cursor oldPlanCursor = db.query(TABLE_TRAINING_PLAN, new String[]{COLUMN_PLAN_ID},
                    COLUMN_PLAN_USER_ID + "=? AND " + COLUMN_PLAN_DAY_NAME + "=?",
                    new String[]{String.valueOf(userId), dayName}, null, null, null);

            while(oldPlanCursor.moveToNext()){
                long oldPlanId = oldPlanCursor.getLong(oldPlanCursor.getColumnIndexOrThrow(COLUMN_PLAN_ID));
                db.delete(TABLE_PLAN_EXERCISE, COLUMN_PLAN_ID + "=?", new String[]{String.valueOf(oldPlanId)});
            }
            oldPlanCursor.close();

            db.delete(TABLE_TRAINING_PLAN,
                    COLUMN_PLAN_USER_ID + "=? AND " + COLUMN_PLAN_DAY_NAME + "=?",
                    new String[]{String.valueOf(userId), dayName});

            ContentValues planValues = new ContentValues();
            planValues.put(COLUMN_PLAN_USER_ID, userId);
            planValues.put(COLUMN_PLAN_DAY_NAME, dayName);
            planId = db.insert(TABLE_TRAINING_PLAN, null, planValues);

            Log.d("DEBUG_PLAN", "insert -> planId=" + planId
                    + ", userId=" + userId + ", day=" + dayName);

            if (planId != -1) {
                for (Exercise ex : exerciseList) {
                    ContentValues exValues = new ContentValues();
                    exValues.put(COLUMN_PLAN_ID, planId);
                    exValues.put(COLUMN_PLAN_EXERCISE_NAME, ex.getName());
                    exValues.put(COLUMN_PLAN_SERIES_COUNT, ex.getSeriesList().size());
                    if (db.insert(TABLE_PLAN_EXERCISE, null, exValues) == -1) {
                        planId = -1; // Mark as failed
                        break;
                    }
                    Log.d("DEBUG_PLAN", "  + exercise '" + ex.getName()
                            + "' (series=" + ex.getSeriesList().size() + ")");
                }
            }

            if (planId != -1) {
                // Jeśli istnieje pusty log dla "dziś" – usuń go, aby odtworzyć wg nowego planu
                // This might need more specific date handling if "today" is relative
                db.delete(TABLE_TRAINING_LOG,
                        COLUMN_LOG_USER_ID + "=? AND " + COLUMN_LOG_DATE + "=date('now','localtime') AND " + COLUMN_LOG_DAY_NAME + "=?",
                        new String[]{String.valueOf(userId), dayName});
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error saving training plan: " + e.getMessage());
            planId = -1;
        } finally {
            db.endTransaction();
            db.close();
        }
        return planId;
    }


    public boolean trainingLogExists(int userId, String date, String dayName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRAINING_LOG, new String[]{COLUMN_LOG_ID},
                COLUMN_LOG_USER_ID + "=? AND " + COLUMN_LOG_DATE + "=? AND " + COLUMN_LOG_DAY_NAME + "=?",
                new String[]{String.valueOf(userId), date, dayName},
                null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
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
                return false; // No plan found
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
                // Ensure you are using the correct column name for log_exercise table
                logExValues.put(COLUMN_PLAN_EXERCISE_NAME, exerciseName); // This was COLUMN_PLAN_EXERCISE_NAME in your original, check if it should be a specific log exercise name column
                long logExerciseId = db.insert(TABLE_LOG_EXERCISE, null, logExValues);

                if (logExerciseId == -1) {
                    exCursor.close();
                    return false; // Failed to insert exercise log
                }

                for (int i = 0; i < seriesCount; i++) {
                    ContentValues s = new ContentValues();
                    s.put(COLUMN_LOG_EXERCISE_ID, logExerciseId);
                    s.put(COLUMN_LOG_REPS, 0); // Default reps
                    s.put(COLUMN_LOG_WEIGHT, 0.0f); // Default weight
                    if (db.insert(TABLE_LOG_SERIES, null, s) == -1) {
                        exCursor.close();
                        return false; // Failed to insert series log
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

        Cursor logCursor = db.query(TABLE_TRAINING_LOG, new String[]{COLUMN_LOG_ID},
                COLUMN_LOG_USER_ID + "=? AND " + COLUMN_LOG_DATE + "=? AND " + COLUMN_LOG_DAY_NAME + "=?",
                new String[]{String.valueOf(userId), date, dayName}, null, null, null);

        if (logCursor.moveToFirst()) {
            logId = logCursor.getLong(logCursor.getColumnIndexOrThrow(COLUMN_LOG_ID));
        }
        logCursor.close();

        if (logId == -1) {
            db.close();
            return list; // No log found for this user, date, and day name
        }

        // Ensure you are using the correct column name for log_exercise table
        Cursor exCursor = db.query(TABLE_LOG_EXERCISE,
                new String[]{COLUMN_LOG_EXERCISE_ID, COLUMN_PLAN_EXERCISE_NAME}, // Check COLUMN_PLAN_EXERCISE_NAME
                COLUMN_LOG_ID + "=?", new String[]{String.valueOf(logId)}, null, null, null);

        while (exCursor.moveToNext()) {
            long logExerciseId = exCursor.getLong(exCursor.getColumnIndexOrThrow(COLUMN_LOG_EXERCISE_ID));
            String name = exCursor.getString(exCursor.getColumnIndexOrThrow(COLUMN_PLAN_EXERCISE_NAME)); // Check COLUMN_PLAN_EXERCISE_NAME
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
        db.close();
        return list;
    }


    public boolean saveLogSeries(int userId, String date, String dayName, List<Exercise> exercises) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        boolean success = false;
        try {
            /*──────────────────── 1. zapewnij istnienie LOGU ────────────────────*/
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

            /*──────────────────── 2. usuń ćwiczenia, których NIE ma w liście ────*/
            HashSet<String> newNames = new HashSet<>();
            for (Exercise ex : exercises) newNames.add(ex.getName());

            Cursor exCur = db.query(TABLE_LOG_EXERCISE,
                    new String[]{COLUMN_LOG_EXERCISE_ID, COLUMN_PLAN_EXERCISE_NAME}, // Check COLUMN_PLAN_EXERCISE_NAME
                    COLUMN_LOG_ID + "=?",
                    new String[]{String.valueOf(logId)},
                    null, null, null);

            List<Long> exercisesToDelete = new ArrayList<>();
            while (exCur.moveToNext()) {
                long currentLogExerciseId = exCur.getLong(exCur.getColumnIndexOrThrow(COLUMN_LOG_EXERCISE_ID));
                String name = exCur.getString(exCur.getColumnIndexOrThrow(COLUMN_PLAN_EXERCISE_NAME)); // Check COLUMN_PLAN_EXERCISE_NAME

                if (!newNames.contains(name)) {
                    exercisesToDelete.add(currentLogExerciseId);
                }
            }
            exCur.close();

            for (Long logExerciseIdToDelete : exercisesToDelete) {
                db.delete(TABLE_LOG_SERIES, COLUMN_LOG_EXERCISE_ID + "=?",
                        new String[]{String.valueOf(logExerciseIdToDelete)});
                db.delete(TABLE_LOG_EXERCISE, COLUMN_LOG_EXERCISE_ID + "=?",
                        new String[]{String.valueOf(logExerciseIdToDelete)});
            }

            /*──────────────────── 3. up-sert aktualną listę ćwiczeń ─────────────*/
            for (Exercise ex : exercises) {
                Cursor find = db.query(TABLE_LOG_EXERCISE, new String[]{COLUMN_LOG_EXERCISE_ID},
                        COLUMN_LOG_ID + "=? AND " + COLUMN_PLAN_EXERCISE_NAME + "=?", // Check COLUMN_PLAN_EXERCISE_NAME
                        new String[]{String.valueOf(logId), ex.getName()},
                        null, null, null);

                long logExerciseId;
                if (find.moveToFirst()) {
                    logExerciseId = find.getLong(find.getColumnIndexOrThrow(COLUMN_LOG_EXERCISE_ID));
                    // Delete old series for this exercise before inserting new ones
                    db.delete(TABLE_LOG_SERIES, COLUMN_LOG_EXERCISE_ID + "=?",
                            new String[]{String.valueOf(logExerciseId)});
                } else {
                    ContentValues ev = new ContentValues();
                    ev.put(COLUMN_LOG_ID, logId);
                    ev.put(COLUMN_PLAN_EXERCISE_NAME, ex.getName()); // Check COLUMN_PLAN_EXERCISE_NAME
                    logExerciseId = db.insert(TABLE_LOG_EXERCISE, null, ev);
                }
                find.close();

                if (logExerciseId == -1) return false; // Failed to insert/find exercise log

                for (Series s : ex.getSeriesList()) {
                    ContentValues sv = new ContentValues();
                    sv.put(COLUMN_LOG_EXERCISE_ID, logExerciseId);
                    sv.put(COLUMN_LOG_REPS, s.getReps());
                    sv.put(COLUMN_LOG_WEIGHT, s.getWeight());
                    if (db.insert(TABLE_LOG_SERIES, null, sv) == -1) {
                        return false; // Failed to insert series
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
        Cursor cursor = db.query(TABLE_TRAINING_LOG, new String[]{COLUMN_LOG_ID},
                COLUMN_LOG_USER_ID + "=? AND " + COLUMN_LOG_DATE + "=? AND " + COLUMN_LOG_DAY_NAME + "=?",
                new String[]{String.valueOf(userId), date, dayName}, null, null, null);
        long logId = -1;
        if (cursor.moveToFirst()) {
            logId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LOG_ID));
        }
        cursor.close();
        db.close();
        return logId;
    }

    public void deleteLogExercise(long logId, String exerciseName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Check COLUMN_PLAN_EXERCISE_NAME
            Cursor cursor = db.query(TABLE_LOG_EXERCISE, new String[]{COLUMN_LOG_EXERCISE_ID},
                    COLUMN_LOG_ID + "=? AND " + COLUMN_PLAN_EXERCISE_NAME + "=?",
                    new String[]{String.valueOf(logId), exerciseName}, null, null, null);

            if (cursor.moveToFirst()) {
                long logExerciseId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LOG_EXERCISE_ID));
                db.delete(TABLE_LOG_SERIES, COLUMN_LOG_EXERCISE_ID + "=?", new String[]{String.valueOf(logExerciseId)});
                db.delete(TABLE_LOG_EXERCISE, COLUMN_LOG_EXERCISE_ID + "=?", new String[]{String.valueOf(logExerciseId)});
            }
            cursor.close();
            // After deleting an exercise, check if the log itself has become empty
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
        ContentValues logVals = new ContentValues();
        logVals.put(COLUMN_LOG_USER_ID, userId);
        logVals.put(COLUMN_LOG_DATE, date);
        logVals.put(COLUMN_LOG_DAY_NAME, dayName);
        long result = db.insert(TABLE_TRAINING_LOG, null, logVals);
        db.close();
        return result;
    }

    private int countExercisesInLog(long logId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_LOG_EXERCISE + " WHERE " + COLUMN_LOG_ID + "=?",
                new String[]{String.valueOf(logId)});
        int cnt = 0;
        if (c.moveToFirst()) {
            cnt = c.getInt(0);
        }
        c.close();
        // db.close(); // Do not close if db is passed from a method that will close it
        return cnt;
    }

    // This method seems redundant if deleteLogExercise handles purging.
    // Kept for now as it was in original code.
    private void purgeEmptyLog(long logId) {
        // This method should be called within a transaction if it's part of a larger operation
        // or ensure the db connection is managed correctly.
        SQLiteDatabase db = this.getWritableDatabase(); // Opens a new connection
        if (countExercisesInLog(logId) == 0) { // countExercisesInLog opens its own readable DB
            db.delete(TABLE_TRAINING_LOG, COLUMN_LOG_ID + "=?", new String[]{String.valueOf(logId)});
        }
        db.close(); // Closes the writable DB connection
    }


    public void deleteEmptyLogIfNeeded(long logId) {
        SQLiteDatabase db = getWritableDatabase();
        // countExercisesInLog will open its own readable database instance.
        // This is generally fine but be mindful of multiple open connections.
        if (countExercisesInLog(logId) == 0) {
            db.delete(TABLE_TRAINING_LOG, COLUMN_LOG_ID + "=?", new String[]{String.valueOf(logId)});
        }
        db.close(); // Close the writable database instance opened here.
    }
}
