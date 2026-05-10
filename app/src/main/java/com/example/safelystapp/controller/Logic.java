package com.example.safelystapp.controller;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Logic {
    private static final Map<String, List<String>> allergiesMap = new HashMap<>();

    static {
        List<String> glutenKeywords = Arrays.asList("gluten", "wheat", "barley", "rye", "malt", "spelt", "kamut", "farina", "semolina");
        allergiesMap.put("Gluten", glutenKeywords);
        allergiesMap.put("Celiac", glutenKeywords);
        allergiesMap.put("Lactose", Arrays.asList("milk", "lactose", "whey", "butter", "cream", "cheese", "yogurt", "curd", "casein"));
        allergiesMap.put("Nuts", Arrays.asList("nut", "peanut", "almond", "hazelnut", "walnut", "cashew", "pistachio", "pecan", "brazil nut"));
        allergiesMap.put("Soy", Arrays.asList("soy", "soya", "tofu", "edamame", "lecithin"));
        allergiesMap.put("Eggs", Arrays.asList("egg", "albumin", "yolk", "ovalbumin"));
        allergiesMap.put("Fish", Arrays.asList("fish", "salmon", "tuna", "cod", "anchovy", "shrimp", "prawn", "crab", "lobster", "mussel"));
    }

    public static List<String> checkAllergies(String ingredients, String savedAllergies) {
        List<String> warningsFound = new ArrayList<>();
        if (ingredients == null || savedAllergies == null || ingredients.isEmpty() || savedAllergies.isEmpty()) {
            return warningsFound;
        }

        String[] userAllergies = savedAllergies.split(",");
        for (String allergy : userAllergies) {
            List<String> keywordsMap = allergiesMap.get(allergy.trim());

            if (keywordsMap != null) {
                for (String keyword : keywordsMap) {
                    if (ingredients.toLowerCase().contains(keyword)) {
                        if (allergy.trim().equals("Celiac")) {
                            warningsFound.add("Celiac ALERT! " + keyword);
                        }
                        else {
                            warningsFound.add(keyword);
                        }
                        break;
                    }
                }
            }
        }
        return  warningsFound;
    }

    public static List<String> checkMedicalConditions(JSONObject nutriments, String savedMedicalConditions) {
        List<String> medicalWarnings = new ArrayList<>();
        if (nutriments == null || savedMedicalConditions == null || savedMedicalConditions.isEmpty()) {
            return medicalWarnings;
        }

        if (savedMedicalConditions.contains("Diabetes")) {
            double sugar = nutriments.optDouble("sugars_100g", 0.0);
            if (sugar > 36.0) {
                medicalWarnings.add("High Sugar - " + sugar + "g");
            }
        }

        if (savedMedicalConditions.contains("Hypertension")) {
            double salt = nutriments.optDouble("salt_100g", 0.0);
            if (salt > 5.0) {
                medicalWarnings.add("High Salt - " + salt + "g");
            }
        }

        if (savedMedicalConditions.contains("Cholesterol")) {
            double fat = nutriments.optDouble("fat", 0.0);
            if (fat > 13.0) {
                medicalWarnings.add("High Saturated Fat - " + fat + "g");
            }
        }

        return medicalWarnings;
    }

    public static List<String> productEvaluation(String ingredients, JSONObject nutriments, String savedAllergies, String savedMedicalConditions) {
        List<String> totalWarnings = new ArrayList<>();
        totalWarnings.addAll(checkAllergies(ingredients, savedAllergies));
        totalWarnings.addAll(checkMedicalConditions(nutriments, savedMedicalConditions));
        return totalWarnings;
    }
}
