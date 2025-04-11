package com.example.gymtracker;

import java.util.List;

public class TrainingDay {
    private String day;
    private List<Exercise> exercises;

    public TrainingDay(String day, List<Exercise> exercises) {
        this.day = day;
        this.exercises = exercises;
    }

    public String getDay() {
        return day;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }
}