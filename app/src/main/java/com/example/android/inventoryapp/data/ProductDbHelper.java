package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by AiGa on 2017.07.24..
 */
//Database helper for Schoolbook Inventory app. Manages database creation and version management.
public class ProductDbHelper extends SQLiteOpenHelper {

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Name of the database
    public final static String DATABASE_NAME = "schoolbooks.db";

    //Database version. If database schema is changed then must to increment database version.
    public final static int DATABASE_VERSION = 1;

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the coffee table
        String SQL_CREATE_SCHOOLBOOK_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT, "
                + ProductEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_IMAGE + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_SCHOOLBOOK_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
