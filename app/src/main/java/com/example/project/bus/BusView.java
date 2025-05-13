package com.example.project.bus;

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

public class BusView extends LinearLayout {
    LinearLayout viewLayout;
    TextView tvNO;
    TextView tvDest;
    TextView tvMsg;
    // Generate > Constructor

    public BusView(Context context) {
        super(context);
        init(context);
    }

    public BusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // singer_item.xml을 inflation
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.busview, this, true);

        viewLayout = (LinearLayout) findViewById(R.id.viewLayout);
        tvNO = (TextView) findViewById(R.id.tvNO);
        tvDest = (TextView) findViewById(R.id.tvDest);
        tvMsg = (TextView) findViewById(R.id.tvMsg);
    }

    public void setNO(int NO, int color) {
        String stNO = Integer.toString(NO);
        tvNO.setText(stNO + "번 버스");
        tvNO.setTextColor(color);
    }

    public void setBackground(int color){
        if(color == Color.rgb(255, 102, 102)){ // RED
            viewLayout.setBackgroundResource(R.drawable.layout_red);
        }
        else if(color == Color.rgb(102,102,204)){ // BLUE
            viewLayout.setBackgroundResource(R.drawable.layout_blue);
        }
        else{ //YELLOW
            viewLayout.setBackgroundResource(R.drawable.layout_yellow);
        }
    }

    public void setDest(String dest) {
        tvDest.setText(dest + " 방향");
    }

    public void setMsg(int msg, int extime) {
        String message = toMSG(msg, extime);
        tvMsg.setText(message);
    }

    private String toMSG(int msgNo, int extime) {
        switch (msgNo) {
            case 1:
                return "도착";
            case 2:
                return "출발";
            case 3:
                return extime + "분 후 도착";
            case 4:
                return "교차로 통과";
            case 6:
                return "진입중";
            case 7:
                return "차고지 운행대기중";
        }

        return null;
    }
}