package com.aakash.lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCreate = findViewById(R.id.btnCreateAdvert);
        Button btnShow   = findViewById(R.id.btnShowItems);
        Button btnMap    = findViewById(R.id.btnShowMap);

        btnCreate.setOnClickListener(v ->
                startActivity(new Intent(this, CreateAdvertActivity.class)));

        btnShow.setOnClickListener(v ->
                startActivity(new Intent(this, ItemListActivity.class)));

        btnMap.setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));
    }
}