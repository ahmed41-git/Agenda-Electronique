package com.example.agenda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    Double latitude;
    Double longitude;
    String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map) ;
        mapFragment.getMapAsync(this);

        Geocoder geocoder= new Geocoder(this, Locale.getDefault());
      //  Intent in=this.getIntent();
        address="paris";

        try {
            List<Address> list=geocoder.getFromLocationName(address,1);
            if( list.size()>0) {
                Address add = (Address) list.get(0);
                 latitude=add.getLatitude();
                longitude=add.getLongitude();
               // Toast.makeText(context, String.valueOf(latitude), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        LatLng modele= new LatLng(latitude,longitude);
        map.addMarker(new MarkerOptions().position(modele).title(address));
        map.moveCamera(CameraUpdateFactory.newLatLng(modele));
    }
}