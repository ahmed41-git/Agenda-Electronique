package com.example.agenda;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.usage.UsageEvents;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder> {
    Context context;
    ArrayList<Events> arrayList;
    DBOpenHelper dbOpenHelper;
    AlertDialog alertDialog,dialog;
//constructeur
    public EventRecyclerAdapter(Context context, ArrayList<Events> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    //fonction pour instancier un vue avec layout show_event_rowlayout
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.show_event_rowlayout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);//creation builder pour alertDialog
        //recuperation des elements de position
        Events events=arrayList.get(position);
        holder.Event.setText(events.getEVENT());
        holder.DateTxt.setText(events.getDATE());
        holder.Time.setText(events.getTIME());

        if (!events.getLOCATION().equals("") && !events.getLOCATION().isEmpty())//si l'adresse a été renseigné
        {
            //affichage boutton pour l'itinéraire
            holder.carte.setVisibility(View.VISIBLE);
            //clique sur le boutton pour se localiser
            holder.carte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                    redirection vers google maps
                     */
                    try {
                        Uri uri =Uri.parse("https://www.google.co.in/maps/dir//"+events.getLOCATION());
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        intent.setPackage("com.google.android.apps.maps");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }catch (ActivityNotFoundException e)
                    {
                        Uri uri =Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                   // Toast.makeText(context.getApplicationContext(),events.getLOCATION(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        /*
        clique sur le boutton pour supprimer evenement
         */
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirection pour authentification
                LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             View vi= li.inflate(R.layout.log2,null);
             //recupération login et mot de passe renseignés
                EditText log= (EditText) vi.findViewById(R.id.username2);
                EditText pass=(EditText) vi.findViewById(R.id.password2);

                 builder.setView(vi);
                builder.setTitle(R.string.title);
                alertDialog=builder.create();
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Supprimer" , new DialogInterface.OnClickListener() {
                    @Override
                    //clique sur le button supprimer du dialog
                    public void onClick(DialogInterface dialog, int which) {
                        //verification de l'authentification
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                        if ((preferences.getString("login","")).equals(log.getText().toString()) && (preferences.getString("passWord","")).equals(pass.getText().toString())){
                            //si log et mot de passe correct
                            //suppression evenement
                            deleteCalendarEvent(events.getEVENT(),events.getDATE(),events.getTIME());
                            arrayList.remove(position);
                             Toast.makeText(context.getApplicationContext(),R.string.confirm,Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                            alertDialog.dismiss();
                        }

                        else//sinon message d'erreur
                            Toast.makeText(context.getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    //clique sur le boutton annuler du dialog
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        notifyDataSetChanged();
                    }
                });
            }
        });
        //clique sur le button update pour modifier un evenement
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alerte dialogue pour modifier l'evenement
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater layoutInflater =(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                View vi= layoutInflater.inflate(R.layout.update_event_layout,null);
                //recuperation des nouvelle valeur renseigner
                Button updateEvent= (Button) vi.findViewById(R.id.updateEvent);
                EditText text = (EditText) vi.findViewById(R.id.updateeventname);
                text.setText(events.getEVENT());
                TextView textView =(TextView) vi.findViewById(R.id.updateTime);
                textView.setText(events.getTIME());
                ImageButton SetTime = vi.findViewById(R.id.updateseteventtime);
                //clique sur le boutton horloge pour modifier l'heur
                SetTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar= Calendar.getInstance();
                        //recuperation de la nouvelle heure
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        int minuts =calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(vi.getContext(), R.style.Theme_MaterialComponents_Dialog_Alert
                                , new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar c= Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE,minute);
                                c.setTimeZone(TimeZone.getDefault());
                                SimpleDateFormat hformate = new SimpleDateFormat("K:mm a", Locale.getDefault());
                                String event_Time= hformate.format(c.getTime());
                                textView.setText(event_Time);
                            }
                        },hours,minuts, false);
                        timePickerDialog.show();
                    }
                });
                //clique sur le boutton modifier apres avoir modifier les champs
                updateEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mise a jour dans la base de donnees
                        updateCalendarEvent(text.getText().toString(),textView.getText().toString(),events.getEVENT(),events.TIME,events.getDATE());
                        //actualisation des elements dans la list events
                        events.setEVENT(text.getText().toString());
                        events.setTIME(textView.getText().toString());
                        arrayList.set(position,events);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                builder.setView(vi);
                dialog=builder.create();
                dialog.show();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        notifyDataSetChanged();
                    }
                });
            }
        });
        //pour definir l'image de notification
        if(isAlarmed(events.getDATE(),events.getEVENT(),events.getTIME()))
        {
            holder.setAlarm.setImageResource(R.drawable.ic_baseline_notifications_on_24);
        }
        else {
            holder.setAlarm.setImageResource(R.drawable.ic_baseline_notifications_off_24);
        }
        Calendar dateCalendar=Calendar.getInstance();
        dateCalendar.setTime(ConvertStringToDate(events.getDATE()));
        int alarmYear=dateCalendar.get(Calendar.YEAR);
        int alarmMonth=dateCalendar.get(Calendar.MONTH);
        int alarmDay=dateCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar timeCalendar=Calendar.getInstance();
        timeCalendar.setTime(ConvertStringToTime(events.getTIME()));
        int alamrHour=timeCalendar.get(Calendar.HOUR_OF_DAY);
        int alamrMinut=timeCalendar.get(Calendar.MINUTE);




    //clique sur la cloche de notification
        holder.setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAlarmed(events.getDATE(),events.getEVENT(),events.getTIME()))//si s'etait en on
                {
                    holder.setAlarm.setImageResource(R.drawable.ic_baseline_notifications_off_24); // modification en off
                    cancelAlarm(getRequestCode(events.getDATE(),events.getEVENT(),events.getTIME()));
                    updateEvent(events.getDATE(),events.getEVENT(),events.getTIME(),"off");
                    //notifyDataSetChanged();
                }
                else { //sinon
                    holder.setAlarm.setImageResource(R.drawable.ic_baseline_notifications_on_24);// modification en on
                    Calendar alarmCalendar=Calendar.getInstance();
                    //mise a jour des elements
                    alarmCalendar.set(alarmYear,alarmMonth,alarmDay,alamrHour,alamrMinut);
                    setAlarm(alarmCalendar,events.getEVENT(),events.getTIME(),getRequestCode(events.getDATE(),events.getEVENT(),events.getTIME()));
                    updateEvent(events.getDATE(),events.getEVENT(),events.getTIME(),"on");

                   // notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    // retourne le nombre d'evenement
    public int getItemCount() {
        return arrayList.size();
    }

//
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView DateTxt,Event,Time;
        Button delete,update,carte;
        ImageButton setAlarm;
        //instanciation des elements de l'evenement par les valeur de l'item
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DateTxt=itemView.findViewById(R.id.eventdate);
            Event=itemView.findViewById(R.id.eventname);
            Time=itemView.findViewById(R.id.eventtime);
            delete=itemView.findViewById(R.id.delete);
            update=itemView.findViewById(R.id.update);
            setAlarm=itemView.findViewById(R.id.alarmbtn);
            carte=itemView.findViewById(R.id.carte);

        }
    }
    //conversion String en Date
    public Date ConvertStringToDate(String eventDate)
    {
        SimpleDateFormat format= new SimpleDateFormat(("yyyy-MM-dd"), Locale.getDefault());
        Date date=null;
        try {
            date=format.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    //conversion String en format heure

    public Date ConvertStringToTime(String eventDate)
    {
        SimpleDateFormat format= new SimpleDateFormat(("kk:mm"), Locale.getDefault());
        Date date=null;
        try {
            date=format.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    //mise a jour evenement dans la base de données
    public  void updateCalendarEvent(String Newevent, String Newtime,String Oldevent, String Oldtime, String OldDate ){
        dbOpenHelper=new DBOpenHelper(context);
        SQLiteDatabase database=dbOpenHelper.getWritableDatabase();
        dbOpenHelper.updateEvent1(Newevent,Newtime,Oldevent,Oldtime,OldDate,database);
        dbOpenHelper.close();
    }

//supprimer evenement dans la base de données du calendrier
    public void deleteCalendarEvent(String event,String date, String time){
        dbOpenHelper=new DBOpenHelper(context);
        SQLiteDatabase database=dbOpenHelper.getWritableDatabase();
        dbOpenHelper.deleteEvent(event,date,time,database);
        dbOpenHelper.close();

    }
    //si notify est à "on" ou "off"
    public boolean isAlarmed(String date,String event,String time)
    {
        boolean alarmed=false;
        dbOpenHelper=new DBOpenHelper(context);
        SQLiteDatabase database=dbOpenHelper.getReadableDatabase();
        Cursor cursor=dbOpenHelper.ReadIDEvents(date,event,time,database);
        while (cursor.moveToNext()){
            String notify =cursor.getString(cursor.getColumnIndex(DBStructure.Notify));
            String id =cursor.getString(cursor.getColumnIndex(DBStructure.ID));
            if (notify.equals("on")) alarmed=true;
            else alarmed=false;
        }
        cursor.close();
        dbOpenHelper.close();
        return alarmed;
        }

        //modifier alarm
    private void setAlarm(Calendar calendar, String event, String time, int RequestCOde){
        Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
        intent.putExtra("event",event);
        intent.putExtra("time",time);
        intent.putExtra("id",RequestCOde);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,RequestCOde,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }


// pour desactiver une notification
    private void cancelAlarm(int RequestCOde){
        Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,RequestCOde,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
// rettourne l'id d'un evenement
    private  int getRequestCode(String date, String event, String time) {
        int code = 0;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvents(date, event, time, database);
        while (cursor.moveToNext()) {
            code = cursor.getInt(cursor.getColumnIndex(DBStructure.ID));
        }
        cursor.close();
        dbOpenHelper.close();
        return code;
    }
    // modifier notification dans la base de données
    public void updateEvent(String date,String event,String time,String notify)
    {
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.updateEvent(date,event,time,notify,database);
        dbOpenHelper.close();
        }

    }
