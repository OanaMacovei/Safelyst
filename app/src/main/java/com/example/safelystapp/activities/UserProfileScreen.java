package com.example.safelystapp.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safelystapp.R;
import com.example.safelystapp.adapters.RiskAdapter;
import com.example.safelystapp.model.RiskProduct;

import java.util.ArrayList;
import java.util.List;

public class UserProfileScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RiskAdapter adapter;
    List<RiskProduct> riskProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        TextView warningCount = findViewById(R.id.warningCountTextView);
        LinearLayout emptyRiskProductLayout = findViewById(R.id.emptyRiskProductLayout);
        Button resetButton = findViewById(R.id.resetWarningsButton);

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




}
