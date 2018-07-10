package com.udacity.inventory.adapters;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
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

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.tv_product_name);
        TextView quantityTextView = view.findViewById(R.id.tv_product_quantity);
        TextView priceTextView = view.findViewById(R.id.tv_product_price);

        int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        final int id = cursor.getInt(idColumnIndex);
        Button mSaleButton = view.findViewById(R.id.btn_sale_button);
        mSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Id od clicked product: " + String.valueOf(id), Toast.LENGTH_SHORT).show();

//                int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
//                new CursorLoader(this, " dds", " sdsds", null, null,  null);

            }
        });

        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int statusColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_STATUS);

        String productName = cursor.getString(nameColumnIndex);
        int productQuantity = cursor.getInt(quantityColumnIndex);
        double productPrice = cursor.getDouble(priceColumnIndex);
        int productStatus = cursor.getInt(statusColumnIndex);

        priceTextView.setText(String.valueOf(productPrice));
        nameTextView.setText(productName);

        if (productStatus == 0 && productQuantity >= 1){
            quantityTextView.setText(String.valueOf(productQuantity));
            quantityTextView.setTextColor(context.getResources().getColor(R.color.colorSpinnerAvailable));
        }else /*if (productStatus == 1  && productQuantity == 0)*/{
            quantityTextView.setText(R.string.product_status_out_of_stock);
            quantityTextView.setTextColor(context.getResources().getColor(R.color.colorSpinnerOutOfStock));
        }


        if(productStatus == 2){
            quantityTextView.setText(R.string.product_status_out_of_market);
            quantityTextView.setTextColor(context.getResources().getColor(R.color.colorSpinnerOutOfMarket));
        }
    }
}

