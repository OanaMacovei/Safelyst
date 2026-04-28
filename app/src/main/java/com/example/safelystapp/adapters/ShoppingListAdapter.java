package com.example.safelystapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safelystapp.R;
import com.example.safelystapp.activities.ShoppingListSelectedScreen;
import com.example.safelystapp.model.ShoppingList;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>{
    private Context context;
    private List<ShoppingList> shoppingLists;

    public ShoppingListAdapter(List<ShoppingList> shoppingLists, Context context) {
        this.shoppingLists = shoppingLists;
        this.context = context;
    }

    public ShoppingListAdapter.ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_object, parent, false);
        return new ShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
        ShoppingList crtShoppingList = shoppingLists.get(position);
        holder.shoppingListName.setText(crtShoppingList.name);
        holder.shoppingListItemCount.setText(crtShoppingList.itemCount + " items");

        holder.itemView.setOnClickListener(e -> {
            Intent intent = new Intent(context, ShoppingListSelectedScreen.class);
            intent.putExtra("LIST_NAME", crtShoppingList.name);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }


    static class ShoppingListViewHolder extends RecyclerView.ViewHolder {
        TextView shoppingListName, shoppingListItemCount;

        public ShoppingListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.shoppingListName = itemView.findViewById(R.id.listNameTextView);
            this.shoppingListItemCount = itemView.findViewById(R.id.listItemCount);
        }
    }

}
