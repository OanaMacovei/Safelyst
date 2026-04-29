package com.example.safelystapp.activities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safelystapp.R;
import com.example.safelystapp.adapters.ProductAdapter;
import com.example.safelystapp.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ShoppingListSelectedScreen extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private Integer productID = 3;
    private Stack<Product> undoStack = new Stack<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_selected);

        String receivedTitle = getIntent().getStringExtra("LIST_NAME");
        EditText title = findViewById(R.id.listSelectedTitle);
        if (!receivedTitle.isEmpty()) {
            title.setText(receivedTitle);
        }
        else {
            title.setText("");
            title.setHint("List Name");
            title.requestFocus();

            title.setOnEditorActionListener((e, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    title.clearFocus();
                    return true;
                }
                return false;
            });
        }

        EditText search = findViewById(R.id.searchProductEditText);
        search.setOnEditorActionListener((e, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String productName = search.getText().toString().trim();

                if (!productName.isEmpty()) {
                    Product newProduct = new Product(++productID, productName);
                    productList.add(newProduct);
                    adapter.notifyItemInserted(productList.size() - 1);
                    search.setText("");
                }
                return true;
            }
            return false;
        });

        productList.add(new Product(1, "Lapte 1.5%"));
        productList.add(new Product(2, "Paine Integrala"));
        productList.add(new Product(3, "Ciocolata Neagra"));

        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);


        ImageButton undoButton = findViewById(R.id.undoButton);
        ItemTouchHelper.SimpleCallback deleteSwipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            private final Drawable deleteIcon = ContextCompat.getDrawable(ShoppingListSelectedScreen.this, R.drawable.ic_delete_white);
            private final GradientDrawable backgroundRed = new GradientDrawable();

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Product crtProduct = productList.get(position);
                crtProduct.lastPosition = position;
                undoStack.push(crtProduct);

                productList.remove(position);
                adapter.notifyItemRemoved(position);

                undoButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isActive) {
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isActive);

                backgroundRed.setColor(Color.RED);
                backgroundRed.setCornerRadius(12f);
                View itemView = viewHolder.itemView;
                if (dX == 0f) {
                    backgroundRed.setBounds(0, 0, 0, 0);
                    backgroundRed.draw(canvas);
                    return;
                }

                if (dX < 0) {
                   backgroundRed.setBounds(
                           itemView.getRight() + (int) dX,
                           itemView.getTop(),
                           itemView.getRight(),
                           itemView.getBottom()
                   );
                }
                backgroundRed.draw(canvas);

                double sizeMultiplier = 1.5;
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicHeigth = deleteIcon.getIntrinsicHeight();
                int biggerIconSize = (int) (intrinsicHeigth * sizeMultiplier);

                int iconTop = itemView.getTop() + (itemHeight - biggerIconSize) / 2;
                int iconMargin = (itemHeight - biggerIconSize) / 2;
                int iconLeft = itemView.getRight() - iconMargin - biggerIconSize;
                int iconRight = itemView.getRight() - iconMargin;
                int iconBottom = iconTop + biggerIconSize;

                if (dX < -100) {
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    deleteIcon.draw(canvas);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(deleteSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        undoButton.setOnClickListener(e -> {
            if (!undoStack.isEmpty()) {
                Product productToUndoTheUndo = undoStack.pop();
                productList.add(productToUndoTheUndo.lastPosition, productToUndoTheUndo);

                if (undoStack.isEmpty()) {
                    undoButton.setVisibility(View.GONE);
                }
                adapter.notifyItemInserted(productToUndoTheUndo.lastPosition);
            }
        });
    }
}
