package com.example.safelystapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safelystapp.R;
import com.example.safelystapp.adapters.ShoppingListAdapter;
import com.example.safelystapp.db.Tables;
import com.example.safelystapp.model.ShoppingList;
import com.example.safelystapp.utils.SwipeToDelete;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListsScreen extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ShoppingListAdapter adapter;
    List<ShoppingList> shoppingLists = new ArrayList<>();
    private Tables db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_shopping_page);
        db = new Tables(this);

        Cursor cursor = db.getAllLists();
        loadListsFromDB(cursor);

        FloatingActionButton addListButton = findViewById(R.id.addNewListButton);
        addListButton.setOnClickListener(e -> {
            long newIDReturned = db.insertList("");
            Intent intent = new Intent(ShoppingListsScreen.this, ShoppingListSelectedScreen.class);
            intent.putExtra("ID_LIST", (int) newIDReturned);
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

        adapter = new ShoppingListAdapter(shoppingLists, this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper deleteListItem = new ItemTouchHelper(new SwipeToDelete(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ShoppingList list = shoppingLists.get(position);

                if (db.deleteList(list.id)) {
                    shoppingLists.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        });
        deleteListItem.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor cursor = db.getAllLists();
        loadListsFromDB(cursor);
        adapter.notifyDataSetChanged();
    }

    public void loadListsFromDB(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        shoppingLists.clear();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int count = db.getProductsCount(id);
                shoppingLists.add(new ShoppingList(id, name, count));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}
