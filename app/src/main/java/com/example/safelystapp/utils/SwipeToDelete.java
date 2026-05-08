package com.example.safelystapp.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safelystapp.R;

public abstract class SwipeToDelete extends ItemTouchHelper.SimpleCallback {
    private final Drawable deleteIcon;
    private final GradientDrawable backgroundRed;

    public SwipeToDelete(Context context) {
        super(0, ItemTouchHelper.LEFT);
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white);
        backgroundRed = new GradientDrawable();
        backgroundRed.setColor(Color.RED);
        backgroundRed.setCornerRadius(12f);
    }

    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isActive) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isActive);

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
}
