package com.example.safelystapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safelystapp.R;
import com.example.safelystapp.adapters.RiskAdapter;
import com.example.safelystapp.db.Tables;
import com.example.safelystapp.model.RiskProduct;

import java.util.ArrayList;
import java.util.List;

public class UserProfileScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RiskAdapter adapter;
    private Tables db;
    List<RiskProduct> riskProductList = new ArrayList<>();
    List<CheckBox> allergiesCheckbox = new ArrayList<>();
    List<CheckBox> medicalCheckbox = new ArrayList<>();
    TextView warningCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        db = new Tables(this);
        warningCount = findViewById(R.id.warningCountTextView);

        LinearLayout emptyRiskProductLayout = findViewById(R.id.emptyRiskProductLayout);
        Button resetButton = findViewById(R.id.resetWarningsButton);

        init();
        loadUserFromDB();

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(e -> {
            saveProfile();
        });

        riskProductList.add(new RiskProduct("Cheese Chips", 5));
        riskProductList.add(new RiskProduct("Nuts", 3));
        riskProductList.add(new RiskProduct("Lava Cake", 3));

        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RiskAdapter(riskProductList, this);
        recyclerView.setAdapter(adapter);

        resetButton.setOnClickListener(e -> {
            riskProductList.clear();
            warningCount.setText("0");

            if (riskProductList.isEmpty()) {
                emptyRiskProductLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            else {
                emptyRiskProductLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
        });

        ImageButton backToShoppingScreenButton = findViewById(R.id.profileCloseButton);
        backToShoppingScreenButton.setOnClickListener(e -> {
            Intent intent = new Intent(UserProfileScreen.this, ShoppingListsScreen.class);
            startActivity(intent);
        });
    }

    public void init() {
        allergiesCheckbox.add(findViewById(R.id.glutenCheckbox));
        allergiesCheckbox.add(findViewById(R.id.lactoseCheckbox));
        allergiesCheckbox.add(findViewById(R.id.nutsCheckbox));
        allergiesCheckbox.add(findViewById(R.id.soyCheckbox));
        allergiesCheckbox.add(findViewById(R.id.eggsCheckbox));
        allergiesCheckbox.add(findViewById(R.id.fishCheckbox));

        medicalCheckbox.add(findViewById(R.id.diabetesCheckbox));
        medicalCheckbox.add(findViewById(R.id.hypertensionCheckbox));
        medicalCheckbox.add(findViewById(R.id.cholesterolCheckbox));
        medicalCheckbox.add(findViewById(R.id.celiacCheckbox));
    }

    private void loadUserFromDB() {
        Cursor cursor = db.getUserProfile();
        if (cursor != null && cursor.moveToFirst()) {
            String savedAllergies = cursor.getString(1);
            String savedMedicalConditions = cursor.getString(2);

            checkSavedItems(allergiesCheckbox, savedAllergies);
            checkSavedItems(medicalCheckbox, savedMedicalConditions);
            cursor.close();
        }
    }

    private void checkSavedItems(List<CheckBox> checkBoxList, String savedDataByUser) {
        if (savedDataByUser == null) {
            return;
        }

        for (CheckBox checkBox : checkBoxList) {
            if (savedDataByUser.contains(checkBox.getText().toString().trim())) {
                checkBox.setChecked(true);
            }
        }
    }

    private String getSelectedItems(List<CheckBox> checkBoxList) {
        StringBuilder sb = new StringBuilder();
        for (CheckBox checkBox : checkBoxList) {
            if (checkBox.isChecked()) {
                sb.append(checkBox.getText().toString().trim()).append(",");
            }
        }
        return sb.toString();
    }

    private void saveProfile() {
        String allergies = getSelectedItems(allergiesCheckbox);
        String medicalConditions = getSelectedItems(medicalCheckbox);

        db.updateUserProfile(allergies, medicalConditions);
        Toast.makeText(this, "Profile Saved Successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
