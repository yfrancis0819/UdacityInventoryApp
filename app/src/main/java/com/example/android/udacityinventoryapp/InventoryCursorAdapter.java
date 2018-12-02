package com.example.android.udacityinventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.udacityinventoryapp.Data.InventoryContract;
public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView supplierTextView = (TextView) view.findViewById(R.id.sup_name);
        TextView phoneTextView = (TextView) view.findViewById(R.id.sup_phone);

        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
        int supplierColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME);
        int phoneColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER);

        String itemName = cursor.getString(nameColumnIndex);
        final String itemQuantity = cursor.getString(quantityColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        String supplierName = cursor.getString(supplierColumnIndex);
        String supplierPhoneNumber = cursor.getString(phoneColumnIndex);

        nameTextView.setText ( itemName );
        quantityTextView.setText ( itemQuantity );
        priceTextView.setText ( itemPrice );
        supplierTextView.setText ( supplierName );
        phoneTextView.setText ( supplierPhoneNumber );
        TextView soldButton = (TextView) view.findViewById(R.id.sale_button);
        final TextView saleTextView = (TextView) view.findViewById(R.id.sale_view);
        int saleColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
        saleTextView.setText(cursor.getString(saleColumnIndex));
        //set click listener for this view
        soldButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                int changeQuantity = Integer.parseInt(quantityTextView.getText().toString().trim());
                if (changeQuantity > 0) {
                    changeQuantity -= 1;
                    quantityTextView.setText(Integer.toString(changeQuantity));

                    long id_number = Integer.parseInt(saleTextView.getText().toString());

                    Uri itemSelected = ContentUris.withAppendedId( InventoryContract.InventoryEntry.CONTENT_URI,id_number);
                    ContentValues values = new ContentValues ();

                    values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY,quantityTextView.getText().toString());

                    int rowsAffected = context.getContentResolver().update(itemSelected, values, null, null);
                    if (rowsAffected == 0) {
                        Toast.makeText(context, R.string.sale_eror, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, R.string.one_sold, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.no_stock, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}