package com.example.gymtracker;

public class Exercise {
    private String name;
    private int reps;
    private int sets; // Previously weight, now repurposed as sets

    public Exercise(String name, int reps, int sets) {
        this.name = name;
        this.reps = reps;
        this.sets = sets;
    }

    public String getName() {
        return name;
    }

    public int getReps() {
        return reps;
    }

    public int getSets() {
        return sets;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }
}