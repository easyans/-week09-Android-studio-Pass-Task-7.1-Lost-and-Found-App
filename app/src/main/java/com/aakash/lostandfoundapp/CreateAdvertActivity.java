package com.aakash.lostandfoundapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.util.Calendar;
import java.util.UUID;

public class CreateAdvertActivity extends AppCompatActivity {

    private EditText etName, etPhone, etDescription, etDate, etLocation;
    private RadioGroup rgPostType;
    private Spinner spinnerCategory;
    private ImageView imgPreview;
    private String selectedImagePath = "";

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    // Copy image to private app storage to avoid URI permission expiry
                    String savedPath = copyImageToAppStorage(uri);
                    if (savedPath != null) {
                        selectedImagePath = savedPath;
                        imgPreview.setImageBitmap(BitmapFactory.decodeFile(savedPath));
                    } else {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        etName        = findViewById(R.id.etName);
        etPhone       = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etDate        = findViewById(R.id.etDate);
        etLocation    = findViewById(R.id.etLocation);
        rgPostType    = findViewById(R.id.rgPostType);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        imgPreview    = findViewById(R.id.imgPreview);

        // Populate category spinner
        String[] categories = {"Electronics", "Pets", "Wallets", "Keys", "Bags", "Clothing", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Date picker
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) ->
                    etDate.setText(String.format("%02d/%02d/%04d", day, month + 1, year)),
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Image picker
        findViewById(R.id.btnSelectImage).setOnClickListener(v ->
                imagePickerLauncher.launch("image/*"));

        // Save button
        findViewById(R.id.btnSave).setOnClickListener(v -> saveItem());
    }

    /**
     * Copies the picked image into the app's private files directory
     * so it remains accessible after the picker URI permission expires.
     */
    private String copyImageToAppStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            // Create images directory inside app's private storage
            File imagesDir = new File(getFilesDir(), "images");
            if (!imagesDir.exists()) imagesDir.mkdirs();

            // Unique filename
            File destFile = new File(imagesDir, UUID.randomUUID().toString() + ".jpg");

            // Copy bytes
            OutputStream outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();

            return destFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveItem() {
        String name     = etName.getText().toString().trim();
        String phone    = etPhone.getText().toString().trim();
        String desc     = etDescription.getText().toString().trim();
        String date     = etDate.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        int selectedId  = rgPostType.getCheckedRadioButtonId();
        String type     = (selectedId == R.id.rbLost) ? "Lost" : "Found";

        if (name.isEmpty() || phone.isEmpty() || desc.isEmpty()
                || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImagePath.isEmpty()) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper db = new DatabaseHelper(this);
        long result = db.insertItem(type, name, phone, desc, date, location, category, selectedImagePath);

        if (result != -1) {
            Toast.makeText(this, "Advert saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show();
        }
    }
}