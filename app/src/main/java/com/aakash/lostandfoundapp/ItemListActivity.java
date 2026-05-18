package com.aakash.lostandfoundapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView listView;
    private Button btnAll, btnLost, btnFound;
    private List<LostFoundItem> currentList;
    private String selectedType = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        dbHelper  = new DatabaseHelper(this);
        listView  = findViewById(R.id.listViewItems);
        btnAll    = findViewById(R.id.btnAll);
        btnLost   = findViewById(R.id.btnLost);
        btnFound  = findViewById(R.id.btnFound);

        // This is my Filter button which can be clickable and used in my app.
        btnAll.setOnClickListener(v -> {
            selectedType = "All";
            updateButtonStyles();
            applyFilter();
        });

        btnLost.setOnClickListener(v -> {
            selectedType = "Lost";
            updateButtonStyles();
            applyFilter();
        });

        btnFound.setOnClickListener(v -> {
            selectedType = "Found";
            updateButtonStyles();
            applyFilter();
        });

        // This for the tap on item that takes us on to the details screen of the lost item.
        listView.setOnItemClickListener((parent, view, position, id) -> {
            LostFoundItem item = currentList.get(position);
            Intent intent = new Intent(this, ItemDetailActivity.class);
            intent.putExtra("ITEM_ID", item.getId());
            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        applyFilter();
    }
    private void applyFilter() {
        List<LostFoundItem> allItems = dbHelper.getAllItems();

        if (selectedType.equals("All")) {
            currentList = allItems;
        } else {
            currentList = new ArrayList<>();
            for (LostFoundItem item : allItems) {
                if (item.getType().equalsIgnoreCase(selectedType)) {
                    currentList.add(item);
                }
            }
        }
        loadList();
    }
    private void updateButtonStyles() {
        btnAll.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#AAAAAA")));
        btnLost.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#AAAAAA")));
        btnFound.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#AAAAAA")));
        switch (selectedType) {
            case "All":
                btnAll.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#555555")));
                break;
            case "Lost":
                btnLost.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#CC3333")));
                break;
            case "Found":
                btnFound.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2E7D32")));
                break;
        }
    }
    private void loadList() {
        ArrayAdapter<LostFoundItem> adapter = new ArrayAdapter<LostFoundItem>(
                this, R.layout.list_item, currentList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                }
                LostFoundItem item = currentList.get(position);

                TextView tvTitle     = convertView.findViewById(R.id.tvTitle);
                TextView tvTimestamp = convertView.findViewById(R.id.tvTimestamp);
                TextView tvCategory  = convertView.findViewById(R.id.tvCategory);
                ImageView imgThumb   = convertView.findViewById(R.id.imgThumb);

                String typeLabel = item.getType().equalsIgnoreCase("Lost") ? "Lost" : "Found";
                tvTitle.setText(typeLabel + ": " + item.getDescription());
                tvTimestamp.setText(item.getTimestamp());
                tvCategory.setText("Category: " + item.getCategory());

                if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
                    Bitmap bmp = BitmapFactory.decodeFile(item.getImagePath());
                    if (bmp != null) {
                        imgThumb.setImageBitmap(bmp);
                    } else {
                        imgThumb.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                } else {
                    imgThumb.setImageResource(android.R.drawable.ic_menu_gallery);
                }

                return convertView;
            }
        };
        listView.setAdapter(adapter);
    }
}