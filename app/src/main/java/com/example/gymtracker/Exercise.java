package com.example.gymtracker;

public class Exercise {
    private String name;
    private int sets;
    private int reps;
    private float weight;

    public Exercise(String name, int sets, int reps, float weight) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public float getWeight() {
        return weight;
    }
}