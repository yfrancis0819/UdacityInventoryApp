package com.example.android.udacityinventoryapp.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private static final int ITEMS = 100;
    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int ITEM_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private InventoryDbHelper mDbHelper;

    static {

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS + "/#", ITEM_ID);
    }
    /**
     * Initialize the provider and the database helper object.
     */

    @Override
    public boolean onCreate() {
       mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case ITEM_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                    selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

                default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri ( getContext ().getContentResolver (), uri );

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertItem(Uri uri, ContentValues values)throws IllegalArgumentException {

        InventoryCheckInfo checkInfo = new InventoryCheckInfo ();

        String name = values.getAsString ( InventoryContract.InventoryEntry.COLUMN_ITEM_NAME );
        String quantity = values.getAsString (InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY );
        String price = values.getAsString ( InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE );
        String supplier = values.getAsString ( InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME );
        String phone = values.getAsString (InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER );
        if (checkInfo.InventoryCheckInfoTrue ( name, quantity, price, supplier, phone )) {

            SQLiteDatabase database = mDbHelper.getWritableDatabase ();

            long id = database.insert ( InventoryContract.InventoryEntry.TABLE_NAME, null, values );

            if (id == -1) {
                Toast.makeText ( getContext (), "not added", Toast.LENGTH_SHORT ).show ();
                return null;
            }
            getContext ().getContentResolver ().notifyChange ( uri, null );

            return ContentUris.withAppendedId ( uri, id );
        } else {
            return null;
        }
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    public int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME)) {

            String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);

            if (name == null) {

                throw new IllegalArgumentException("Item requires a name");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE)) {

            String price = values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Price requires valid amount");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY)) {

            Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY);

            if (quantity != null && quantity < 0) {

                throw new IllegalArgumentException("Item requires valid quantity");
            }
        }

        if (values.containsKey (InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME)) {

            String supplier = values.getAsString (InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME);

            if (supplier == null) {

                throw new IllegalArgumentException ( "Item requires valid supplier" );
            }
        }

        if (values.containsKey (InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER)) {

            String phone = values.getAsString (InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER);

            if (phone == null) {

                throw new IllegalArgumentException ( "Supplier requires valid phone number" );
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update ( InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs );

        if (rowsUpdated != 0) {
            getContext ().getContentResolver ().notifyChange ( uri, null );
        }
        // Return the number of rows updated
        return rowsUpdated;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                rowsDeleted = database.delete ( InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs );
                break;
            case ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete ( InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs );
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext ().getContentResolver ().notifyChange ( uri, null );
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}