package com.example.safelystapp.model;

public class Product {
    public int id, lastPosition;
    public String name, expirationDate;
    public boolean isChecked;

    public Product(int id, String name) {
        this.id = id;
        this.expirationDate = "";
        this.isChecked = false;
        this.name = name;
    }

    public int getID() {
        return id;
    }
}
