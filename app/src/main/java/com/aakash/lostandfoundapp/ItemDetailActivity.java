package com.aakash.lostandfoundapp;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ItemDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        dbHelper = new DatabaseHelper(this);
        itemId   = getIntent().getIntExtra("ITEM_ID", -1);

        if (itemId == -1) { finish(); return; }

        LostFoundItem item = dbHelper.getItemById(itemId);
        if (item == null) { finish(); return; }

        ((TextView) findViewById(R.id.tvDetailType))
                .setText(item.getType() + " Item");
        ((TextView) findViewById(R.id.tvDetailCategory))
                .setText("Category: " + item.getCategory());
        ((TextView) findViewById(R.id.tvDetailTimestamp))
                .setText("Posted: " + item.getTimestamp());
        ((TextView) findViewById(R.id.tvDetailDescription))
                .setText("Description: " + item.getDescription());
        ((TextView) findViewById(R.id.tvDetailName))
                .setText("Contact: " + item.getName());
        ((TextView) findViewById(R.id.tvDetailPhone))
                .setText("Phone: " + item.getPhone());
        ((TextView) findViewById(R.id.tvDetailDate))
                .setText("Date: " + item.getDate());
        ((TextView) findViewById(R.id.tvDetailLocation))
                .setText("Location: " + item.getLocation());

        ImageView imgDetail = findViewById(R.id.imgDetail);
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            Bitmap bmp = BitmapFactory.decodeFile(item.getImagePath());
            if (bmp != null) imgDetail.setImageBitmap(bmp);
        }
        findViewById(R.id.btnRemove).setOnClickListener(v -> {
            dbHelper.deleteItem(itemId);
            Toast.makeText(this, "Advert removed", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}