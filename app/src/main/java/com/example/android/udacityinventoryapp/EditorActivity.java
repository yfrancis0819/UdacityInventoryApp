package com.example.android.udacityinventoryapp;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.udacityinventoryapp.Data.InventoryContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_ITEM_LOADER = 0;

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private Uri mCurrentItemUri;

    /**
     * EditText field to enter the product name
     */
    private EditText mNameEditText;
    /**
     * EditText field to enter the product price
     */
    private EditText mPriceEditText;
    /**
     * EditText field to enter the quantity
     */
    private EditText mQuantityEditText;

    private EditText mSupplierPhoneNumberEditText;

    private EditText mSupplierNameEditText;

    private boolean mItemHasChanged = false;
    private Spinner mCanSellSpinner;
    private int mCanSell = InventoryContract.InventoryEntry.CAN_SELL_UNKNOWN;
    private Button moreButton;
    private Button lessButton;
    private Button phoneButton;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener () {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent ();
        mCurrentItemUri = intent.getData ();

        if (mCurrentItemUri == null) {
            setTitle ( getString ( R.string.editor_activity_title_new_product ) );
            invalidateOptionsMenu ();
        } else {
            setTitle ( getString ( R.string.edit_item ) );


            getLoaderManager ().initLoader ( EXISTING_ITEM_LOADER, null,  this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_item_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);
        mCanSellSpinner = (Spinner) findViewById ( R.id.spinner_available );

        mNameEditText.setOnTouchListener ( mTouchListener );
        mQuantityEditText.setOnTouchListener ( mTouchListener );
        mCanSellSpinner.setOnTouchListener ( mTouchListener );
        mPriceEditText.setOnTouchListener ( mTouchListener );
        mSupplierNameEditText.setOnTouchListener ( mTouchListener );
        mSupplierPhoneNumberEditText.setOnTouchListener ( mTouchListener );

    moreButton = (Button) findViewById ( R.id.increment_button );
    lessButton = (Button) findViewById ( R.id.decrement_button );
    phoneButton = (Button) findViewById ( R.id.call_supplier );
    setupSpinner ();

        moreButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Integer changeQuantity;

                try {

                    changeQuantity = Integer.parseInt ( mQuantityEditText.getText ().toString () );
                    changeQuantity += 1;
                    if (changeQuantity > 100) {
                        changeQuantity = 100;
                    }

                } catch (Exception e) {
                    changeQuantity = 1;
                }

                mQuantityEditText.setText ( Integer.toString ( changeQuantity ) );
            }
        } );
        lessButton.setOnClickListener ( new View.OnClickListener () {

            @Override
            public void onClick(View v) {
                Integer changeQuantity;

                try {
                    changeQuantity = Integer.parseInt ( mQuantityEditText.getText ().toString () );
                    changeQuantity -= 1;

                    if (changeQuantity < 0) {
                        changeQuantity = 0;
                    }
                } catch (Exception e) {
                    changeQuantity = 0;
                }

                mQuantityEditText.setText ( Integer.toString ( changeQuantity ) );
            }
        } );
        phoneButton.setOnClickListener ( new View.OnClickListener () {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( Intent.ACTION_DIAL );
                intent.setData ( Uri.parse ( "tel:" + mSupplierPhoneNumberEditText.getText ().toString () ) );
                startActivity ( intent );
            }
        } );
    }

    private void setupSpinner() {
        ArrayAdapter sellSpinnerAdapter = ArrayAdapter.createFromResource ( this,
                R.array.array_available_options, android.R.layout.simple_spinner_item );
        sellSpinnerAdapter.setDropDownViewResource ( android.R.layout.simple_dropdown_item_1line );
        mCanSellSpinner.setAdapter ( sellSpinnerAdapter );
        mCanSellSpinner.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener () {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition ( position );
                if (!TextUtils.isEmpty ( selection )) {
                    if (selection.equals ( getString ( R.string.can_sell_yes ) )) {
                        mCanSell = InventoryContract.InventoryEntry.CAN_SELL_YES;
                    } else if (selection.equals ( getString ( R.string.can_sell_no ) )) {
                        mCanSell = InventoryContract.InventoryEntry.CAN_SELL_NO;
                    } else {
                        mCanSell = InventoryContract.InventoryEntry.CAN_SELL_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCanSell = InventoryContract.InventoryEntry.CAN_SELL_UNKNOWN;
            }
        } );
    }

    private void saveItem() {
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();

        int quantity;
        try {
            quantity = Integer.parseInt ( quantityString );
        } catch (Exception e) {
            quantity = 0;
        }

            if (mCurrentItemUri == null &&
                    TextUtils.isEmpty ( nameString ) && TextUtils.isEmpty ( quantityString ) &&
                    TextUtils.isEmpty ( supplierNameString ) && TextUtils.isEmpty ( priceString )
                    && TextUtils.isEmpty ( supplierPhoneNumberString )) {

                if (!checkInfo ( nameString, quantityString, priceString, supplierNameString, supplierPhoneNumberString )) {
                    return;
                }
            }
            ContentValues values = new ContentValues ();

        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME, nameString);
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, quantityString);
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE, priceString);
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME, supplierNameString);
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_CAN_SELL, mCanSell);
        values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

         if (mCurrentItemUri == null) {

                Uri newUri = getContentResolver ().insert ( InventoryContract.InventoryEntry.CONTENT_URI, values );
                if (newUri == null) {
                    Toast.makeText ( this, getString ( R.string.error_saving ),
                            Toast.LENGTH_SHORT ).show ();
                } else {
                    Toast.makeText ( this, getString ( R.string.item_saved ),
                            Toast.LENGTH_SHORT ).show ();
                }
            } else {

                int rowsAffected = getContentResolver ().update ( mCurrentItemUri, values, null, null );
                if (!checkInfo ( nameString, quantityString, priceString, supplierNameString, supplierPhoneNumberString )) {

                    return;
                }

                if (rowsAffected == 0) {
                    Toast.makeText ( this, getString ( R.string.save_error ),
                            Toast.LENGTH_SHORT ).show ();
                } else {
                    Toast.makeText ( this, getString ( R.string.item_saved_id ),
                            Toast.LENGTH_SHORT ).show ();
                }
            }

        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu options from the res/menu/menu_editor.xml file.
            // This adds menu items to the app bar.
            getMenuInflater ().inflate ( R.menu.menu_editor, menu );
            return true;
        }
        /**
         * This method is called after invalidateOptionsMenu(), so that the
         * menu can be updated (some menu items can be hidden or made visible).
         */
        @Override
        public boolean onPrepareOptionsMenu(Menu menu) {
            super.onPrepareOptionsMenu ( menu );
            if (mCurrentItemUri == null) {
                MenuItem menuItem = menu.findItem ( R.id.action_delete );
                menuItem.setVisible ( false );
            }
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId ()) {
                case R.id.action_save:
                    saveItem ();
                    return true;
                case R.id.action_delete:
                    showDeleteConfirmationDialog ();
                    return true;
                case android.R.id.home:
                    NavUtils.navigateUpFromSameTask ( this );
                    if (!mItemHasChanged) {
                        NavUtils.navigateUpFromSameTask ( EditorActivity.this );
                        return true;
                    }
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener () {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NavUtils.navigateUpFromSameTask ( EditorActivity.this );
                                }
                            };
                    showUnsavedChangesDialog ( discardButtonClickListener );
                    return true;
            }
            return super.onOptionsItemSelected ( item );
        }

        /**
         * This method is called when the back button is pressed.
         */
        @Override
        public void onBackPressed() {
            if (!mItemHasChanged) {
                super.onBackPressed ();
                return;
            }
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener () {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish ();
                        }
                    };
            showUnsavedChangesDialog ( discardButtonClickListener );
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            String[] projection = {
                    InventoryContract.InventoryEntry._ID,
                    InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                    InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY,
                    InventoryContract.InventoryEntry.COLUMN_ITEM_CAN_SELL,
                    InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE,
                    InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME,
                    InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER};

            // This loader will execute the ContentProvider's query method on a background thread
            return new CursorLoader( this,   // Parent activity context
                    mCurrentItemUri,         // Query the content URI for the current pet
                    projection,             // Columns to include in the resulting Cursor
                    null,                   // No selection clause
                    null,                   // No selection arguments
                    null );                  // Default sort order
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor == null || cursor.getCount () < 1) {
                return;
            }

            if (cursor.moveToFirst ()) {
                // Find the columns of product attributes that we're interested in
                int nameColumnIndex = cursor.getColumnIndex ( InventoryContract.InventoryEntry.COLUMN_ITEM_NAME );
                int quantityColumnIndex = cursor.getColumnIndex ( InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY );
                int canSellColumnIndex = cursor.getColumnIndex (InventoryContract.InventoryEntry.COLUMN_ITEM_CAN_SELL );
                int priceColumnIndex = cursor.getColumnIndex ( InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE );
                int supplierColumnIndex = cursor.getColumnIndex ( InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME );
                int phoneColumnIndex = cursor.getColumnIndex ( InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE_NUMBER );

                String name = cursor.getString ( nameColumnIndex );
                String price = cursor.getString ( priceColumnIndex );
                int quantity = cursor.getInt ( quantityColumnIndex );
                int canSell = cursor.getInt ( canSellColumnIndex );
                int phone = cursor.getInt ( phoneColumnIndex );
                String supplier = cursor.getString ( supplierColumnIndex );

                mNameEditText.setText ( name );
                mPriceEditText.setText ( price );
                mSupplierNameEditText.setText ( supplier );
                mQuantityEditText.setText ( Integer.toString ( quantity ) );
                mSupplierPhoneNumberEditText.setText  ( Integer.toString ( phone ) );

                switch (canSell) {
                    case InventoryContract.InventoryEntry.CAN_SELL_YES:
                        mCanSellSpinner.setSelection ( 1 );
                        break;
                    case InventoryContract.InventoryEntry.CAN_SELL_NO:
                        mCanSellSpinner.setSelection ( 2 );
                        break;
                    default:
                        mCanSellSpinner.setSelection ( 0 );
                        break;
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mNameEditText.setText ( "" );
            mQuantityEditText.setText ( "" );
            mSupplierNameEditText.setText ( "" );
            mPriceEditText.setText ( "" );
            mSupplierPhoneNumberEditText.setText ( "" );
            mCanSellSpinner.setSelection ( 0 );
        }

        private void showUnsavedChangesDialog(
                DialogInterface.OnClickListener discardButtonClickListener) {

            AlertDialog.Builder builder = new AlertDialog.Builder ( this );
            builder.setMessage ( R.string.unsaved_changes_dialog_msg );
            builder.setPositiveButton ( R.string.discard, discardButtonClickListener );
            builder.setNegativeButton ( R.string.keep_editing, new DialogInterface.OnClickListener () {
                public void onClick(DialogInterface dialog, int id) {

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            } );
            AlertDialog alertDialog = builder.create ();
            alertDialog.show ();
        }

        /**
         * Prompt the user to confirm that they want to delete this item.
         */
        private void showDeleteConfirmationDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder ( this );
            builder.setMessage ( R.string.delete_dialog_msg );
            builder.setPositiveButton ( R.string.delete_button, new DialogInterface.OnClickListener () {
                public void onClick(DialogInterface dialog, int id) {
                    deleteItem();
                }
            } );
            builder.setNegativeButton ( R.string.cancel, new DialogInterface.OnClickListener () {
                public void onClick(DialogInterface dialog, int id) {

                    if (dialog != null) {
                        dialog.dismiss ();
                    }
                }
            } );
            AlertDialog alertDialog = builder.create ();
            alertDialog.show ();
        }

        private void deleteItem() {
            if (mCurrentItemUri != null) {
                int rowsDeleted = getContentResolver ().delete ( mCurrentItemUri, null, null );
                if (rowsDeleted == 0) {
                    Toast.makeText ( this, getString ( R.string.editor_delete_item_failed ),
                            Toast.LENGTH_SHORT ).show ();
                } else {
                    Toast.makeText ( this, getString ( R.string.editor_delete_item_successful ),
                            Toast.LENGTH_SHORT ).show ();
                }
                finish ();
            }
        }

        private boolean checkInfo(String nameString, String quantity, String
        price, String supplierString, String phoneString) {
            if (TextUtils.isEmpty ( nameString )) {
                Toast.makeText ( this, getResources ().getString ( R.string.empty_name ), Toast.LENGTH_SHORT ).show ();
                return false;
            }
            if (TextUtils.isEmpty ( quantity )) {
                Toast.makeText ( this, getResources ().getString ( R.string.empty_quantity ), Toast.LENGTH_SHORT ).show ();
                return false;
            }
            if (TextUtils.isEmpty ( supplierString )) {
                Toast.makeText ( this, getResources ().getString ( R.string.empty_supplier ), Toast.LENGTH_SHORT ).show ();
                return false;
            }
            if (TextUtils.isEmpty ( price )) {
                Toast.makeText ( this, getResources ().getString ( R.string.empty_price ), Toast.LENGTH_SHORT ).show ();
                return false;
            }
            if (TextUtils.isEmpty ( phoneString )) {
                Toast.makeText ( this, getResources ().getString ( R.string.empty_phone ), Toast.LENGTH_SHORT ).show ();
                return false;
            }

            return true;
        }

    }
