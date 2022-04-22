package com.example.agenda;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
public class DBOpenHelper extends SQLiteOpenHelper {
    // declaration variable d'structuration de la base de données
    private  final static String CREATE_EVENTS_TABLE = "create table "+ DBStructure.EVENT_TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            +DBStructure.EVENT+" TEXT, "+DBStructure.TIME+" TEXT, "+DBStructure.DATE+" TEXT, "+DBStructure.MONTH+" TEXT, "
            +DBStructure.YEAR+" TEXT, "+DBStructure.Notify+" TEXT, "+DBStructure.Location+" TEXT);";
    private static final String DROP_EVENTS_TABLE ="DROP TABLE IF EXISTS "+DBStructure.EVENT_TABLE_NAME;
    public DBOpenHelper(@Nullable Context context) {
        super(context, DBStructure.DB_NAME, null, DBStructure.DB_VERSION);
    }
    @Override
    //creation de la table eventstable
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_EVENTS_TABLE);
        onCreate(db);
    }

    //sauvegarder évenement dans la base
    public void SaveEvent(String event, String time, String date, String month, String year,String notify,String location, SQLiteDatabase database ){
        ContentValues contentValues= new ContentValues();
        contentValues.put(DBStructure.EVENT,event);
        contentValues.put(DBStructure.TIME,time);
        contentValues.put(DBStructure.DATE,date);
        contentValues.put(DBStructure.MONTH,month);
        contentValues.put(DBStructure.YEAR,year);
        contentValues.put(DBStructure.Notify,notify);
        contentValues.put(DBStructure.Location,location);
        database.insert(DBStructure.EVENT_TABLE_NAME,null,contentValues);
    }
    /*recupération des éléments event,time,date,month,year dans la base de données
        d'une date donnée
         */
    public Cursor ReadEvents(String date, SQLiteDatabase database){
        String [] Projections= {DBStructure.EVENT, DBStructure.TIME, DBStructure.DATE, DBStructure.MONTH,DBStructure.Location, DBStructure.YEAR};
        String Selection= DBStructure.DATE+ "=?";
        String [] SelectinArgs= {date};
        return database.query(DBStructure.EVENT_TABLE_NAME,Projections,Selection,SelectinArgs,null,null,null);
    }

    //recupération de l'id et du notification de l'evenement d'une date, event et time données

    public Cursor ReadIDEvents(String date,String event, String time, SQLiteDatabase database){
        String [] Projections= {DBStructure.ID, DBStructure.Notify};
        String Selection= DBStructure.DATE+"=? and "+DBStructure.EVENT+"=? and "+DBStructure.TIME+"=?";
        String [] SelectinArgs= {date,event,time};
        return database.query(DBStructure.EVENT_TABLE_NAME,Projections,Selection,SelectinArgs,null,null,null);
    }

    //recupération des éléments event,time,date,month,year d'un mois et année donnés

    public Cursor ReadEventsperMonth(String month,String year, SQLiteDatabase database){
        String [] Projections= {DBStructure.EVENT, DBStructure.TIME, DBStructure.DATE, DBStructure.MONTH, DBStructure.YEAR,DBStructure.Location};
        String Selection= DBStructure.MONTH+ "=? and " +DBStructure.YEAR+" =?";
        String [] SelectinArgs= {month,year};
        return database.query(DBStructure.EVENT_TABLE_NAME,Projections,Selection,SelectinArgs,null,null,null);
    }



    public Cursor ReadEventsperMonth2(SQLiteDatabase database){
        String [] Projections= {DBStructure.EVENT, DBStructure.TIME, DBStructure.DATE, DBStructure.MONTH, DBStructure.YEAR,DBStructure.Location};
        return database.query(DBStructure.EVENT_TABLE_NAME,Projections,null,null,null,null,DBStructure.DATE);
    }

    //suppression d'un événement
    public void deleteEvent(String event, String date, String time, SQLiteDatabase database){
        String selection = DBStructure.EVENT+"=? and "+DBStructure.DATE+"=? and "+DBStructure.TIME+"=?";
        String[] selectionArg = {event,date,time};
        database.delete(DBStructure.EVENT_TABLE_NAME,selection,selectionArg);
    }

    //modification de la notification
    public void updateEvent(String date,String event, String time,String notify, SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBStructure.Notify,notify);
        String Selection= DBStructure.DATE+"=? and "+DBStructure.EVENT+"=? and "+DBStructure.TIME+"=?";
        String [] SelectinArgs= {date,event,time};
        database.update(DBStructure.EVENT_TABLE_NAME,contentValues,Selection,SelectinArgs);
    }
    //modification d'un événement
    public void updateEvent1(String Newevent, String Newtime,String Oldevent, String Oldtime, String OldDate,SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBStructure.EVENT,Newevent);
        contentValues.put(DBStructure.TIME,Newtime);
        String Selection= DBStructure.DATE+"=? and "+DBStructure.EVENT+"=? and "+DBStructure.TIME+"=?";
        String [] SelectinArgs= {OldDate,Oldevent,Oldtime};
        database.update(DBStructure.EVENT_TABLE_NAME,contentValues,Selection,SelectinArgs);
    }
}