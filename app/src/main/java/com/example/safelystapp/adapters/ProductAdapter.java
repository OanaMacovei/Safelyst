package com.example.safelystapp.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safelystapp.R;
import com.example.safelystapp.db.Tables;
import com.example.safelystapp.model.Product;

import java.util.Calendar;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private Context context;
    private Tables db;

    public ProductAdapter(List<Product> products, Context context) {
        this.products = products;
        this.context = context;
        this.db = new Tables(context);
    }


    @NonNull
    @Override
    //ProductViewHolder e folosit pt a tine minte static id-urile unor view-uri astfel incat sa nu caute de fiecare data
    //Cand sunt multe view-uri de randat, ar lua enorm timp pt ca trebe sa stea sa caute, iar cu ProductViewHolder o sa
    //stie exact ce id este asignat
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_detail_object, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.nameTextView.setText(product.name);

        if (product.expirationDate != null && !product.expirationDate.isEmpty()) {
            holder.expirationDateTextView.setText(product.expirationDate);
        }

        holder.expirationDateButton.setOnClickListener(e -> {
            showDatePicker(holder, product);
        });

        holder.checkbox.setChecked(product.isChecked);
        holder.nameTextView.getPaint().setStrikeThruText(product.isChecked);
        holder.nameTextView.setTextColor(product.isChecked ? Color.GRAY : Color.BLACK);

        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.isChecked = isChecked;
            int isCheckedVal;
            if (isChecked) {
                isCheckedVal = 1;
            }
            else {
                isCheckedVal = 0;
            }

            db.updateProductCheckbox(product.id, isCheckedVal);
            if (isCheckedVal == 1) {
                holder.nameTextView.getPaint().setStrikeThruText(isChecked);
                holder.nameTextView.setTextColor(Color.GRAY);
            }
            else {
                holder.nameTextView.getPaint().setStrikeThruText(false);
                holder.nameTextView.setTextColor(Color.BLACK);
            }
            holder.nameTextView.invalidate();
        });
    }

    private void showDatePicker(ProductViewHolder holder, Product product) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);

        DatePickerDialog dataPickerDialog = new DatePickerDialog(context, (view, year1, month1, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            product.expirationDate = date;
            holder.expirationDateTextView.setText("Expire at: " + date);
            db.updateExpirationDate(product.id, date);
        }, year, month, day);

        dataPickerDialog.show();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, expirationDateTextView;
        CheckBox checkbox;
        ImageButton expirationDateButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nameTextView = itemView.findViewById(R.id.productNameTextView);
            this.checkbox = itemView.findViewById(R.id.checkIfBought);
            this.expirationDateButton = itemView.findViewById(R.id.datePickerButton);
            this.expirationDateTextView = itemView.findViewById(R.id.expirationDate);
        }
    }
}
