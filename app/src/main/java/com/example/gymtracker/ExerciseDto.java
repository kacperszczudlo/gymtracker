package com.example.gymtracker;

import java.math.BigDecimal;

public class ExerciseDto {
    private Integer id;
    private String name;
    private BigDecimal weight; // Jeśli chcesz przechowywać wagę używaną przy ćwiczeniu

    // Gettery i settery

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
