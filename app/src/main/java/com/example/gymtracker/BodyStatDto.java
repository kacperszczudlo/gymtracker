package com.example.gymtracker;

import java.math.BigDecimal;

public class BodyStatDto {
    private BigDecimal weight;
    private BigDecimal armCircumference;
    private BigDecimal waistCircumference;
    private BigDecimal hipCircumference;

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getArmCircumference() {
        return armCircumference;
    }

    public void setArmCircumference(BigDecimal armCircumference) {
        this.armCircumference = armCircumference;
    }

    public BigDecimal getWaistCircumference() {
        return waistCircumference;
    }

    public void setWaistCircumference(BigDecimal waistCircumference) {
        this.waistCircumference = waistCircumference;
    }

    public BigDecimal getHipCircumference() {
        return hipCircumference;
    }

    public void setHipCircumference(BigDecimal hipCircumference) {
        this.hipCircumference = hipCircumference;
    }
}
