package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import static com.example.android.inventoryapp.R.id.quantity;
import static com.example.android.inventoryapp.R.id.sale_button;
/**
 * Created by AiGa on 2017.07.24..
 */

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        //Find individual views that we want to modify in the list item layout
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(quantity);
        Button mSaleButton = (Button) view.findViewById(sale_button);

        //Find the columns of pet attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);

        //Read the product attributes from the Cursor for the current schoolbook
        String bookTitle = cursor.getString(titleColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        final int bookQuantity = cursor.getInt(quantityColumnIndex);
        final int bookId = cursor.getInt(idColumnIndex);

        //Update the TextView with the attributes for the current product
        titleTextView.setText(bookTitle);
        priceTextView.setText("Price: " + bookPrice + "€");
        quantityTextView.setText("In stock: " + Integer.toString(bookQuantity));

        // Set a clickListener on sale button
        mSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri currentBookUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, bookId);
                reduceBookQuantity(context, bookQuantity, currentBookUri);
            }
        });
    }

    private void reduceBookQuantity(Context context, int quantity, Uri uri) {
        if (quantity > 0) {
            quantity--;

            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            context.getContentResolver().update(uri, values, null, null);
        } else {
            Toast.makeText(context, "This book has no stock", Toast.LENGTH_SHORT).show();
        }
    }
}