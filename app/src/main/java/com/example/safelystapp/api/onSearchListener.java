package com.example.safelystapp.api;

import com.google.gson.JsonArray;

public interface onSearchListener {
    void onResults(JsonArray products);
    void onError(String error);
}
