package com.example.safelystapp.api;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchLogic {

    public static void fetchProducts(Api api, String query, onSearchListener listener) {
        api.searchProducts(query).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonArray products = response.body().getAsJsonArray("products");
                    listener.onResults(products);
                }
                else {
                    listener.onError("Server error");
                    Log.e("API_DEBUG", "Statuscode: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }
    public static String getProductName(JsonObject product) {
        if (product.has("product_name") && product.get("product_name") != null) {
            return product.get("product_name").getAsString();
        }
        return "Unknown";
    }

    public static JSONObject nutrimentsConvertedToJSON (JsonObject product) {
        JSONObject nutrimentsJSON = new JSONObject();
        JsonObject nutriments = product.getAsJsonObject("nutriments");

        try {
            if (nutriments != null) {
                double sugar = getDoubleFromJson(nutriments,"sugars_100g");
                double salt = getDoubleFromJson(nutriments,"salt_100g");
                double fat = getDoubleFromJson(nutriments,"saturated-fat_100g");
                nutrimentsJSON.put("sugars_100g", sugar);
                nutrimentsJSON.put("salt_100g", salt);
                nutrimentsJSON.put("saturated-fat_100g", fat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nutrimentsJSON;
    }

    private static double getDoubleFromJson(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsDouble();
        }
        return 0.0;
    }
}
