package com.example.gymtracker;

public class Exercise {
    private String name;
    private int reps;
    private int weight;

    public Exercise(String name, int reps, int weight) {
        this.name = name;
        this.reps = reps;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public int getReps() {
        return reps;
    }

    public int getWeight() {
        return weight;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}