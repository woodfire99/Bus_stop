package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Route extends Activity {
    ListView routeListview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);
        routeListview = (ListView) findViewById(R.id.routeListview);
        Intent intent = getIntent();
        ArrayList<String> arr = intent.getStringArrayListExtra("routes");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
        routeListview.setAdapter(adapter);
    }
}
