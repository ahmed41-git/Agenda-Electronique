package com.example.agenda;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CustomCalendarView extends LinearLayout {

    /*    declaration des attributs */
    ImageButton NextButton, PreviousButton;
    TextView CurrentDate;
    GridView gridView;
    private static final int MAX_CALENDAR_DAYS = 42;

    /*creation de l'objet calendar
     dont les champs de calendrier ont été initialisés
     avec la date et l'heure actuelles dans la région géographique*/
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    Context context;
    //format de la date local
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
    SimpleDateFormat yearFormate = new SimpleDateFormat("yyyy", Locale.getDefault());
    SimpleDateFormat eventDateFormate = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());

    MyGridAdapter myGridAdapter;
    AlertDialog alertDialog;
    List<Date> dates = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>();
    int alarmYear,alarmMonth,alarmDay,alarmHour,alarmMinut;
    DBOpenHelper dbOpenHelper;
    //contructeur
    public CustomCalendarView(Context context) {
        super(context);
    }

    //contructeur
    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        IntializeLayout();
        SetUpCalendar();
/*
clique sur le bouton previous pour aller au mois précédent
 */
        PreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                SetUpCalendar();
            }
        });
        /*
        clique sur le bouton previous pour aller au mois suivant
         */
        NextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                SetUpCalendar();
            }
        });
        /*
         *clique sur une date
         */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                //appel du layout add_newevent pour enregistrer un nouveau evenement
                final View addView=LayoutInflater.from(parent.getContext()).inflate(R.layout.add_newevent_layout, null);
                final EditText EventName = addView.findViewById(R.id.eventname);
                final TextView EventTime = addView.findViewById(R.id.eventtime);
                ImageButton SetTime = addView.findViewById(R.id.seteventtime);
                CheckBox alarmMe = addView.findViewById(R.id.alarm);
                CheckBox check = addView.findViewById(R.id.check);
                final EditText lieu = addView.findViewById(R.id.lieu);
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.setTime(dates.get(position));
                alarmYear =dateCalendar.get(Calendar.YEAR);
                alarmMonth = dateCalendar.get(Calendar.MONTH);
                alarmDay = dateCalendar.get(Calendar.DAY_OF_MONTH);
                Button AddEvent = addView.findViewById(R.id.addevent);
                    check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                          if(isChecked) lieu.setVisibility(View.VISIBLE);
                          else lieu.setVisibility(View.INVISIBLE);
                        }
                    });
                     /*affichage de l'horloge pour definir l'heure de l'evenement
                losqu'on clique sur l'horloge
                 */
                SetTime.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar= Calendar.getInstance();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        int minuts =calendar.get(Calendar.MINUTE);
                        //boite de dialogue de l'horloge
                        TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_MaterialComponents_Dialog_Alert
                                , new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar c= Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE,minute);
                                c.setTimeZone(TimeZone.getDefault());
                                SimpleDateFormat hformate = new SimpleDateFormat("K:mm a", Locale.getDefault());
                                String event_Time= hformate.format(c.getTime());
                                EventTime.setText(event_Time);
                                alarmHour=c.get(Calendar.HOUR_OF_DAY);
                                alarmMinut=c.get(Calendar.MINUTE);
                            }
                        },hours,minuts, false);
                        timePickerDialog.show();
                    }
                });
                //recuperation de la date, mois, année choisie
                final String date= eventDateFormate.format(dates.get(position));
                final String month = monthFormat.format(dates.get(position));
                final String year= yearFormate.format(dates.get(position));

                //clique sur le bouton ajouter  l'evenement
                AddEvent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!EventName.getText().toString().equals("")) {//si le eventname n'est pas vide
                            if (alarmMe.isChecked()) {//si l'alarme est coché

                                //enregistrer les données de l'evenement dans la base de données
                                SaveEvent(EventName.getText().toString(), EventTime.getText().toString(), date, month, year, "on", lieu.getText().toString());
                                //actualisation du calendrier
                                SetUpCalendar();
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinut);
                                setAlarm(calendar, EventName.getText().toString(), EventTime.getText().toString(), getRequestCode(date, EventName.getText().toString(), EventTime.getText().toString()));
                                alertDialog.dismiss();
                            } else { //sinon

                                //enregistrer les données de l'evenement dans la base de données
                                SaveEvent(EventName.getText().toString(), EventTime.getText().toString(), date, month, year, "off", lieu.getText().toString());
                                //actualisation du calendrier
                                SetUpCalendar();
                                alertDialog.dismiss();

                            }
                        }
                        else //si l'eventName n'est pas renseigné
                            Toast.makeText(context, "Veuillez renseigner l'evenement", Toast.LENGTH_SHORT).show();
                    }
                });
                //Alerte dialogue Ajouter evenement
                builder.setView(addView);
                alertDialog=builder.create();
                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //Actualisation calendrier
                        SetUpCalendar();
                    }
                });
            }
        });
        //afficher les trois premiers evenement a venir
        ListView listView=(ListView) findViewById(R.id.maliste);
        ArrayList<Events> li= new ArrayList<>();
        li=collect();
        if(li.size()!=0)
        {
            listView.setAdapter(new EventAdapter(listView.getContext(), R.layout.exemple, li));
        }
        else {
            Events ev=new Events("Aucun evenement !!","","","","","");
            li.add(ev);
            listView.setAdapter(new EventAdapter(listView.getContext(), R.layout.exemple, li));
        }
