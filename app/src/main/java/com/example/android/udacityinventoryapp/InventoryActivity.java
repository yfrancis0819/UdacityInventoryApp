package com.example.android.udacityinventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.android.udacityinventoryapp.Data.InventoryContract.InventoryEntry;

import com.example.android.udacityinventoryapp.Data.InventoryDbHelper;

public class InventoryActivity extends AppCompatActivity {

    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //**Set up a button to open the editor activity*/

        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        mDbHelper = new InventoryDbHelper(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME,
                InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER};


        // Perform a query on the pets table
        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        TextView displayView = (TextView) findViewById(R.id.text_view_inventory);
        try {

            displayView.setText("The inventory table contains " + cursor.getCount() + " items.\n\n");
            displayView.append(InventoryEntry._ID + " - " +
                    InventoryEntry.COLUMN_ITEM_NAME + " - " +
                    InventoryEntry.COLUMN_ITEM_PRICE + " - " +
                    InventoryEntry.COLUMN_ITEM_QUANTITY + " - " +
                    InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME + "-" +
                    InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER +"\n");

            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int breedColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
            int genderColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
            int weightColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);

                String currentName = cursor.getString(nameColumnIndex);
                String currentPrice = cursor.getString(breedColumnIndex);
                int currentQuantity = cursor.getInt(genderColumnIndex);
                int currentSupplierName = cursor.getInt(weightColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + "-" +
                        currentSupplierPhone));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */

    private void insertItem() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(InventoryEntry.COLUMN_ITEM_NAME, "Fritos");
        values.put(InventoryEntry.COLUMN_ITEM_PRICE, 1);
        values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, 3);
        values.put(InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME, "Sam's");
        values.put(InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER, "732-956-9087");


        long newRowID = db.insert(InventoryEntry.TABLE_NAME, null, values);


    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item){

        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertItem();
                displayDatabaseInfo();
                return true;
            //Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
