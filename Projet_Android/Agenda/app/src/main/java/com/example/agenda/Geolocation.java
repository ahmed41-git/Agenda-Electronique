package com.example.agenda;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Message;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import android.os.Handler;

public class Geolocation {
    public static  String getAdress(String locationAddress, Context context){
        List<String> data=null;
        String[] code=null;
        Thread thread= new Thread(){
            @Override
            public  void run()
            {
                Geocoder geocoder= new Geocoder(context, Locale.getDefault());

                try {
                    List list=geocoder.getFromLocationName(locationAddress,1);
                        if(list.size()>0)
                        {
                            Address addess=(Address) list.get(0);
                            Double latitude=addess.getLatitude();
                            Double longitude=addess.getLongitude();
                            data.add(String.valueOf(latitude));
                            data.add(String.valueOf(longitude));
                            code[0] =String.valueOf(latitude);


                        }

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        };
        thread.start();
        return code[0];
    }
}
