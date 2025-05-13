package com.example.project;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import android.app.AlertDialog;

import com.example.project.bus.Bus;
import com.example.project.bus.BusStation;
import com.example.project.bus.BusView;
import com.example.project.search.SearchView;
import com.example.project.sql.SQLiteBusStation;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Search extends Activity {
    EditText searchText;
    Button searchBtn, backBtn;
    ListView listView;
    SQLiteBusStation sql = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        setTitle("검색");
        sql = new SQLiteBusStation(this);
        searchBtn = (Button) findViewById(R.id.buttonSearch);
        backBtn = (Button) findViewById(R.id.buttonBack);
        searchText = (EditText) findViewById(R.id.editTextSearch);
        listView = (ListView) findViewById(R.id.listViewSearch);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String target = searchText.getText().toString();
                if(target.equals("")){
                    Toast.makeText(getApplicationContext(), "정류장을 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<BusStation> arr = readCsvData(R.raw.busdata, target);
                SearchAdapter adapter = new SearchAdapter(arr);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                        BusStation data = (BusStation) listView.getItemAtPosition(index);
                        String message = String.format("%s 정류장을 즐겨찾기에 추가하시겠습니까?", data.getStationName());
                        AlertDialog.Builder builder = new AlertDialog.Builder(Search.this);
                        builder.setTitle("안내").setMessage(message);
                        // negative
                        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        // positive
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //data

                                boolean b = sql.insert(data);
                                Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            }
        });
    }

    class SearchAdapter extends BaseAdapter {
        ArrayList<BusStation> items;

        public SearchAdapter(ArrayList<BusStation> bs){
            this.items = bs;
        }

        // Generate > implement methods
        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(BusStation item) {
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
                    SearchView view = null;
                    if (convertView == null) {
                        view = new SearchView(getApplicationContext());
                    } else {
                        view = (SearchView) convertView;
            }

            BusStation item = items.get(position);
            view.setText(item.getID(), item.getStationName(), item.getSi_gun_gu(), item.getEup_myeon_dong());

            return view;
        }
    }

    private double processingLatLng(@NonNull String data){
        String[] arr = data.split("°|'|E|N");
        double first = Double.parseDouble(arr[0]);
        double second = Double.parseDouble(arr[1])/60;

        return first + second;
    }

    public  ArrayList<BusStation> readCsvData(int path, String searchText){
        ArrayList<BusStation> arr = new ArrayList<>();
        try{
            InputStreamReader is = new InputStreamReader(getResources().openRawResource(path));
            BufferedReader br = new BufferedReader(is);
            CSVReader reader = new CSVReader(br);
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                if(nextLine[3].contains(searchText)){
                    BusStation bs = new BusStation();
                    bs.setID(Integer.parseInt(nextLine[1]));
                    bs.setStationName(nextLine[3]);
                    bs.setLatitude(processingLatLng(nextLine[8]));
                    bs.setLongitude(processingLatLng(nextLine[7]));
                    bs.setSi_gun_gu(nextLine[5]);
                    bs.setEup_myeon_dong(nextLine[6]);
                    arr.add(bs);
                }
            }
        } catch (IOException e){

        }
        return arr;
    }
}
