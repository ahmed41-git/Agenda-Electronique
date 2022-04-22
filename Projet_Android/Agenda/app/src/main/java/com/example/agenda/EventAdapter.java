package com.example.agenda;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
/*classe pour gerer la liste des trois premiers evenements a venir
afficher en bas de la page de du calendrier
 */

public class EventAdapter  extends ArrayAdapter<Events> {
    private final Context context;
    private final ArrayList<Events> data;
    private final int layoutResourceId;
//constructeur
    public EventAdapter(Context context, int layoutResourceId, ArrayList<Events> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    //pour obtenir et manipuler chaque view de la liste view
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.textView1 = (TextView)row.findViewById(R.id.da);
            holder.textView2 = (TextView)row.findViewById(R.id.ev);
            holder.textView3 = (TextView)row.findViewById(R.id.ti);

            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }
        Events event = data.get(position);
//personnalisation des elements
        holder.textView1.setText(event.getDATE());
        holder.textView2.setText(event.getEVENT());
        holder.textView3.setText(event.getTIME());
        return row;
    }
    static class ViewHolder
    {
        //declaration des elements
        TextView textView1;
        TextView textView2;
        TextView textView3;
    }
}
