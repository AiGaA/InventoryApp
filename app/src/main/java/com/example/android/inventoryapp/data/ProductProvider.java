package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by AiGa on 2017.07.23..
 */

public class ProductProvider extends ContentProvider {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    //URI matcher code for the content URI for the schoolbooks table
    private static final int SCHOOLBOOKS = 100;

    //URI matcher for the content URI for a single schoolbook item in the schoolbooks table
    private static final int SCHOOLBOOK_ID = 101;

    //UriMatcher object to match a content URI to a corresponding code.
    //The input passed into the constructor represents the code to return for the root URI.
    //It's common to use NO_MATCH as the input for this case.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, SCHOOLBOOKS);

        // The content URI of the form "content://com.example.android.inventoryapp/products/#" will map to the
        // integer code {@link #SCHOOLBOOK_ID}. This URI is used to provide access to ONE single row
        // of the schoolbooks table.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", SCHOOLBOOK_ID);
    }

    //Database helper object
    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        //Create and initialize a ProductDbHelper object to gain access to the schoolbooks database.
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    //Perform a query for the given URI. Use the given projection, selection, selection arguments, and sort order.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SCHOOLBOOKS:
                // For the SCHOOLBOOKS code, query the schoolbooks table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the schoolbooks table.
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SCHOOLBOOK_ID:
                // For the SCHOOLBOOK_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.inventoryapp/products/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the schoolbooks table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        //Return the cursor
        return cursor;
    }

    //Returns the MIME type of data for the content URI.
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SCHOOLBOOKS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case SCHOOLBOOK_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SCHOOLBOOKS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a schoolbook into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Schoolbook item requires a title");
        }

        // Check that the price is greater than or equal to 0
        Double price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Schoolbook item requires a price");
        } else if (price < 0) {
            throw new IllegalArgumentException("Schoolbook item requires valid price");
        }

        // Check that the quantity is greater than or equal to 0
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity is required");
        } else if (quantity < 0) {
            throw new IllegalArgumentException("Valid quantity is required");
        }

        // No need to check the supplier, any value is valid (including null).

        // Check that the supplier's email is not null
        String supplierEmail = values.getAsString(ProductEntry.COLUMN_SUPPLIER_EMAIL);
        if (supplierEmail == null) {
            throw new IllegalArgumentException("Supplier's e-mail is required.");
        }

        //Check that the supplier's phone is not null
        String supplierPhoneNumber = values.getAsString(ProductEntry.COLUMN_SUPPLIER_PHONE);
        if (supplierPhoneNumber == null) {
            throw new IllegalArgumentException("Supplier's phone number is required.");
        }

        //Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new schoolbook with the given values
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners that the data has changed for the schoolbook content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SCHOOLBOOKS:
                return updateProduct(uri, values, selection, selectionArgs);
            case SCHOOLBOOK_ID:
                // For the SCHOOLBOOK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update schoolbooks in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more schoolbooks).
     * Return the number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link ProductEntry#COLUMN_PRODUCT_NAME} key is present,
        // check that the title value is not null.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String title = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (title == null) {
                throw new IllegalArgumentException("Book requires a title-");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_PRICE} key is present,
        // check that the price value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Double price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price <= 0) {
                throw new IllegalArgumentException("Book requires valid price");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_QUANTITY} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            // Check that the quantity is greater than or equal to 0
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Book requires valid quantity");
            }
        }

        // No need to check the supliers name, any value is valid (including null).

        //If the {@link ProductEntry#COLUMN_SUPPLIER_EMAIL} key is present,
        //check that the supplier email is valid.
        if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_EMAIL)) {
            String email = values.getAsString(ProductEntry.COLUMN_SUPPLIER_EMAIL);
            if (email == null) {
                throw new IllegalArgumentException("Supplier requires a valid email.");
            }
        }

        //If the {@link ProductEntry#COLUMN_SUPPLIER_PHONE} key is present,
        //check that the supplier phone is valid.
        if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_PHONE)) {
            String phone = values.getAsString(ProductEntry.COLUMN_SUPPLIER_PHONE);
            if (phone == null) {
                throw new IllegalArgumentException("Supplier requires a valid email.");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement
        return database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SCHOOLBOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SCHOOLBOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }
}

