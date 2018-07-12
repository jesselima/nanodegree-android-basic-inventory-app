package com.udacity.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.udacity.inventory.adapters.ProductCursorAdapter;
import com.udacity.inventory.data.ProductContract.ProductEntry;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.Random;


public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the product data loader 
    private static final int PRODUCT_LOADER = 0;

    // Adapter for the ListView 
    ProductCursorAdapter mCursorAdapter;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Open EditorActivity to add new product
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the product data
        ListView productListView = findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        // Start the loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_STATUS};

        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    /**
     * THE METHOD BELOW IS FOR TESTING PURPOSE ONLY Helper method to insert hardcoded product data into the database. For debugging purposes only.
     */
    private void insertSampleProducts() {

        byte[] productDummyImageInBytes = convertBitmapToBytes();

        int sampleDataNumber = 6;

        for (int i = 1; i < sampleDataNumber; i++) {

            long timeStamp = System.currentTimeMillis() / 1000;
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Product Test " + i);
            values.put(ProductEntry.COLUMN_PRODUCT_BRAND, "Brand " + i);
            values.put(ProductEntry.COLUMN_PRODUCT_DESCRIPTION, "Product " + i + " description...");
            values.put(ProductEntry.COLUMN_PRODUCT_CATEGORY, "Category of product " + i);

            /* INSERT SAMPLES PRODUCTS */
                    Random randomGenerator = new Random();
                    double randomPrice = 30 + (150 - 30) * randomGenerator.nextDouble();
                    DecimalFormat df = new DecimalFormat("#.00");
                    double price = Double.parseDouble(df.format(randomPrice));
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
            values.put(ProductEntry.COLUMN_PRODUCT_DISCOUNT, 10);
            values.put(ProductEntry.COLUMN_PRODUCT_STATUS, ProductEntry.PRODUCT_STATUS_AVAILABLE);

                    Random randomQuantityGenerator = new Random();
                    int randomQuantity = randomQuantityGenerator.nextInt(300) + 10;
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, randomQuantity);
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, "Supplier of product " + i);

                    Random randomPhoneGenerator = new Random();
                    int randomPhone = randomPhoneGenerator.nextInt(700) + 200;
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, "(" + String.valueOf(randomPhone) + ") " + String.valueOf(randomPhone - 30) + "-" + String.valueOf(randomPhone - 15) + "-" + String.valueOf(randomPhone - 10));
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, "contact@supplier" + i + ".com");
            values.put(ProductEntry.COLUMN_PRODUCT_ENTRY_DATE, timeStamp); // Epoch timestamp: 1528046360
            values.put(ProductEntry.COLUMN_PRODUCT_UPDATED, timeStamp); // Epoch timestamp: 1528046360
            values.put(ProductEntry.COLUMN_PRODUCT_PICTURE, productDummyImageInBytes); // Sample product image.

            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        }
        doToast(String.valueOf(sampleDataNumber - 1) + " test products added to the database.");
    }

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        doToast(rowsDeleted + " products deleted from inventory database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertSampleProducts();
                return true;
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_data_warning);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllProducts();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This method makes the reuse of toast object to avoid toasts queue
     *
     * @param string is the text you want to show in the toast.
     */
    private void doToast(String string) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Convert the dummy product image into an array of bytes to be stored in the database when the user do not set a product picture.
     *
     * @return a image as an array of bytes
     */
    public byte[] convertBitmapToBytes() {
        Bitmap bitmapFromResource = BitmapFactory.decodeResource(getResources(), R.drawable.dummy_product_image);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapFromResource.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}