/*appui long sur une date du calendrier

 */
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String date = eventDateFormate.format(dates.get(position));
               /* affichage de la liste des evenements associés a la date selectionnée
                        gerer dans l'activité EventRecyclerAdapter
                        * */
                AlertDialog.Builder builder =new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView= LayoutInflater.from(parent.getContext()).inflate(R.layout.show_event_layout, null);
                RecyclerView recyclerView= showView.findViewById(R.id.EventRV);
                RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(showView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                EventRecyclerAdapter eventRecycleAdapter= new EventRecyclerAdapter(showView.getContext(),CollectEventByDate(date));
                recyclerView.setAdapter(eventRecycleAdapter);
                eventRecycleAdapter.notifyDataSetChanged();
                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        SetUpCalendar();
                    }
                });
                return true;
            }
        });

    }
    /*
     * fonction de recuperation de l'ID*/
    private  int getRequestCode(String date, String event, String time){
        int code=0;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvents(date,event,time,database);
        while (cursor.moveToNext()){
            code= cursor.getInt(cursor.getColumnIndex(DBStructure.ID));
        }
        cursor.close();
        dbOpenHelper.close();
        return code;
    }

    /*
    fonction pour regler l'alarm
     */
    private void setAlarm(Calendar calendar,String event,String time,int RequestCOde){
        Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
        intent.putExtra("event",event);
        intent.putExtra("time",time);
     //   intent.putExtra("date",date);
        intent.putExtra("id",RequestCOde);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,RequestCOde,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }


    //fonction de recuperation de la liste des evenements d'une date
    private  ArrayList<Events> CollectEventByDate(String date){
        ArrayList<Events> arrayList= new ArrayList<>();
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEvents(date,database);
        while (cursor.moveToNext()){
            String event= cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time= cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            String Date= cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String month= cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String Year= cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            String Location= cursor.getString(cursor.getColumnIndex(DBStructure.Location));
            Events events = new Events(event,time,Date,month,Year,Location);
            arrayList.add(events);
        }
        cursor.close();
        dbOpenHelper.close();
        return arrayList;
    }
    //contructeur
    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //fonction pour enregistrer un nouveau evenements
    private void SaveEvent(String event, String time, String date, String month,String year,String notify,String location){
        dbOpenHelper= new DBOpenHelper(context);
        SQLiteDatabase database=dbOpenHelper.getWritableDatabase();
        dbOpenHelper.SaveEvent(event,time,date,month,year,notify,location,database);
        dbOpenHelper.close();
        Toast.makeText(context, R.string.save, Toast.LENGTH_SHORT).show();
    }
//initialisation layout du calendrier

    private void IntializeLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        NextButton = view.findViewById(R.id.nextBtn);
        PreviousButton = view.findViewById(R.id.previewBtn);
        CurrentDate = view.findViewById(R.id.current_Date);
        gridView = view.findViewById(R.id.gridView);
    }
//actualisation calendrier
    private void SetUpCalendar() {
        String currwntDate = dateFormat.format(calendar.getTime());
        CurrentDate.setText(currwntDate);
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int FirstDayofMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayofMonth);
        collectEventsPerMonth(monthFormat.format(calendar.getTime()),yearFormate.format(calendar.getTime()));
        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        myGridAdapter= new MyGridAdapter(context,dates,calendar,eventsList);
        gridView.setAdapter(myGridAdapter);
    }
    /*
   recuperer les evenements d'un mois donné
    */
    private void collectEventsPerMonth(String Month,String year) {
        eventsList.clear();
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEventsperMonth(Month, year, database);
        while (cursor.moveToNext()) {
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            String date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            String Location = cursor.getString(cursor.getColumnIndex(DBStructure.Location));

            Events events = new Events(event, time, date, month, Year, Location);
            eventsList.add(events);

        }
    }
    //recuperation des trois premiers evenement a vinir
        public ArrayList<Events> collect(){
            ArrayList<Events> eve = new ArrayList<>();
            dbOpenHelper= new DBOpenHelper(context);
            SQLiteDatabase database= dbOpenHelper.getReadableDatabase();
            Cursor cursor= dbOpenHelper.ReadEventsperMonth2(database);
            int i=0;
            while(cursor.moveToNext() && i<3){
                i++;
                String event= cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
                String time= cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
                String date= cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
                String month= cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
                String Year= cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
                String Location= cursor.getString(cursor.getColumnIndex(DBStructure.Location));

                Events events =new Events(event,time,date,month,Year,Location);
                eve.add(events);

            }
           return  eve;
    }


}
