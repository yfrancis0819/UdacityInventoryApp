package com.example.android.udacityinventoryapp.Data;

import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class InventoryContract {

        private InventoryContract(){}

public static abstract class InventoryEntry implements BaseColumns {


    public final static String TABLE_NAME ="inventory";

    public final static String _ID = BaseColumns._ID;
    public final static String COLUMN_ITEM_NAME = "Item_Name";
    public final static String COLUMN_ITEM_PRICE = "Price";
    public final static String COLUMN_ITEM_QUANTITY = "Quantity";
    public final static String COLUMN_ITEM_SUPPLIER_NAME = "Supplier_Name";
    public final static String COLUMN_ITEM_SUPPLIER_PHONE_NUMBER = "Supplier_Phone_Number";


}}
