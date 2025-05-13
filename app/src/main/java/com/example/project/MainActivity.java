package com.example.project;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.project.alarm.Alarm;
import com.example.project.bus.Bus;
import com.example.project.bus.BusStation;
import com.example.project.bus.BusView;
import com.example.project.sql.SQLiteAlarm;
import com.example.project.sql.SQLiteBusStation;
import com.example.project.thread.BusRouteThread;
import com.example.project.thread.BusThread;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView busListView;
    Button  addBtn, removeBtn, locationBtn, alarmBtn;
    Spinner busSpinner;
    SQLiteBusStation sqlBS = null;
    SQLiteAlarm sqlAlarm = null;
    BusAdapter adapter;
    Handler mainHandler;
    final NotifyThread notifyThread = new NotifyThread(60000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("정류장별 버스 도착 정보");
        setContentView(R.layout.activity_main);
        // data setting
        sqlAlarm = new SQLiteAlarm(this);
        sqlBS = new SQLiteBusStation(this);
        alarmBtn = (Button) findViewById(R.id.buttonAlarm);
        locationBtn = (Button) findViewById(R.id.buttonLocation);
        addBtn = (Button) findViewById(R.id.buttonAdd);
        removeBtn = (Button) findViewById(R.id.buttonRemove);
        busListView = (ListView) findViewById(R.id.busListView);
        busSpinner = (Spinner) findViewById(R.id.spinnerBusStation);
        mainHandler = new Handler();

        // init
        display_list();
        createNotificationChannel();
        notifyThread.start();

        // listener
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                startActivityForResult(intent, 0);
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String target = (String)busSpinner.getSelectedItem();
                sqlBS.delete(target);
                Toast.makeText(getApplicationContext(), "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                display_list();
                if(busSpinner.getCount() == 0){
                    removeListviewData();
                }
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapViewer.class);
                String target = (String)busSpinner.getSelectedItem();
                BusStation targetBS = sqlBS.selectByName(target);
                double[] latlng = {targetBS.getLatitude(), targetBS.getLongitude()};
                intent.putExtra("latlng", latlng);
                intent.putExtra("name", (String)busSpinner.getSelectedItem());
                startActivity(intent);
            }
        });

        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlarmCheck.class);
                startActivity(intent);
            }
        });

        busSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String target = (String) busSpinner.getItemAtPosition(i);
                loadBusData(target);
                Thread refreshThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true) {
                            try {
                                Thread.sleep(60000);
                                String target = (String) busSpinner.getSelectedItem();
                                refreshBusData(target);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
                refreshThread.start();  // 1분마다 새로고침
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        busListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bus target = (Bus)busListView.getItemAtPosition(i);
                if(target == null)return;
                try {
                    BusRouteThread brt = new BusRouteThread(target.getRouteCD());
                    brt.start(); brt.join();
                    ArrayList<String> routes = brt.getRoutes();
                    Intent intent = new Intent(getApplicationContext(), Route.class);
                    intent.putExtra("routes", routes);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        busListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String station = (String)busSpinner.getSelectedItem();
                int stationID = Integer.parseInt(station.split("-")[1]);
                Bus bus = (Bus)busListView.getItemAtPosition(position);
                String busNum = Integer.toString(bus.getRouteNO());
                String message = String.format("%s 정류장의 %s번 버스에 알람 설정 하시겠습니까?",
                        station, busNum);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("알림 설정");
                builder.setMessage(message);
                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<Alarm> alarmSize = sqlAlarm.select();
                        if(alarmSize.size() >= 1){
                            Toast.makeText(getApplicationContext(), "Test : 알람 갯수 1개로 제한", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        sqlAlarm.insert(stationID, bus.getRouteNO());
                        Toast.makeText(getApplicationContext(), "알람이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                return true;
            }
        });
    }
    // Alarm Thread
    class NotifyThread extends Thread{
        private int milSec;

        public NotifyThread(int milSec){
            this.milSec = milSec;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    ArrayList<Alarm> alarmArr = sqlAlarm.select();
                    for (int i = 0; i < alarmArr.size(); i++) {
                        int stationID = alarmArr.get(i).getStationID();
                        int busNO = alarmArr.get(i).getBusNO();
                        BusThread bt = new BusThread(stationID);
                        bt.start();bt.join();
                        ArrayList<Bus> buses = bt.getBuses();
                        for(Bus bus : buses){
                            int time = bus.getExtimeMin();
                            int msgNum = bus.getMsgNum();
                            if(bus.getRouteNO() == busNO && (time <= 5 || msgNum == 6)){ // 5분 남았거나 진입중일 경우
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotificationCompat.Builder builder = getBuilder(getMessage(busNO, time, msgNum));
                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                                        notificationManager.notify(0, builder.build());
                                    }
                                });
                                break;
                            }
                        }

                    }
                    Thread.sleep(milSec);
                } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
        }

        private NotificationCompat.Builder getBuilder(String message){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "main")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("버스 알람")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            return builder;
        }

        private String getMessage(int busNO, int time, int msgNum){
            if(msgNum == 6){
                return String.format("%d번 버스 현재 진입중입니다.", busNO);
            }

            return String.format("%d번 버스 %d분후 도착합니다.", busNO, time);
        }
    }

    // BusList Adapter
    class BusAdapter extends BaseAdapter {
        ArrayList<Bus> items = new ArrayList<>();


        // Generate > implement methods
        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Bus item) {
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
            BusView view = null;
            if (convertView == null) {
                view = new BusView(getApplicationContext());
            } else {
                view = (BusView) convertView;
            }

            Bus item = items.get(position);

            view.setNO(item.getRouteNO(), item.getColor());
            view.setDest(item.getDestination());
            view.setMsg(item.getMsgNum(), item.getExtimeMin());
            view.setBackground(item.getColor());

            return view;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            display_list();
        } else {   // RESULT_CANCEL
        }
    }

    private void display_list() {
        //데이터 목록 가져오기
        ArrayList<BusStation> sqlList = sqlBS.selectBusList();
        ArrayList<String> list = new ArrayList<String>();
        for(BusStation bs : sqlList){
            list.add(bs.getStationName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, list);
        busSpinner.setAdapter(adapter);
    }

    private void removeListviewData(){
        busListView.setAdapter(null);
    }

    private void loadBusData(String target){
        try{
            BusStation bs = sqlBS.selectByName(target);
            BusThread bt = new BusThread(bs.getID());
            bt.start(); bt.join();
            ArrayList<Bus> buses = bt.getBuses();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    adapter = new BusAdapter();
                    for (int i = 0; i < buses.size(); i++) {
                        adapter.addItem(buses.get(i));
                    }
                    busListView.setAdapter(adapter);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void refreshBusData(String target){
        try {
            BusStation bs = sqlBS.selectByName(target);
            BusThread bt = new BusThread(bs.getID());
            bt.start();
            bt.join();
            ArrayList<Bus> buses = bt.getBuses();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    for (int i = 0; i < buses.size(); i++) {
                        adapter.addItem(buses.get(i));
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "name";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("main", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}