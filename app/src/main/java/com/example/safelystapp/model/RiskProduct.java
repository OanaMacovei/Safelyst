package com.example.safelystapp.model;

public class RiskProduct {
    public String name;
    public Integer warningCount;

    public RiskProduct(String name, Integer warningCount) {
        this.name = name;
        this.warningCount = warningCount;
    }

    public String getName() {
        return name;
    }

    public Integer getWarningCount() {
        return warningCount;
    }
}
