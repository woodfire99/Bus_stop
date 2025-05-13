package com.example.project.alarm;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.project.R;

public class AlarmView extends LinearLayout {
    LinearLayout viewLayout;
    TextView tvStationID, tvBusNO;
    // Generate > Constructor

    public AlarmView(Context context) {
        super(context);
        init(context);
    }

    public AlarmView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // singer_item.xml을 inflation
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.alarmview, this, true);

        viewLayout = (LinearLayout) findViewById(R.id.alarmLayout);
        tvBusNO = (TextView) findViewById(R.id.tvAlarmBusNO);
        tvStationID = (TextView) findViewById(R.id.tvAlarmStationID);
    }

    public void setText(int stationID, int busNO) {
        String stID = Integer.toString(stationID);
        tvBusNO.setText(busNO + "번 버스");
        tvStationID.setText(stID);
    }
}