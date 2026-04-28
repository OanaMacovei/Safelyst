package com.example.safelystapp.model;

public class ShoppingList {
    public int id, itemCount;
    public String name;

    public ShoppingList(int id, String name, int itemCount) {
        this.id = id;
        this.name = name;
        this.itemCount = itemCount;
    }
}
