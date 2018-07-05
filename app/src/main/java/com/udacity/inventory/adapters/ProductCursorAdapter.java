package com.udacity.inventory.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.udacity.inventory.R;
import com.udacity.inventory.data.ProductContract.ProductEntry;


public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.tv_product_name);
        TextView brandTextView = view.findViewById(R.id.tv_product_brand);
        TextView categoryTextView = view.findViewById(R.id.tv_product_category);

        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int brandColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_BRAND);
        int categoryColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_CATEGORY);

        String productName = cursor.getString(nameColumnIndex);
        String productBrand = cursor.getString(brandColumnIndex);
        String productCategory = cursor.getString(categoryColumnIndex);

        nameTextView.setText(productName);
        brandTextView.setText(productBrand);
        categoryTextView.setText(productCategory);
    }
}

