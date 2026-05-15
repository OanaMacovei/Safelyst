package com.example.safelystapp.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safelystapp.R;
import com.example.safelystapp.adapters.ProductAdapter;
import com.example.safelystapp.api.Api;
import com.example.safelystapp.api.SearchLogic;
import com.example.safelystapp.api.onSearchListener;
import com.example.safelystapp.controller.Logic;
import com.example.safelystapp.db.Tables;
import com.example.safelystapp.model.Product;
import com.example.safelystapp.utils.SwipeToDelete;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShoppingListSelectedScreen extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private Stack<Product> undoStack = new Stack<>();
    private ArrayAdapter<String> autoMatchAdapter;
    private List<JsonObject> crtSuggestions = new ArrayList<>();
    private Tables db;
    private Api api;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_selected);
        db = new Tables(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("User-Agent", "Safelyst/1.0")
                            .build();
                    return chain.proceed(request);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://en.openfoodfacts.org/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);

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

        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Searching for products...");
        progress.setCancelable(false);
        EditText search = findViewById(R.id.searchEditText);
        search.setOnEditorActionListener((e, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = search.getText().toString();
                if (query.length() >= 2) {
                    progress.show();
                    SearchLogic.fetchProducts(api, query, new onSearchListener() {
                        @Override
                        public void onResults(JsonArray products) {
                            progress.dismiss();
                            showSelectionDialog(products, crtListID);
                        }

                        @Override
                        public void onError(String error) {
                            progress.dismiss();
                            Toast.makeText(ShoppingListSelectedScreen.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
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
                long newIDReturned = db.insertProduct(crtListID, productToUndoTheUndo.name, productToUndoTheUndo.expirationDate, "", productToUndoTheUndo.hasWarning);

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
                product.expirationDate = "Expire at: " + expirationDateVal;
                product.hasWarning = cursor.getString(6);
                productList.add(product);
            } while(cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void showWarningDialog(String name, List<String> warnings, int listID) {
        StringBuilder sb = new StringBuilder("Product contains:\n");
        int i = 1;
        for (String warning : warnings) {
            sb.append(i).append(". ").append(warning).append("\n");
            i++;
        }
        sb.append("\nDo you still want to add it?");

        new AlertDialog.Builder(this)
            .setTitle("WARNING: " + name)
            .setMessage(sb.toString())
            .setPositiveButton("Add anyway", ((dialog, which) -> {
                String warningsToString = String.join(",", warnings);
                long newIDReturned = db.insertProduct(listID, name + " ⚠️", "--/--/----", "", warningsToString);
                if (newIDReturned != -1) {
                    loadProductsFromDB(listID);
                }

                db.incrementWarningCount();
            }))
            .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
            .show();
    }

    private void showSelectionDialog(JsonArray products, int listID) {
        if (products.size() == 0) {
            new AlertDialog.Builder(this).setTitle("Warning").setMessage("No products found!").show();
            return;
        }

        String[] productNames = new String[products.size()];
        for (int i = 0; i < products.size(); i++) {
            productNames[i] = SearchLogic.getProductName(products.get(i).getAsJsonObject());
        }

        new AlertDialog.Builder(this)
                .setTitle("Select product")
                .setItems(productNames, ((dialog, which) -> {
                    JsonObject selectedProduct = products.get(which).getAsJsonObject();
                    productionSelection(selectedProduct, listID);
                }))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void productionSelection(JsonObject selectedProduct, int listID) {
        String name = SearchLogic.getProductName(selectedProduct);

        String ingredients = "";
        if (selectedProduct.has("ingredients_text")) {
            ingredients = selectedProduct.get("ingredients_text").getAsString();
        }
        JSONObject nutrimentsJSON = SearchLogic.nutrimentsConvertedToJSON(selectedProduct);

        Log.d("DEBUG_LOGIC", "Ingredients: " + ingredients);
        Log.d("DEBUG_LOGIC", "Nutriments: " + nutrimentsJSON);
        List<String> warnings = Logic.productEvaluation(ingredients, nutrimentsJSON, db.getUserAllergies(), db.getUserMedicalConditions());
        if (warnings.isEmpty()) {
            db.insertProduct(listID, name, "--/--/----", ingredients, null);
            loadProductsFromDB(listID);
        }
        else {
            showWarningDialog(name, warnings, listID);
        }

        EditText search = findViewById(R.id.searchEditText);
        search.setText("");
    }
}
