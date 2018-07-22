package com.sam10795.shareacab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.sam10795.shareacab.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.util.Date;
import java.util.List;


public class MainActivity extends Activity implements Animation.AnimationListener {

    boolean firstlaunch;
    long time = 650;
    final String FL = "FIRST LAUNCH";
    int tvids[] = {R.id.S,R.id.H,R.id.A,R.id.R,R.id.E,R.id.A2,R.id.C,R.id.A3,R.id.B,R.id.I,R.id.I2,R.id.T,R.id.M};
    TextView tvs[] = new TextView[13];
    Animation an[] = new Animation[13];
    TelephonyManager tm;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Check if notif request received/accepted
        //request expire check
        //option to cancel

        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("Details",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Intent intent = getIntent();
        if(intent.hasExtra("Exit"))
        {
            if(intent.getBooleanExtra("Exit",false))
            {
                Toast.makeText(this,"FINISH",Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);

        SharedPreferences sp = getSharedPreferences("FL",MODE_PRIVATE);
        final SharedPreferences.Editor ed = sp.edit();
        firstlaunch = sp.getBoolean(FL,true);

        setContentView(R.layout.activity_main);
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
        for(int i=0;i<13;i++)
        {
            an[i] = AnimationUtils.loadAnimation(this,R.anim.splash);
            an[i].setStartOffset(i*time);
            an[i].setAnimationListener(this);
            tvs[i] = (TextView)findViewById(tvids[i]);
            tvs[i].startAnimation(an[i]);
        }

        if(((sharedPreferences.getLong("time",0)-(new Date().getTime()))>(60000))&&(((sharedPreferences.getLong("time",0)-(new Date().getTime()))<(7200000)))&&sp.getBoolean("hj",true))
        {
            NotificationCompat.Builder notif = new NotificationCompat.Builder(this);
            notif.setSmallIcon(R.drawable.drawable);
            notif.setContentTitle("Happy Journey!");
            notif.setContentText("Have a happy and safe journey!");
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1235,notif.build());
            ed.putBoolean("hj",false);
            ed.apply();
        }




        if(netconnected())
        {
            Log.v("Main","Start");

            Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
            ParsePush.subscribeInBackground("Cab", new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");

                    } else {
                        Log.e("com.parse.push", "failed to subscribe for push", e);
                    }
                }
            });

            Log.v("Check","FL");



            if(firstlaunch)
            {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("CabRequest");
                query.whereEqualTo("ID",tm.getDeviceId());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        if(e==null)
                        {
                            if(parseObjects.size()==0)
                            {
                                Intent intent = new Intent(MainActivity.this,Details.class);
                                startActivity(intent);
                            }
                            else
                            {
                                final ParseObject po = parseObjects.get(0);
                                ParseObject installation = new ParseObject("UserData");
                                installation.put("ID",tm.getDeviceId());
                                installation.put("Name",po.getString("Name"));
                                installation.put("Number",po.getString("Number"));
                                installation.put("Email",po.getString("Email"));
                                installation.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null)
                                        {
                                            editor.putString("Name",po.getString("Name"));
                                            editor.putString("Number",po.getString("Number"));
                                            editor.putString("EmailID",po.getString("Email"));
                                            editor.apply();
                                            recreate();
                                        }
                                        else
                                        {
                                            Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "ERROR. Please try again", Toast.LENGTH_SHORT).show();
                            Log.e("Parse", "Error");
                            e.printStackTrace();
                        }
                    }
                });
            }
            else
            {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("CabRequest");
                    query.whereEqualTo("ID",tm.getDeviceId());
                    SharedPreferences notif = getSharedPreferences("Notif",MODE_PRIVATE);
                    final int res = notif.getInt("code",2);
                    query.findInBackground(new FindCallback<ParseObject>()
                    {
                        @Override
                        public void done (List < ParseObject > parseObjects, ParseException e){
                        if (e == null) {
                            if (parseObjects.size() == 0||((res==2)||(res==3))) {
                                Intent intent = new Intent(MainActivity.this, ShareNo.class);
                                startActivity(intent);
                            } else {
                                if(sharedPreferences.getLong("time",0)<=(new Date().getTime())&&(sharedPreferences.getLong("time",0)!=0))
                                {
                                    SharedPreferences notifs = getSharedPreferences("Notif",MODE_PRIVATE);
                                    SharedPreferences.Editor edi = notifs.edit();
                                    edi.putInt("code",2);
                                    edi.putString("id","");
                                    edi.apply();
                                    ed.putBoolean("hj",true);
                                    ed.apply();
                                    ParseQuery<ParseObject> pq = ParseQuery.getQuery("CabRequest");
                                    pq.getInBackground(sharedPreferences.getString("ObjectID",""),new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject parseObject, ParseException e) {
                                            parseObject.deleteInBackground(new DeleteCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if(e==null)
                                                    {
                                                        recreate();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                                else {
                                    Intent intent = new Intent(MainActivity.this, ShareYes.class);
                                    startActivity(intent);
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "ERROR. Please try again", Toast.LENGTH_SHORT).show();
                            Log.e("Parse", "Error");
                            e.printStackTrace();
                        }
                    }
                    }

                    );
                }
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
                            recreate();
                        }
                    })
                    .show();
        }

    }

    public boolean netconnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        return ni!=null && ni.isConnected();
    }



    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        animation.setStartOffset(time*12);
    }
}
