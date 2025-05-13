package com.example.project.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.project.R;

public class SearchView extends LinearLayout {
    LinearLayout viewLayout;
    TextView tvStationName, tvStationID, tvSi, tvEup;
    // Generate > Constructor

    public SearchView(Context context) {
        super(context);
        init(context);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // singer_item.xml을 inflation
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.searchview, this, true);

        viewLayout = (LinearLayout) findViewById(R.id.searchViewLayout);
        tvStationName = (TextView) findViewById(R.id.tvSearchStationName);
        tvStationID = (TextView) findViewById(R.id.tvSearchStationID);
        tvSi = (TextView) findViewById(R.id.tvSearchSi);
        tvEup = (TextView) findViewById(R.id.tvSearchEup);
    }

    public void setText(int stationID, String stName, String si, String eup) {
        tvSi.setText(si);
        tvEup.setText(eup);
        tvStationID.setText(Integer.toString(stationID));
        tvStationName.setText(stName);
    }
}