package com.example.project;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import android.app.AlertDialog;

import com.example.project.alarm.Alarm;
import com.example.project.alarm.AlarmView;
import com.example.project.bus.Bus;
import com.example.project.bus.BusView;
import com.example.project.sql.SQLiteAlarm;

import java.util.ArrayList;

public class AlarmCheck extends Activity {
    SQLiteAlarm sqlAlarm = new SQLiteAlarm(this);
    ListView alarmListView;
    Button btnBack;
    AlarmAdapter adapter;
    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        btnBack = (Button) findViewById(R.id.buttonBackMain);
        alarmListView = (ListView) findViewById(R.id.listViewAlarm);
        handler = new Handler();
        displayAlarmList();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AlarmCheck.this);
                alert.setMessage("선택하신 알람을 삭제하시겠습니까?");
                alert.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Alarm target = (Alarm) alarmListView.getItemAtPosition(position);
                        sqlAlarm.delete(target.getStationID(), target.getBusNO());
                        refreshAlarmList();
                        Toast.makeText(AlarmCheck.this, "삭제되었습니다.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        });

    }

    private void displayAlarmList(){
        adapter = new AlarmAdapter();
        ArrayList<Alarm> alarms = sqlAlarm.select();
        handler.post(new Runnable() {
            @Override
            public void run() {
                for(Alarm alarm : alarms){
                    adapter.addItem(alarm);
                }
                alarmListView.setAdapter(adapter);
            }
        });
    }

    private void refreshAlarmList(){
        adapter.clear();
        ArrayList<Alarm> alarms = sqlAlarm.select();
        handler.post(new Runnable() {
            @Override
            public void run() {
                for(Alarm alarm : alarms){
                    adapter.addItem(alarm);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    class AlarmAdapter extends BaseAdapter {
        ArrayList<Alarm> items = new ArrayList<Alarm>();


        // Generate > implement methods
        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Alarm item) {
            items.add(item);
        }

        public void clear(){
            items.clear();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 뷰 객체 재사용
            AlarmView view = null;
            if (convertView == null) {
                view = new AlarmView(getApplicationContext());
            } else {
                view = (AlarmView) convertView;
            }

            Alarm item = items.get(position);

            view.setText(item.getStationID(), item.getBusNO());

            return view;
        }
    }
}
