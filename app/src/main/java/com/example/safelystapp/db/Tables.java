package com.example.safelystapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObservable;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Tables extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Safelyst.db";
    public static final int DATABASE_VERSION = 1;

    public Tables (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE shopping_lists (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "LIST_NAME TEXT)");

        db.execSQL("CREATE TABLE products (" +
                "ID_PRODUCT INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ID_LIST INTEGER," +
                "NAME TEXT," +
                "EXPIRATION_DATE TEXT," +
                "IS_CHECKED INTEGER," +
                "INGREDIENTS TEXT)");

        db.execSQL("CREATE TABLE user_profile (" +
                "ID_USER INTEGER PRIMARY KEY," +
                "ALLERGIES TEXT," +
                "MEDICAL_CONDITIONS TEXT," +
                "WARNINGS_COUNT INTEGER)");

        db.execSQL("INSERT INTO user_profile (ID_USER, ALLERGIES, MEDICAL_CONDITIONS, WARNINGS_COUNT) VALUES (1, '', '', 0)"); //pt ca randul sa existe
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS shopping_lists");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS user_profile");
        onCreate(db);
    }

    public long insertList(String listName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("LIST_NAME", listName);
        return db.insert("shopping_lists", null, contentValues);
    }

    public Cursor getAllLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM shopping_lists", null);
    }

    public void updateListName(int listID, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("LIST_NAME", name);
        db.update("shopping_lists", contentValues, "ID = ?", new String[]{String.valueOf(listID)});
    }

    public boolean deleteList(int listID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("products", "ID_LIST = ?", new String[]{String.valueOf(listID)});
        int result = db.delete("shopping_lists", "ID = ?", new String[]{String.valueOf(listID)});
        return result > 0;
    }

    public long insertProduct(int listID, String productName, String expirationDate, String ingredients) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_LIST", listID);
        contentValues.put("NAME", productName);
        contentValues.put("EXPIRATION_DATE", expirationDate);
        contentValues.put("IS_CHECKED", 0);
        contentValues.put("INGREDIENTS", ingredients);

        return db.insert("products", null, contentValues);
    }

    public boolean deleteProduct(int listID) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("products", "ID_PRODUCT = ?", new String[]{String.valueOf(listID)});
        return result > 0;
    }

    public boolean updateProductCheckbox(int productID, int isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("IS_CHECKED", isChecked);
        int result = db.update("products", contentValues,"ID_PRODUCT = ?", new String[]{String.valueOf(productID)});
        return  result > 0;
    }

    public Cursor getProductsByList(int listID) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM products WHERE ID_LIST = ?", new String[]{String.valueOf(listID)});
    }

    public int getProductsCount(int listID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM products WHERE ID_LIST = ?", new String[]{String.valueOf(listID)});
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public void updateExpirationDate(int productID, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EXPIRATION_DATE", date);
        db.update("products", contentValues, "ID_PRODUCT = ?", new String[]{String.valueOf(productID)});
    }
}














