package com.example.agenda;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyGridAdapter extends ArrayAdapter {
    //declarartion des variables
    List<Date> dates;
    Calendar currentDate;
    List<Events> events;
    LayoutInflater inflater;
//constructeur
    public MyGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, List<Events> events) {
        //j'ai supprimeé le second parametre int ressource dns le constructeur'
        super(context, R.layout.single_cell_layout);
        this.dates=dates;
        this.currentDate=currentDate;
        this.events=events;
        inflater=LayoutInflater.from(context);
    }
    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //recupere les elements correspondants a la view de la position
        Date monthDate=dates.get(position);
        Calendar dateCalendar=Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int dayNo =dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth=dateCalendar.get(Calendar.MONTH)+1;
        int displayYear=dateCalendar.get(Calendar.YEAR);
        int currentMonth=currentDate.get(Calendar.MONTH)+1;
        int currentYear=currentDate.get(Calendar.YEAR);

        View view=convertView;
        if(view==null){ //si y'a aucun view
           view=inflater.inflate(R.layout.single_cell_layout,parent,false);//initialisation view
        }
        if(displayMonth==currentMonth && displayYear==currentYear)
        {
            //view.setBackgroundColor(getContext().getResources().getColor(R.color.green));
        }
        else {// coloration des jours des mois n'appartenant pas au mois courant
            TextView v= (TextView) view.findViewById(R.id.calendar_day);
            v.setTextColor(Color.parseColor("#BEB9BB"));
        }

        //coloration et mise en forme de la date du jour
        if(currentDate.get(Calendar.DAY_OF_MONTH)==dayNo && currentMonth==displayMonth && currentDate.get(Calendar.YEAR)==displayYear)
        {
            TextView v= (TextView) view.findViewById(R.id.calendar_day);
            v.setBackground(getContext().getResources().getDrawable(R.drawable.today));
            v.setTextColor(Color.parseColor("#FFFFFF"));
            v.setGravity(Gravity.CENTER_HORIZONTAL);
            v.setPadding(5,0,5,0);

        }
        TextView day_Number= (TextView) view.findViewById(R.id.calendar_day);
        TextView event_Number=(TextView) view.findViewById(R.id.event_id);
        day_Number.setText(String.valueOf(dayNo));
        Calendar eventCalendar=Calendar.getInstance();
        ArrayList<String> arrayList=new ArrayList<>();

        //ajout de la liste des evenements d'une date dans arrayList
        for (int i=0;i<events.size();i++)
        {
            eventCalendar.setTime(ConvertStringToDate(events.get(i).getDATE()));
            if(dayNo==eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth==eventCalendar.get(Calendar.MONTH)+1
                    && displayYear==eventCalendar.get(Calendar.YEAR))
            {
                arrayList.add(events.get(i).getEVENT());
                event_Number.setText(arrayList.size()+" Events");
            }
        }
        return view;
    }
    //conversion string en Date
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

    @Override
    //retourne la taille de lz liste des date
    public int getCount() {
        return dates.size();
    }

    @Nullable
    @Override
    //retourne objet a lla position donner de la liste des dates
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    //retourne la position d'un Objet
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }
}
