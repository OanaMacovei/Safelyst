package com.example.safelystapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safelystapp.R;
import com.example.safelystapp.adapters.ShoppingListAdapter;
import com.example.safelystapp.model.ShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListsScreen extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ShoppingListAdapter adapter;
    private List<ShoppingList> shoppingList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_shopping_page);

        shoppingList.add(new ShoppingList(1, "Shopping", 3));
        shoppingList.add(new ShoppingList(2, "Andrei's Birthday", 10));
        shoppingList.add(new ShoppingList(3, "Dinner Tomorrow", 7));

        FloatingActionButton addListButton = findViewById(R.id.addNewListButton);
        addListButton.setOnClickListener(e -> {
            Intent intent = new Intent(ShoppingListsScreen.this, ShoppingListSelectedScreen.class);
            intent.putExtra("LIST_NAME", "");
            startActivity(intent);
        });

        CardView userProfileButton = findViewById(R.id.toUserProfileButton);
        userProfileButton.setOnClickListener(e -> {
            Intent intent = new Intent(ShoppingListsScreen.this, UserProfileScreen.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ShoppingListAdapter(shoppingList, this);
        recyclerView.setAdapter(adapter);
    }
}
