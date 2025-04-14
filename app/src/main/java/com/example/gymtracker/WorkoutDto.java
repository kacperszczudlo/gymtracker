package com.example.gymtracker;

import java.util.List;

public class WorkoutDto {
    private Integer id;
    private Integer userId;
    private String name;   // Możesz tu przechowywać nazwę dnia, np. "Poniedziałek"
    private String type;   // np. "siłowy", "kardio", "stretching"
    private Integer duration; // w minutach
    private String notes;
    private List<ExerciseDto> exercises; // lista ćwiczeń dla treningu

    // Gettery i settery

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<ExerciseDto> getExercises() {
        return exercises;
    }

    public void setExercises(List<ExerciseDto> exercises) {
        this.exercises = exercises;
    }
}
