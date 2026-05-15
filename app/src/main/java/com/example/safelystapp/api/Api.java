package com.example.safelystapp.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface Api {
    @GET("cgi/search.pl?search_simple=1&action=process&json=1&page_size=10")
    Call<JsonObject> searchProducts
            (@Query("search_terms") String query);
}
