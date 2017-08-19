package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by AiGa on 2017.07.23..
 */

public final class ProductContract {

    //An empty private constructor makes sure that the class is not going to be initialised.
    private ProductContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.inventoryapp/products/ is a valid path for
     * looking at products data. content://com.example.android.inventoryapp/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PRODUCTS = "products";

    /**
     * Inner class that defines constant values for the products database table.
     * Each entry in the table represents a single product.
     */
    public static final class ProductEntry implements BaseColumns {

        //inside each of the Entry classes in the contract, we create a full URI for the class
        //as a constant called CONTENT_URI.
        //The Uri.withAppendedPath() method appends the BASE_CONTENT_URI
        //(which contains the scheme and the content authority) to the path segment.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single products.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        //Name of database table for products.
        public final static String TABLE_NAME = "products";

        //Unique ID number for the product (only for use in the database table).
        //Type: INTEGER
        public final static String _ID = BaseColumns._ID;

        //Name of the product. Type: TEXT
        public final static String COLUMN_PRODUCT_NAME = "product_name";

        //Product price. Type: REAL
        public final static String COLUMN_PRODUCT_PRICE = "product_price";

        //Product image. Type: TEXT
        public final static String COLUMN_PRODUCT_IMAGE = "product_image";

        //Product quantity. Type: INTEGER
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        //Product supplier. Type: TEXT
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";

        //Product suppliers e-mail. Type: TEXT
        public final static String COLUMN_SUPPLIER_EMAIL = "supplier_email";

        //Product suppliers phone number. Type: TEXT
        public final static String COLUMN_SUPPLIER_PHONE = "supplier_phone";
    }
}
