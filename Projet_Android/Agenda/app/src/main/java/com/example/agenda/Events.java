package com.example.agenda;
/*
structuration d'un evenement
 */
public class Events {
    String EVENT, TIME, DATE, MONTH, YEAR,LOCATION;

    public Events(String EVENT, String TIME, String DATE, String MONTH, String YEAR,String LOCATION) {
        this.EVENT = EVENT;
        this.TIME = TIME;
        this.DATE = DATE;
        this.MONTH = MONTH;
        this.YEAR = YEAR;
        this.LOCATION=LOCATION;
    }
    //pour recupération titre de l'evenement

    public String getEVENT() {
        return EVENT;
    }

    //pour modification titre de l'evenement

    public void setEVENT(String EVENT) {
        this.EVENT = EVENT;
    }

    //pour recupération heure de l'evenement
    public String getTIME() {
        return TIME;
    }
    //pour modification heure de l'evenement
    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    //pour recupération date de l'evenement
    public String getDATE() {
        return DATE;
    }

    //pour modification date de l'evenement
    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    //pour recupération mois de l'evenement
    public String getMONTH() {
        return MONTH;
    }

    //pour modification mois de l'evenement
    public void setMONTH(String MONTH) {
        this.MONTH = MONTH;
    }

    //pour recupération année de l'evenement
    public String getYEAR() {
        return YEAR;
    }

    //pour modification année de l'evenement
    public void setYEAR(String YEAR) {
        this.YEAR = YEAR;
    }

    // pour recuperation adresse evenement
    public String getLOCATION() {
        return LOCATION;
    }

    //pour modifier adresse evenement
    public void setLOCATION(String LOCATION) {
        this.LOCATION = LOCATION;
    }

}
