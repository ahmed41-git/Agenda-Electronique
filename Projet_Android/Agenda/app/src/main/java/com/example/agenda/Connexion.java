package com.example.agenda;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;

public class Connexion extends AppCompatActivity {
    //instanciation du calendrier
    CustomCalendarView  customCalendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //page principale du calendrier
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customCalendarView = findViewById(R.id.mainView);
    }
}