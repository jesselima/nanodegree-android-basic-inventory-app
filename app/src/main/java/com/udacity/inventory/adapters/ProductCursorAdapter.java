package com.udacity.inventory.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.inventory.R;
import com.udacity.inventory.data.ProductContract.ProductEntry;


public class ProductCursorAdapter extends CursorAdapter {

    // Global toast object to avoid queue with many toasts.
    Toast toast;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // References the view on the UI
        TextView nameTextView = view.findViewById(R.id.tv_product_name);
        TextView quantityTextView = view.findViewById(R.id.tv_product_quantity);
        TextView priceTextView = view.findViewById(R.id.tv_product_price);

        // References for the row and columns return from the database
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int statusColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_STATUS);

        // Store the result of each row and column in variables to update the UI
        final String productName = cursor.getString(nameColumnIndex);
        final int productQuantity = cursor.getInt(quantityColumnIndex);
        double productPrice = cursor.getDouble(priceColumnIndex);
        int productStatus = cursor.getInt(statusColumnIndex);

        // Implementation to sale item from the clicked item list. Get the ID in the rows.
        // When user clicks in a item list the get the product ID and actual QUANTITY and updates the database.
        int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        final int id = cursor.getInt(idColumnIndex);
        Button mSaleButton = view.findViewById(R.id.btn_sale_button);
        mSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productQuantity == 0) {
                    doToast(context, context.getString(R.string.this_product_out_of_stock));
                } else {
                    saleItem(context, id, (productQuantity - 1));
                    doToast(context, context.getString(R.string.you_sold_one_) + productName + context.getString(R.string._from_the_store));
                }
            }
        });

        /* Update the UI with the nes data
         *
         */
        priceTextView.setText(String.valueOf(productPrice));
        nameTextView.setText(productName);
        //** Change the quantity TextView according to the quantity value.
        if (productStatus == 0 && productQuantity >= 1) {
            quantityTextView.setText(String.valueOf(productQuantity));
            quantityTextView.setTextColor(context.getResources().getColor(R.color.colorSpinnerAvailable));
        } else {
            quantityTextView.setText(R.string.product_status_out_of_stock);
            quantityTextView.setTextColor(context.getResources().getColor(R.color.colorSpinnerOutOfStock));
        }
        if (productStatus == 2) {
            quantityTextView.setText(R.string.product_status_out_of_market);
            quantityTextView.setTextColor(context.getResources().getColor(R.color.colorSpinnerOutOfMarket));
        }
    }

    /**
     * This method is called when the user clicks on the SALE button on a item list, the product quantity is updates
     * 
     * @param context is the application context.
     * @param productId is the ID of the product in the database
     * @param quantity is the quantity of the product in the database.
     */
    private static void saleItem(Context context, int productId, int quantity) {

        Uri newUri = ProductEntry.CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        String where = ProductEntry._ID + " =? ";
        String[] whereVal = {Long.toString(productId)};

        resolver.update(newUri, values, where, whereVal);
    }

    /**
     * This method makes the reuse of toast object to avoid toasts queue
     *
     * @param string is the text you want to show in the toast.
     */
    private void doToast(Context context, String string) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
        toast.show();
    }

}

