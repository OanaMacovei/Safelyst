package com.example.safelystapp.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safelystapp.R;
import com.example.safelystapp.adapters.ProductAdapter;
import com.example.safelystapp.db.Tables;
import com.example.safelystapp.model.Product;
import com.example.safelystapp.utils.SwipeToDelete;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ShoppingListSelectedScreen extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private Stack<Product> undoStack = new Stack<>();
    private Tables db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_selected);
        db = new Tables(this);

        int crtListID = getIntent().getIntExtra("ID_LIST", -1);

        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);

        if (crtListID != -1) {
            loadProductsFromDB(crtListID);
        }

        String receivedTitle = getIntent().getStringExtra("LIST_NAME");
        EditText title = findViewById(R.id.listSelectedTitle);
        if (receivedTitle != null && !receivedTitle.isEmpty()) {
            title.setText(receivedTitle);
        }
        else {
            title.setText("");
            title.setHint("List Name");
            title.requestFocus();
        }

        title.setOnEditorActionListener((e, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String newName = title.getText().toString().trim();
                if (!newName.isEmpty()) {
                    db.updateListName(crtListID, newName);
                    title.clearFocus();
                }
                return true;
            }
            return false;
        });

        title.setOnFocusChangeListener((e, hasFocus) -> {
            if (!hasFocus) {
                db.updateListName(crtListID, title.getText().toString().trim());
            }
        });

        EditText search = findViewById(R.id.searchProductEditText);
        search.setOnEditorActionListener((e, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String productName = search.getText().toString().trim();

                if (!productName.isEmpty()) {
                    long newIDReturned = db.insertProduct(crtListID, productName, "", "");
                    if (newIDReturned != -1) {
                        loadProductsFromDB(crtListID);
                        search.setText("");
                        search.clearFocus();
                    }
                }
                return true;
            }
            return false;
        });


        ImageButton undoButton = findViewById(R.id.undoButton);
        ItemTouchHelper deleteSwipeCallback = new ItemTouchHelper(new SwipeToDelete(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Product crtProduct = productList.get(position);
                crtProduct.lastPosition = position;
                undoStack.push(crtProduct);

                boolean success = db.deleteProduct(crtProduct.getID());
                if (success) {
                    productList.remove(position);
                    adapter.notifyItemRemoved(position);
                }
                else {
                    adapter.notifyItemChanged(position);
                }

                undoButton.setVisibility(View.VISIBLE);
            }
        });
        deleteSwipeCallback.attachToRecyclerView(recyclerView);

        undoButton.setOnClickListener(e -> {
            if (!undoStack.isEmpty()) {
                Product productToUndoTheUndo = undoStack.pop();
                long newIDReturned = db.insertProduct(crtListID, productToUndoTheUndo.name, productToUndoTheUndo.expirationDate, "");

                if (newIDReturned != -1) {
                    productToUndoTheUndo.id = (int) newIDReturned;
                    productList.add(productToUndoTheUndo.lastPosition, productToUndoTheUndo);
                    adapter.notifyItemInserted(productToUndoTheUndo.lastPosition);
                }

                if (undoStack.isEmpty()) {
                    undoButton.setVisibility(View.GONE);
                }
            }
        });
    }

    public void loadProductsFromDB(int id) {
        productList.clear();
        Cursor cursor = db.getProductsByList(id);
        if (cursor.moveToFirst()) {
            do {
                int ID_Product = cursor.getInt(0);
                String productName = cursor.getString(2);
                int isCheckedVal = cursor.getInt(4);
                String expirationDateVal = cursor.getString(3);
                Product product = new Product(ID_Product, productName);
                product.isChecked = (isCheckedVal == 1);
                product.expirationDate = expirationDateVal;
                productList.add(product);
            } while(cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
