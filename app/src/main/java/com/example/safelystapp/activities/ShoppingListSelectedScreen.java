package com.example.safelystapp.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safelystapp.R;
import com.example.safelystapp.adapters.ProductAdapter;
import com.example.safelystapp.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListSelectedScreen extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_selected);

        String receivedTitle = getIntent().getStringExtra("LIST_NAME");
        TextView title = findViewById(R.id.listSelectedTitle);
        if (!receivedTitle.isEmpty()) {
            title.setText(receivedTitle);
        }
        else {
            title.setText("");
        }

        productList.add(new Product(1, "Lapte 1.5%"));
        productList.add(new Product(2, "Paine Integrala"));
        productList.add(new Product(3, "Ciocolata Neagra"));

        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);
    }
}
