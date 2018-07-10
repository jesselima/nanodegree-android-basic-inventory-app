package com.udacity.inventory.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;


public final class ProductContract {

    private ProductContract() {}

    public static final String CONTENT_AUTHORITY = "com.udacity.inventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS = "products";

    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME ="name";
        public final static String COLUMN_PRODUCT_BRAND = "brand";
        public final static String COLUMN_PRODUCT_DESCRIPTION = "description";
        public final static String COLUMN_PRODUCT_CATEGORY = "category";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_DISCOUNT = "discount";
        public final static String COLUMN_PRODUCT_STATUS = "status";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_SUPPLIER = "supplier";
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE = "phone";
        public final static String COLUMN_PRODUCT_SUPPLIER_EMAIL = "email";
        public final static String COLUMN_PRODUCT_ENTRY_DATE = "entry_date";
        public final static String COLUMN_PRODUCT_UPDATED = "updated";
        public final static String COLUMN_PRODUCT_PICTURE = "picture";

        public static final int PRODUCT_STATUS_AVAILABLE = 0;
        public static final int PRODUCT_STATUS_OUT_OF_STOCK = 1;
        public static final int PRODUCT_STATUS_OUT_OF_MARKET = 2;

        public static boolean checkSelectedProductStatus(int availability) {
            if (availability == PRODUCT_STATUS_OUT_OF_STOCK || availability == PRODUCT_STATUS_OUT_OF_MARKET || availability == PRODUCT_STATUS_AVAILABLE) {
                return true;
            }
            return false;
        }



    }

}

