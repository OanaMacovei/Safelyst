package com.example.safelystapp.adapters;

import static java.lang.Math.min;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safelystapp.R;
import com.example.safelystapp.model.RiskProduct;

import java.util.List;

public class RiskAdapter extends RecyclerView.Adapter<RiskAdapter.RiskViewHolder> {
    private List<RiskProduct> riskProductList;
    private Context context;

    public RiskAdapter(List<RiskProduct> riskProductList, Context context) {
        this.context = context;
        this.riskProductList = riskProductList;
    }


    @NonNull
    @Override
    public RiskAdapter.RiskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.risk_product_item, parent, false);
        return new RiskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RiskAdapter.RiskViewHolder holder, int position) {
        RiskProduct riskProduct = riskProductList.get(position);
        holder.index.setText((position + 1) + ".");
        holder.warningCount.setText(riskProduct.getWarningCount() + "x");
        holder.name.setText(riskProduct.getName());
    }

    @Override
    public int getItemCount() {
        return min(riskProductList.size(), 5);
    }

    public static class RiskViewHolder extends RecyclerView.ViewHolder {
        TextView index, warningCount, name;

        public RiskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.index = itemView.findViewById(R.id.indexTextView);
            this.warningCount = itemView.findViewById(R.id.warningCountTextView);
            this.name = itemView.findViewById(R.id.productNameTextView);
        }
    }
}
