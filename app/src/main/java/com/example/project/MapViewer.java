package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;

import java.util.ArrayList;

public class MapViewer extends Activity implements OnMapReadyCallback{
    private MapView mapView;
    static double lat, lng;
    static String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);

        // get Data
        Intent secondIntent = getIntent();
        double[] arr = secondIntent.getDoubleArrayExtra("latlng");
        lat = arr[0];
        lng = arr[1];
        name = secondIntent.getStringExtra("name");

        // mapview
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap){
        // move camera
        LatLng latlng = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(latlng);
        naverMap.moveCamera(cameraUpdate);
        naverMap.setMinZoom(15.0);
        naverMap.setMaxZoom(18.0);


        // marker
        Marker marker = new Marker();
        marker.setPosition(latlng);
        marker.setMap(naverMap);
        marker.setCaptionText(name);
    }
}
