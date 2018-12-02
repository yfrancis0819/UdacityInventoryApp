package com.example.android.udacityinventoryapp.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

        private InventoryContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.udacityinventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "items";

    public static abstract class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

    public final static String TABLE_NAME ="items";

    public final static String _ID = BaseColumns._ID;
    public final static String COLUMN_ITEM_NAME = "Item_Name";
    public final static String COLUMN_ITEM_PRICE = "Price";
    public final static String COLUMN_ITEM_QUANTITY = "Quantity";
    public final static String COLUMN_ITEM_SUPPLIER_NAME = "Supplier_Name";
    public final static String COLUMN_ITEM_SUPPLIER_PHONE_NUMBER = "Supplier_Phone_Number";
    public final static String COLUMN_ITEM_CAN_SELL = "sell";

        public static final int CAN_SELL_UNKNOWN = 0;
        public static final int CAN_SELL_YES = 1;
        public static final int CAN_SELL_NO = 2;
    }
}
