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

        // Setup the item click listener for the product item list
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                /** Form the content URI that represents the specific product that was clicked on,
                * {@link ProductEntry#CONTENT_URI}.
                * For example, the URI would be "content://com.udacity.android.inventory/products/1"
                * if the product with ID 1 was clicked on.
                */
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                // The content uri with the id of the clicked item will be sent to the editor.
                // When the editor starts it will always check if this data is available. if the uri is available
                // fill the form with data of the clicked item. So the user can change the product data and save it.
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        // Start the loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection for the query in the database. It represents what column data we want to retreive.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_STATUS};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    /**
     * Update {@link ProductCursorAdapter} with this new cursor containing updated product data
     * @param loader is the current CursorLoader.
     * @param data is the updated product data.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    /**
     * Callback called when the data needs to be deleted.
     * @param loader is the current CursorLoader.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
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



    //**********************************
    // TEST SECTION START **************
    //**********************************

    /**
     * THE METHOD BELOW IS FOR TESTING PURPOSE ONLY.
     * It's a Helper method to INSERT hardcoded product data into the database.
     */
    private void insertSampleProducts() {

        byte[] productDummyImageInBytes = convertBitmapToBytes();
        int sampleDataNumber = 6;

        for (int i = 1; i < sampleDataNumber; i++) {

            long timeStamp = System.currentTimeMillis() / 1000;
            ContentValues values = new ContentValues();

            values.put(ProductEntry.COLUMN_PRODUCT_NAME, getString(R.string.product_test_name) + i);
            values.put(ProductEntry.COLUMN_PRODUCT_BRAND, getString(R.string.product_test_brand) + i);
            values.put(ProductEntry.COLUMN_PRODUCT_DESCRIPTION, getString(R.string.product_test_description) + i + getString(R.string.product_test_description_continue));
            values.put(ProductEntry.COLUMN_PRODUCT_CATEGORY, getString(R.string.product_test_category) + i);

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
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, getString(R.string.product_test_supplier_name) + i);

            Random randomPhoneGenerator = new Random();
            int randomPhone = randomPhoneGenerator.nextInt(700) + 200;

            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, "(" + String.valueOf(randomPhone) + ") " + String.valueOf(randomPhone - 30) + "-" + String.valueOf(randomPhone - 15) + "-" + String.valueOf(randomPhone - 10));
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, getString(R.string.product_test_supplier_email_1) + i + getString(R.string.product_test_dot_com));
            values.put(ProductEntry.COLUMN_PRODUCT_ENTRY_DATE, timeStamp); // Epoch timestamp: 1528046360
            values.put(ProductEntry.COLUMN_PRODUCT_UPDATED, timeStamp); // Epoch timestamp: 1528046360
            values.put(ProductEntry.COLUMN_PRODUCT_PICTURE, productDummyImageInBytes); // Sample product image.

            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        }
        doToast(String.valueOf(sampleDataNumber - 1) + getString(R.string.test_products_added_to_database));
    }

    /**
     * THE METHOD BELOW IS FOR TESTING PURPOSE ONLY.
     * It's a Helper method to DELETE hardcoded product data into the database.
     */
    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        doToast(rowsDeleted + getString(R.string.products_deleted_from_database));
    }

    /**
     * THE MENU BELOW IS FOR TESTING PURPOSE ONLY.
     * It's a Helper method to insert hardcoded product data into the database.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    /**
     * This method handles the clicked item menu
     */
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

    /**
     * When "Delete all entries is clicked this confirmation dialog pops up.
     */
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
    //**********************************
    // TEST SECTION END ****************
    //**********************************


    /*
    *   Resources that helped me to solve some problems during the app development:
    *
    *   Official Documentation:
    *
    *   https://developer.android.com/training/camera/photobasics
    *   https://developer.android.com/guide/components/intents-common
    *   https://developer.android.com/reference/android/support/v4/content/CursorLoader
    *
    *   Stack Overflow:
    *
    *   https://stackoverflow.com/questions/2558591/remove-listview-items-in-android/5344958#5344958
    *   https://stackoverflow.com/questions/3680637/generate-a-random-double-in-a-range
    *   https://stackoverflow.com/questions/8819842/best-way-to-format-a-double-value-to-2-decimal-places
    *   https://stackoverflow.com/questions/3680637/generate-a-random-double-in-a-range
    *   https://stackoverflow.com/questions/8065114/how-to-print-a-double-with-two-decimals-in-android
    *   https://stackoverflow.com/questions/2892615/how-to-remove-auto-focus-keyboard-popup-of-a-field-when-the-screen-shows-up
    *   https://stackoverflow.com/questions/7266572/how-to-save-images-from-camera-in-android-to-specific-folder
    *   https://stackoverflow.com/questions/7331310/how-to-store-image-as-blob-in-sqlite-how-to-retrieve-it
    *   https://stackoverflow.com/questions/4989182/converting-java-bitmap-to-byte-array
    *   https://stackoverflow.com/questions/13758560/android-bitmap-to-byte-array-and-back-skimagedecoderfactory-returned-null?lq=1
    *   https://stackoverflow.com/questions/8717333/converting-drawable-resource-image-into-bitmap
    *   https://stackoverflow.com/questions/40838370/sqlite-database-supports-blob-how-to-store-image-in-sqlite-in-android
    *   https://stackoverflow.com/questions/9357668/how-to-store-image-in-sqlite-database
    *   https://stackoverflow.com/questions/20709755/button-inside-listview-not-clickable
    *
    *   SQLite Official Documentation:
    *   https://sqlite.org/datatype3.html
    *
    *   Epoch Converter - Epoch & Unix Timestamp Conversion Tools
    *   https://www.epochconverter.com/
    *
    */
}
