package com.sam10795.shareacab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sam10795.shareacab.R;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

/**
 * Created by SAM10795 on 25-07-2015.
 */
public class Details extends Activity {

    EditText fullname,phoneno,emailid;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        final SharedPreferences sp = getSharedPreferences("Details",MODE_PRIVATE);
        final SharedPreferences.Editor ed = sp.edit();

        fullname = (EditText)findViewById(R.id.FullName);
        phoneno = (EditText)findViewById(R.id.PhoneNumber);
        emailid = (EditText)findViewById(R.id.EmailID);

        Button button = (Button)findViewById(R.id.setdet);

        fullname.setHint("Enter full name");
        phoneno.setHint("Enter Phone number");
        emailid.setHint("Enter Email ID");

        fullname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    Toast.makeText(Details.this,"This name will be displayed on your posts",Toast.LENGTH_SHORT).show();
                }
            }
        });

        phoneno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    Toast.makeText(Details.this,"This number will be available to other users for contacting you",Toast.LENGTH_SHORT).show();
                }
            }
        });

        emailid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    Toast.makeText(Details.this,"This EmailID will be available to other users for contacting you",Toast.LENGTH_SHORT).show();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                progressDialog = new ProgressDialog(Details.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                if(valid()) {

                    progressDialog.show();

                    final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

                    ed.putString("Name", fullname.getText().toString());
                    ed.putString("Number", phoneno.getText().toString());
                    ed.putString("EmailID", emailid.getText().toString());
                    ed.apply();

                    ParseObject installation = new ParseObject("UserData");
                    installation.put("ID", tm.getDeviceId());
                    installation.put("Name",fullname.getText().toString());
                    installation.put("Number", phoneno.getText().toString());
                    installation.put("Email", emailid.getText().toString());
                    installation.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseInstallation pi = ParseInstallation.getCurrentInstallation();
                                pi.put("ID",tm.getDeviceId());
                                pi.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null)
                                        {
                                            SharedPreferences sharedPreferences = getSharedPreferences("FL", MODE_PRIVATE);
                                            SharedPreferences.Editor ed = sharedPreferences.edit();
                                            ed.putBoolean("FIRST LAUNCH", false);
                                            ed.apply();
                                            Toast.makeText(Details.this, "Saved", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Details.this, MainActivity.class);
                                            progressDialog.dismiss();
                                            SharedPreferences notifs = getSharedPreferences("Notif", MODE_PRIVATE);
                                            SharedPreferences.Editor ned = notifs.edit();
                                            ned.putString("alert", "");
                                            ned.putBoolean("new", false);
                                            ned.putString("id", "");
                                            ned.putInt("code", 2);
                                            ned.apply();
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            Toast.makeText(Details.this,"Error",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {
                                SharedPreferences sharedPreferences = getSharedPreferences("FL", MODE_PRIVATE);
                                SharedPreferences.Editor ed = sharedPreferences.edit();
                                ed.putBoolean("FIRST LAUNCH", true);
                                ed.apply();
                                progressDialog.dismiss();
                                Toast.makeText(Details.this, "ERROR: Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }

    private boolean valid()
    {
        String name = fullname.getText().toString();
        String phone = phoneno.getText().toString();
        String email = emailid.getText().toString();
        if(netconnected()) {
            return vn(name) && vp(phone) && ve(email);
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle("Not connected")
                    .setMessage("Please connect to the internet and try again")
                    .setPositiveButton("Try Again",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            return false;
        }
    }
    private boolean vn(String name)
    {
        boolean retval = true;
        if(name.length()<=3||!(name.contains(" ")))
        {
            Toast.makeText(this,"Please enter full name",Toast.LENGTH_SHORT).show();
            retval = false;
        }
        else {
            for (int i = 0; i < name.length(); i++) {
                if(!(Character.isLetter(name.charAt(i))||name.charAt(i)==' '))
                {
                    Toast.makeText(this,"Name cannot include digits or symbols",Toast.LENGTH_SHORT).show();
                    retval = false;
                }
            }
        }
        return retval;
    }
    public boolean netconnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        return ni!=null && ni.isConnected();
    }
    private boolean vp(String phone)
    {
        boolean retval = true;
        if(phone.length()>10)
        {
            if(phone.startsWith("0")||phone.startsWith("+91")) {
                retval = false;
                Toast.makeText(this, "Please enter the 10 digit phone number", Toast.LENGTH_SHORT).show();
            }
            else if(phone.startsWith("+"))
            {
                retval = true;
            }
            else
            {
                retval = false;
                Toast.makeText(this,"Phone number not valid",Toast.LENGTH_SHORT).show();
            }
        }
        else if(phone.length()<10)
        {
            retval = false;
            Toast.makeText(this,"Number must not be less than 10 digits long",Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(int i = 0;i<phone.length();i++)
            {
                if(!(Character.isDigit(phone.charAt(i))||phone.charAt(i)=='+'))
                {
                    retval = false;
                    Toast.makeText(this,"Invalid number format",Toast.LENGTH_SHORT).show();
                }
            }
        }
        return retval;
    }
    private boolean ve(String email)
    {
        if(!(email.contains("@")&&email.contains(".")))
        {
            Toast.makeText(this,"Invalid EmailID format",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences("FL", MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putBoolean("FIRST LAUNCH", true);
        ed.apply();
        Intent exintent = new Intent(this,MainActivity.class);
        exintent.putExtra("Exit",true);
        startActivity(exintent);
    }
}
