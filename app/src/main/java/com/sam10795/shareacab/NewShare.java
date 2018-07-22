package com.sam10795.shareacab;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by SAM10795 on 25-07-2015.
 */
public class NewShare extends Activity {
    String from,to,date,time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent.hasExtra("From")&&intent.hasExtra("To")&&intent.hasExtra("Date")&&intent.hasExtra("Time")) {
            from = intent.getStringExtra("From");
            to = intent.getStringExtra("To");
            date = intent.getStringExtra("Date");
            time = intent.getStringExtra("Time");

            Parse.initialize(this, "IDb0Bi3U9UolZIzP0cbzOxjHtF1EKhXju0filPaf", "N2ESgnhsRGxlXWweOHqxKOF5ymwizmNeJNqtmVuQ");

            Parse.enableLocalDatastore(this);

            SharedPreferences sp = getSharedPreferences("Details",MODE_PRIVATE);

            ParseObject cabRequest = new ParseObject("CabRequest");
            TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
            cabRequest.put("ID",tm.getDeviceId());
            cabRequest.put("Name",sp.getString("Name", "Anonymous"));
            cabRequest.put("Number",sp.getString("Number","Number not provided"));
            cabRequest.put("Email",sp.getString("EmailID","EmailID not provided"));
            cabRequest.put("From",from);
            cabRequest.put("To",to);
            cabRequest.put("Date",date);
            cabRequest.put("Time",time);
            cabRequest.put("Pjoin",1);
            cabRequest.put("Booked",false);
            cabRequest.put("CabServ","Cab not Booked");
            cabRequest.saveInBackground();
            if(cabRequest.saveInBackground().isCompleted())
            {
                Intent newintent = new Intent(this,ShareYes.class);
                startActivity(newintent);
            }

        }
    }


}
