package com.example.safelystapp.model;

public class Product {
    public int id, listId;
    public String name, expirationDate;
    public boolean isChecked;

    public Product(int listId, String name) {
        this.id = id;
        this.expirationDate = "";
        this.isChecked = false;
        this.name = name;
    }
}
