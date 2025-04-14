package com.example.gymtracker;

import java.math.BigDecimal;

public class SetDto {
    private Integer id;
    private int setNumber;
    private int reps;
    private BigDecimal weight;

    // Gettery i settery
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public int getSetNumber() {
        return setNumber;
    }
    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }
    public int getReps() {
        return reps;
    }
    public void setReps(int reps) {
        this.reps = reps;
    }
    public BigDecimal getWeight() {
        return weight;
    }
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
