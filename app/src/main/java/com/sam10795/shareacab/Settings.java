package com.sam10795.shareacab;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by SAM10795 on 01-08-2015.
 */
public class Settings extends Activity {

    TextView name,phone,email,time,dist;
    EditText nm,ph,em,tm,ds;
    String n,p,e,t,d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);


        name= (TextView)findViewById(R.id.Name);
        phone = (TextView)findViewById(R.id.Phone);
        email = (TextView)findViewById(R.id.Email);
        time = (TextView)findViewById(R.id.Timethresh);
        dist = (TextView)findViewById(R.id.Disthresh);

        nm= (EditText)findViewById(R.id.Nameed);
        ph = (EditText)findViewById(R.id.Phoneed);
        em = (EditText)findViewById(R.id.Emailed);
        tm = (EditText)findViewById(R.id.Timethreshed);
        ds = (EditText)findViewById(R.id.Disthreshed);

        name.setText("Name");
        phone.setText("Phone number");
        email.setText("Email ID");
        time.setText("Threshold Time");
        dist.setText("Threshold distance");

        SharedPreferences sp = getSharedPreferences("Details",MODE_PRIVATE);
        final SharedPreferences.Editor ed = sp.edit();

        nm.setText(sp.getString("Name","N/A"));
        ph.setText(sp.getString("Number","N/A"));
        em.setText(sp.getString("EmailID","N/A"));
        time.setText(sp.getInt("Time", 1));
        dist.setText(sp.getInt("Dist", 1));

        Button b = (Button)findViewById(R.id.setsv);

        n = nm.getText().toString();
        p = ph.getText().toString();
        t = tm.getText().toString();
        d = ds.getText().toString();
        e = em.getText().toString();

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(valid())
                {
                    ed.putString("Name",n);
                    ed.putString("Number",p);
                    ed.putString("EmailID",e);
                    ed.putInt("Time", Integer.parseInt(t));
                    ed.putInt("Dist",Integer.parseInt(d));
                    ed.apply();

                    Toast.makeText(Settings.this,"Settings updated",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(Settings.this,"Invalid input",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    boolean valid()
    {
        return true;
    }
}
