package com.example.android.udacityinventoryapp.Data;

import android.text.TextUtils;

public class InventoryCheckInfo {public boolean InventoryCheckInfoTrue (String name, String quantity, String price, String supplier, String phone ) {
    boolean isGood = true;
    if (TextUtils.isEmpty(name)) {
        isGood = false;
    }

    if (TextUtils.isEmpty(quantity)) {
        isGood = false;
    }
    if (TextUtils.isEmpty(price)) {
        isGood = false;
    }
    if (TextUtils.isEmpty(supplier)) {
        isGood = false;
    }
    if (TextUtils.isEmpty(phone)) {
        isGood = false;
    }
    return isGood;
}
}
