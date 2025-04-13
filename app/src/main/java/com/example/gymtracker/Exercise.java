package com.example.gymtracker;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
    private long id; // Dodajemy ID ćwiczenia
    private String name;
    private List<Series> seriesList;

    public Exercise(long id, String name) {
        this.id = id;
        this.name = name;
        this.seriesList = new ArrayList<>();
    }

    // Konstruktor używany w TrainingSetupActivity (bez ID, bo jeszcze nie zapisane do bazy)
    public Exercise(String name, int reps, int sets) {
        this.id = -1; // ID będzie ustawione po zapisaniu do bazy
        this.name = name;
        this.seriesList = new ArrayList<>();
        for (int i = 0; i < sets; i++) {
            seriesList.add(new Series(reps, 0));
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Series> getSeriesList() {
        return seriesList;
    }

    public void addSeries(Series series) {
        seriesList.add(series);
    }

    public int getSets() {
        return seriesList.size();
    }

    public void setSets(int sets) {
        int currentSets = seriesList.size();
        if (sets > currentSets) {
            int reps = getReps();
            for (int i = currentSets; i < sets; i++) {
                seriesList.add(new Series(reps, 0));
            }
        } else if (sets < currentSets) {
            seriesList.subList(sets, currentSets).clear();
        }
    }

    public int getReps() {
        return seriesList.isEmpty() ? 0 : seriesList.get(0).getReps();
    }

    public void setReps(int reps) {
        for (Series series : seriesList) {
            series.setReps(reps);
        }
    }
}