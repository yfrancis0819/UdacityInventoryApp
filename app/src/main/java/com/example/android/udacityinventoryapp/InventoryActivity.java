package com.example.android.udacityinventoryapp;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.example.android.udacityinventoryapp.Data.InventoryContract;
import com.example.android.udacityinventoryapp.Data.InventoryContract.InventoryEntry;
import android.app.LoaderManager;

public class InventoryActivity  extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{
    InventoryCursorAdapter mCursorAdapter;

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

        ListView inventoryListView = (ListView) findViewById(R.id.list_view_inventory);
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);
        mCursorAdapter = new InventoryCursorAdapter ( this, null );
        inventoryListView.setAdapter ( mCursorAdapter );

        inventoryListView.setOnItemClickListener( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri currentItemUri = ContentUris.withAppendedId (InventoryEntry.CONTENT_URI, id );
                Intent intent = new Intent (InventoryActivity.this, EditorActivity.class);
                intent.setData(currentItemUri);
                startActivity ( intent );
            }
        } );
        getLoaderManager ().initLoader ( 0,
                null, this);
    }

    private void insertItem() {

        ContentValues values = new ContentValues();

        values.put(InventoryEntry.COLUMN_ITEM_NAME, "Fritos");
        values.put(InventoryEntry.COLUMN_ITEM_PRICE, 1);
        values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, 3);
        values.put(InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME, "Sam's");
        values.put(InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER, "732-956-9087");

        Uri newUri = getContentResolver().insert
                (InventoryEntry.CONTENT_URI, values);
    }

    private void deleteAllItems() {
        int rowsDeleted =
                getContentResolver().
                        delete(InventoryContract.InventoryEntry.CONTENT_URI,
                                null, null);
        Log.v("Inventory Activity", rowsDeleted + " rows deleted from Inventory Database");
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item){

        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
        }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this,
                InventoryContract.InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor (data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}




