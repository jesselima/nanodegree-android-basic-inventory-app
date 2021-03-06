package com.udacity.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
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
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.inventory.data.ProductContract.ProductEntry;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Uri mCurrentProductUri;
    private EditText mNameEditText;
    private EditText mBrandEditText;
    private EditText mDescriptionEditText;
    private EditText mCategoryEditText;
    private EditText mPriceEditText;
    private EditText mDiscountEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;
    private EditText mSupplierEmailEditText;
    private TextView mEntryDateEditText, mUpdatedDateEditText;
    private Spinner mProductStatusSpinner;

    private ImageView mImageProductStatus;
    private ImageView mImageViewProduct;
    private Bitmap mImageProductBitmap;

    private Toast toast;
    private long currentProductEntryDate;
    private boolean isEditingProduct = false;

    private int mProductStatus = ProductEntry.PRODUCT_STATUS_AVAILABLE;
    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Get the currentProductUri
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();

            LinearLayout linearLayoutEntryDate = findViewById(R.id.layout_entry_date);
            linearLayoutEntryDate.setVisibility(View.GONE);
            LinearLayout linearLayoutLastUpdate = findViewById(R.id.layout_last_update);
            linearLayoutLastUpdate.setVisibility(View.GONE);
            LinearLayout linearLayoutContact = findViewById(R.id.layout_contact);
            linearLayoutContact.setVisibility(View.GONE);

            CardView cardQuantity = findViewById(R.id.card_quantity);
            cardQuantity.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mImageViewProduct = findViewById(R.id.iv_product_details_layout);

        mNameEditText = findViewById(R.id.edit_product_name);
        mBrandEditText = findViewById(R.id.edit_product_brand);
        mDescriptionEditText = findViewById(R.id.edit_product_description);
        mCategoryEditText = findViewById(R.id.edit_product_category);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mDiscountEditText = findViewById(R.id.edit_product_discount);
        mQuantityEditText = findViewById(R.id.edit_product_quantity);
        mSupplierEditText = findViewById(R.id.edit_product_supplier);
        mSupplierPhoneEditText = findViewById(R.id.edit_product_supplier_phone);
        mSupplierEmailEditText = findViewById(R.id.edit_product_supplier_email);

        mEntryDateEditText = findViewById(R.id.tv_entry_date);
        mUpdatedDateEditText = findViewById(R.id.tv_updated_date);
        mProductStatusSpinner = findViewById(R.id.spinner_status);
        mImageProductStatus = findViewById(R.id.iv_product_status);

        mNameEditText.setOnTouchListener(mTouchListener);
        mBrandEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mCategoryEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mDiscountEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mSupplierEmailEditText.setOnTouchListener(mTouchListener);
        mProductStatusSpinner.setOnTouchListener(mTouchListener);
        setupSpinner();

        Button btnAdd1Item = findViewById(R.id.btn_add_one_item);
        btnAdd1Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemAdd(1);
            }
        });
        Button btnAdd10Item = findViewById(R.id.btn_add_ten_item);
        btnAdd10Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemAdd(10);
            }
        });
        Button btnAdd50Item = findViewById(R.id.btn_add_fifty_item);
        btnAdd50Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemAdd(50);
            }
        });

        Button btnRemove1Item = findViewById(R.id.btn_remove_one_item);
        btnRemove1Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemRemove(1);
            }
        });
        Button btnRemove10Item = findViewById(R.id.btn_remove_ten_item);
        btnRemove10Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemRemove(10);
            }
        });
        Button btnRemove50Item = findViewById(R.id.btn_remove_fifty_item);
        btnRemove50Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemRemove(50);
            }
        });

        Button btnCallSupplier = findViewById(R.id.btn_call_supplier);
        btnCallSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSupplier();
            }
        });
        Button btnEmailSupplier = findViewById(R.id.btn_email_supplier);
        btnEmailSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!saveProduct()) {
                    doToast(getString(R.string.fill_all_form_fields));
                    return;
                }
                // Exit activity
                finish();
            }
        });

        Button btnDelete = findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        mImageViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

    }

    private void setupSpinner() {

        ArrayAdapter productStatusSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_status_options, android.R.layout.simple_spinner_item);

        productStatusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mProductStatusSpinner.setAdapter(productStatusSpinnerAdapter);
        mProductStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.product_status_available))) {
                        mProductStatus = ProductEntry.PRODUCT_STATUS_AVAILABLE;
                        /* Changes spinner font size and color. Color changes according to its position */
                        ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSpinnerAvailable));
                        ((TextView) parent.getChildAt(0)).setTextSize(16);
                        mImageProductStatus.setImageResource(R.drawable.ic_product_available_shadow);
                    } else if (selection.equals(getString(R.string.product_status_out_of_stock))) {
                        mProductStatus = ProductEntry.PRODUCT_STATUS_OUT_OF_STOCK;
                        /* Changes spinner font size and color. Color changes according to its position */
                        ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSpinnerOutOfStock));
                        ((TextView) parent.getChildAt(0)).setTextSize(16);
                        mImageProductStatus.setImageResource(R.drawable.ic_product_out_of_stock_shadow);
                    } else {
                        mProductStatus = ProductEntry.PRODUCT_STATUS_OUT_OF_MARKET;
                        /* Changes spinner font size and color. Color changes according to its position */
                        ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSpinnerOutOfMarket));
                        ((TextView) parent.getChildAt(0)).setTextSize(16);
                        mImageProductStatus.setImageResource(R.drawable.ic_product_deprecated_shadow);
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mProductStatus = ProductEntry.PRODUCT_STATUS_OUT_OF_STOCK;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                if (!saveProduct()) {
                    doToast(getString(R.string.fill_all_form_fields));
                    return false;
                }
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the product table
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_BRAND,
                ProductEntry.COLUMN_PRODUCT_DESCRIPTION,
                ProductEntry.COLUMN_PRODUCT_CATEGORY,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_DISCOUNT,
                ProductEntry.COLUMN_PRODUCT_STATUS,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_ENTRY_DATE,
                ProductEntry.COLUMN_PRODUCT_UPDATED,
                ProductEntry.COLUMN_PRODUCT_PICTURE,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // If the cursor has data
        isEditingProduct = true;

        // This method will return false if the cursor is empty.
        // https://developer.android.com/reference/android/database/Cursor#moveToFirst()
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int brandColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_BRAND);
            int descriptionColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_DESCRIPTION);
            int categoryColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_CATEGORY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int discountColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_DISCOUNT);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);
            int supplierEmailColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
            int statusColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_STATUS);
            int entryDateColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_ENTRY_DATE);
            int updatedColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_UPDATED);
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PICTURE);

            /* GET DATA FROM THE TABLE RESULT */
            String name = cursor.getString(nameColumnIndex);
            String brand = cursor.getString(brandColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            String category = cursor.getString(categoryColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int discount = cursor.getInt(discountColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);

            long entryDateLong = cursor.getInt(entryDateColumnIndex);
            // Convert the date to a friendly format
            DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(entryDateLong * 1000);
            String entryDate = formatter.format(calendar.getTime());
            // Product data has been return from data. Sou we need to set the currentProductEntryDate == to the product date returned.
            // This avoid to update the entry date while updating others products fields.
            currentProductEntryDate = entryDateLong;

            long updatedDateLong = cursor.getInt(updatedColumnIndex);
            // Convert the date to a friendly format
            DateFormat formatterUpdated = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            Calendar calendarUpdated = Calendar.getInstance();
            calendarUpdated.setTimeInMillis(updatedDateLong * 1000);
            String updatedDate = formatterUpdated.format(calendarUpdated.getTime());

            byte[] imageInBytes = cursor.getBlob(imageColumnIndex);
            Bitmap mImageFromDb = convertToBitmap(imageInBytes);
            mImageViewProduct.setImageBitmap(mImageFromDb);

            /* UPDATE UI WITH DATA FROM DATABASE */
            mNameEditText.setText(name);
            mBrandEditText.setText(brand);
            mDescriptionEditText.setText(description);
            mCategoryEditText.setText(category);
            mPriceEditText.setText(String.valueOf(price));
            mDiscountEditText.setText(String.valueOf(discount));
            mQuantityEditText.setText(String.valueOf(quantity));
            mSupplierEditText.setText(supplier);
            mSupplierPhoneEditText.setText(supplierPhone);
            mSupplierEmailEditText.setText(supplierEmail);

            mEntryDateEditText.setText(String.valueOf(entryDate));
            mUpdatedDateEditText.setText(String.valueOf(updatedDate));

            /* Status codes:
             * AVAILABLE = 0;
             * OUT_OF_STOCK = 1;
             * DEPRECATED = 2;
             */
            int status = cursor.getInt(statusColumnIndex);
            if (status == 2) {
                doToast(getString(R.string.warning_product_deprecated));
            } else if (quantity == 0) {
                status = ProductEntry.PRODUCT_STATUS_OUT_OF_STOCK; // if quantity is 1 set status to out of stock.
            } else if (quantity >= 1) {
                status = ProductEntry.PRODUCT_STATUS_AVAILABLE;
            }

            switch (status) {
                case ProductEntry.PRODUCT_STATUS_AVAILABLE:
                    mProductStatusSpinner.setSelection(status);
                    break;
                case ProductEntry.PRODUCT_STATUS_OUT_OF_STOCK:
                    mProductStatusSpinner.setSelection(status);
                    break;
                default:
                    mProductStatusSpinner.setSelection(status);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from form input fields.
        mNameEditText.setText("");
        mBrandEditText.setText("");
        mDescriptionEditText.setText("");
        mCategoryEditText.setText("");
        mPriceEditText.setText("");
        mDiscountEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mSupplierEmailEditText.setText("");
        mEntryDateEditText.setText("");
        mUpdatedDateEditText.setText("");
        mProductStatusSpinner.setSelection(0);

    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
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

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private boolean saveProduct() {
        // Get data from forms
        String nameString;
        if (mNameEditText.getText().toString().equals("")) {
            return false;
        } else {
            nameString = mNameEditText.getText().toString().trim();
        }

        String brandString;
        if (mBrandEditText.getText().toString().equals("")) {
            return false;
        } else {
            brandString = mBrandEditText.getText().toString().trim();
        }

        String descriptionString;
        if (mDescriptionEditText.getText().toString().equals("")) {
            return false;
        } else {
            descriptionString = mDescriptionEditText.getText().toString().trim();
        }

        String categoryString;
        if (mCategoryEditText.getText().toString().equals("")) {
            return false;
        } else {
            categoryString = mCategoryEditText.getText().toString().trim();
        }

        String supplier;
        if (mSupplierEditText.getText().toString().equals("")) {
            return false;
        } else {
            supplier = mSupplierEditText.getText().toString().trim();
        }

        String supplierPhone;
        if (mSupplierPhoneEditText.getText().toString().equals("")) {
            return false;
        } else {
            supplierPhone = mSupplierPhoneEditText.getText().toString().trim();
        }

        String supplierEmail;
        if (mSupplierEmailEditText.getText().toString().equals("")) {
            return false;
        } else {
            supplierEmail = mSupplierEmailEditText.getText().toString().trim();
        }

        double price;
        if (mPriceEditText.getText().toString().equals("")) {
            return false;
        } else {
            price = Double.parseDouble(mPriceEditText.getText().toString().trim());
        }

        int discount;
        if (mDiscountEditText.getText().toString().equals("")) {
            return false;
        } else {
            discount = Integer.parseInt(mDiscountEditText.getText().toString());
        }

        int quantity;
        if (mQuantityEditText.getText().toString().equals("")) {
            return false;
        } else {
            quantity = Integer.parseInt(mQuantityEditText.getText().toString());
        }

        int status = mProductStatus;

        // If quantity in the form is > 0 the status is auto set to available.
        if (quantity > 0) {
            status = 0;
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_BRAND, brandString);
        values.put(ProductEntry.COLUMN_PRODUCT_DESCRIPTION, descriptionString);
        values.put(ProductEntry.COLUMN_PRODUCT_CATEGORY, categoryString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductEntry.COLUMN_PRODUCT_DISCOUNT, discount);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplier);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierEmail);
        values.put(ProductEntry.COLUMN_PRODUCT_STATUS, status);

        byte[] productImageBytes;
        if (mImageProductBitmap != null) {
            productImageBytes = getImageInBytes(mImageProductBitmap);
        } else {
            productImageBytes = convertBitmapResourceToBytes();
        }
        values.put(ProductEntry.COLUMN_PRODUCT_PICTURE, productImageBytes);


        long currentDateTime = System.currentTimeMillis() / 1000;
        long entryDate;
        if (isEditingProduct) {
            entryDate = currentProductEntryDate;
        } else {
            entryDate = currentDateTime;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_ENTRY_DATE, entryDate);
        values.put(ProductEntry.COLUMN_PRODUCT_UPDATED, currentDateTime);

        // FOR NEW PRODUCT
        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                doToast(getString(R.string.editor_insert_product_failed));
            } else {
                doToast(getString(R.string.editor_insert_product_successful));
            }
            // FOR PRODUCT UPDATE
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                doToast(getString(R.string.editor_update_product_failed));
            } else {
                doToast(getString(R.string.editor_update_product_successful));
            }
        }
        return true;
    }

    private void updateItemAdd(int quantityToAdd) {

        int quantityFromInput;
        int quantityUpdated;

        if (TextUtils.isEmpty(mQuantityEditText.getText().toString().trim())) {
            quantityUpdated = quantityToAdd;
        } else {
            quantityFromInput = Integer.parseInt(mQuantityEditText.getText().toString().trim());
            quantityUpdated = quantityFromInput + quantityToAdd;
        }

        mQuantityEditText.setText(String.valueOf(quantityUpdated));

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityUpdated);

        int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
        if (rowsAffected == 0) {
            doToast(getString(R.string.editor_update_product_failed));
        } else {
            doToast(getString(R.string.product_quantity_updated) + String.valueOf(quantityUpdated));
        }

    }

    private void updateItemRemove(int quantityToRemove) {

        int quantityFromInput;
        int quantityUpdated;
        if (TextUtils.isEmpty(mQuantityEditText.getText().toString().trim())) {
            doToast(getString(R.string.set_product_quantity));
            return;
//            quantityUpdated = quantityToRemove;
        } else {
            quantityFromInput = Integer.parseInt(mQuantityEditText.getText().toString().trim());
            quantityUpdated = quantityFromInput - quantityToRemove;
        }


        if (quantityFromInput == 0) {
            Toast.makeText(this, R.string.stock_already_empty, Toast.LENGTH_SHORT).show();
            return;
        } else if (quantityUpdated < 0) {
            Toast.makeText(this, R.string.stock_is_now_empty, Toast.LENGTH_SHORT).show();
            quantityUpdated = 0;
        }
        mQuantityEditText.setText(String.valueOf(quantityUpdated));

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityUpdated);

        int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
        if (rowsAffected == 0) {
            doToast(getString(R.string.editor_update_product_failed));
        } else {
            doToast(getString(R.string.product_quantity_updated) + String.valueOf(quantityUpdated));
        }

    }

    private void doToast(String string) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
        toast.show();
    }

    protected void callSupplier() {

        String phoneNumber = mSupplierPhoneEditText.getText().toString().trim();

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    protected void sendEmail() {

        String[] TO = {mSupplierEmailEditText.getText().toString().trim()};
        String productName = mNameEditText.getText().toString().trim();
        String productBrand = mBrandEditText.getText().toString().trim();
        String productDescription = mDescriptionEditText.getText().toString().trim();

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));

        StringBuilder emailData = new StringBuilder();
        emailData.append(getString(R.string.email_header_template));
        emailData.append(getResources().getString(R.string.email_item_label_name) + productName + "\n");
        emailData.append(getResources().getString(R.string.email_item_label_brand) + productBrand + "\n");
        emailData.append(getResources().getString(R.string.email_item_label_description) + productDescription + "\n\n");
        emailData.append(getResources().getString(R.string.email_item_label_request_quantity));
        String emailBody = emailData.toString();

        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.email_intent_title)));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            doToast(getString(R.string.warning_no_email_app_installed));
        }
    }

    /* GET IMAGE FROM DEVICE CAMERA */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {

                mImageProductBitmap = (Bitmap) extras.get("data");
                mImageViewProduct.setImageBitmap(mImageProductBitmap);

                if (mImageProductBitmap != null) {
                    getImageInBytes(mImageProductBitmap);
                }
            }
        }
    }

    // Convert bitmap to byte array.
    public static byte[] getImageInBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // Convert byte array to bitmap
    public static Bitmap convertToBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public byte[] convertBitmapResourceToBytes() {
        Bitmap bitmapFromResource = BitmapFactory.decodeResource(getResources(), R.drawable.dummy_product_image);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapFromResource.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


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