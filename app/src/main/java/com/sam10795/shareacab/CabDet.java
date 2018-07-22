package com.sam10795.shareacab;

import com.parse.ParseGeoPoint;

import java.util.Date;

/**
 * Created by SAM10795 on 26-07-2015.
 */
public class CabDet {
    public String Name;
    public long Time;
    public java.util.Date Date;
    public int pjoin;
    public boolean booked;
    public String cabserv;
    public String devID;
    public String Number;
    public String Email;
    public double fromlat,fromlon,tolat,tolon;

    public String getDevID() {
        return devID;
    }

    public void setDevID(String devID) {
        this.devID = devID;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public void setCabserv(String cabserv) {
        this.cabserv = cabserv;
    }

    public void setDate(Date date) {
        Date = date;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPjoin(int pjoin) {
        this.pjoin = pjoin;
    }

    public void setTime(long time) {
        Time = time;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public void setFromlat(double fromlat)
    {
        this.fromlat = fromlat;
    }

    public void setFromlon(double fromlon) {
        this.fromlon = fromlon;
    }

    public void setTolat(double tolat) {
        this.tolat = tolat;
    }

    public void setTolon(double tolon) {
        this.tolon = tolon;
    }

    public String getEmail() {
        return Email;
    }

    public String getNumber() {
        return Number;
    }

    public int getPjoin() {
        return pjoin;
    }

    public String getCabserv() {
        return cabserv;
    }

    public Date getDate() {
        return Date;
    }

    public String getName() {
        return Name;
    }

    public long getTime() {
        return Time;
    }

    public boolean isBooked() {
        return booked;
    }

    public double getFromlat() {
        return fromlat;
    }

    public double getFromlon() {
        return fromlon;
    }

    public double getTolat() {
        return tolat;
    }

    public double getTolon() {
        return tolon;
    }
}
