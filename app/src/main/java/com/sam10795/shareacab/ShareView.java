package com.sam10795.shareacab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseGeoPoint;
import com.sam10795.shareacab.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by SAM10795 on 25-07-2015.
 */
public class ShareView extends Activity {

    ArrayList<CabDet> cabDets = new ArrayList<>();
    TextView f,t,na;
    ListView listView;
    String from,to,date;
    double fromlat,fromlon,tolat,tolon;
    Date date1;
    long time;
    long tthresh = 3600000;
    long dthresh = 5;
    ParseObject cabRequest;
    ProgressDialog progressDialog;
    ParseGeoPoint gfrom,gto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_requests);
        final TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        final SharedPreferences sp = getSharedPreferences("Details",MODE_PRIVATE);
        Intent intent = getIntent();
        if(intent.hasExtra("FromStr")&&
                intent.hasExtra("ToStr")&&
                intent.hasExtra("DateTime")&&
                intent.hasExtra("FromLat")&&
                intent.hasExtra("FromLon")&&
                intent.hasExtra("ToLat")&&
                intent.hasExtra("ToLon")&&
                netconnected())
        {
            progressDialog = new ProgressDialog(ShareView.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            from = intent.getStringExtra("FromStr");
            to = intent.getStringExtra("ToStr");
            date = intent.getStringExtra("DateTime");
            tthresh = tthresh*sp.getInt("Time",1);
            dthresh = sp.getInt("DThresh",5);
            double dthlat = dthresh/111;
            double dthlon = dthresh/100;
            fromlat = intent.getDoubleExtra("FromLat", 0);
            fromlon = intent.getDoubleExtra("FromLon",0);
            tolat = intent.getDoubleExtra("ToLat",0);
            tolon = intent.getDoubleExtra("ToLon",0);

            DateFormat d1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            try {
                date1 = d1.parse(date);
                time = date1.getTime();
                Log.e(date1.toString(),Long.toString(time));
            }
            catch(Exception e)
            {
                Log.e("Date read","Error");
            }

            f = (TextView)findViewById(R.id.from);
            t = (TextView)findViewById(R.id.to);
            na = (TextView)findViewById(R.id.na);
            listView = (ListView)findViewById(R.id.listView);
            na.setVisibility(View.GONE);
            f.setText("From: "+from);
            t.setText("To: "+to);

            progressDialog.show();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("CabRequest");
            query.whereGreaterThanOrEqualTo("FromLat",fromlat-dthresh);
            query.whereLessThanOrEqualTo("FromLat",fromlat+dthresh);
            query.whereGreaterThanOrEqualTo("ToLat",tolat-dthresh);
            query.whereLessThanOrEqualTo("ToLat",tolat+dthresh);
            query.whereGreaterThanOrEqualTo("FromLon",fromlon-dthresh);
            query.whereLessThanOrEqualTo("FromLon",fromlon+dthresh);
            query.whereGreaterThanOrEqualTo("ToLon",tolon-dthresh);
            query.whereLessThanOrEqualTo("ToLon",tolon+dthresh);
            query.whereGreaterThanOrEqualTo("Time", time - tthresh);
            query.whereLessThanOrEqualTo("Time",time + tthresh);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if(e==null)
                    {
                        int size = parseObjects.size();
                        if(size>0) {
                            for (int i = 0; i < size; i++) {
                                CabDet cabDet = new CabDet();
                                cabDet.setDate(parseObjects.get(i).getDate("Date"));
                                cabDet.setBooked(parseObjects.get(i).getBoolean("Booked"));
                                cabDet.setName(parseObjects.get(i).getString("Name"));
                                cabDet.setTime(parseObjects.get(i).getLong("Time"));
                                cabDet.setPjoin(parseObjects.get(i).getInt("Pjoin"));
                                cabDet.setDevID(parseObjects.get(i).getString("ID"));
                                cabDet.setNumber(parseObjects.get(i).getString("Number"));
                                cabDet.setEmail(parseObjects.get(i).getString("Email"));
                                cabDet.setFromlat(parseObjects.get(i).getDouble("FromLat"));
                                cabDet.setTolat(parseObjects.get(i).getDouble("ToLat"));
                                cabDet.setFromlon(parseObjects.get(i).getDouble("FromLon"));
                                cabDet.setTolon(parseObjects.get(i).getDouble("ToLon"));
                                if (cabDet.isBooked()) {
                                    cabDet.setCabserv(parseObjects.get(i).getString("CabServ"));
                                }
                                cabDets.add(cabDet);
                            }
                            progressDialog.dismiss();
                            listView.setAdapter(new CabAdapter(ShareView.this,cabDets));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    final CabDet cab = (CabDet)parent.getItemAtPosition(position);

                                    Dialog dialog = new Dialog(ShareView.this);
                                    dialog.setTitle("Contact "+cab.getName());
                                    dialog.setContentView(R.layout.contact);
                                    TextView call = (TextView)dialog.findViewById(R.id.call);
                                    TextView email = (TextView)dialog.findViewById(R.id.email);
                                    TextView sms = (TextView)dialog.findViewById(R.id.message);
                                    TextView notif = (TextView)dialog.findViewById(R.id.notif);
                                    Log.e("LOG",cab.getName());
                                    call.setText("Call "+cab.getName());
                                    email.setText("Email "+cab.getName());
                                    sms.setText("Send "+cab.getName()+" an SMS");
                                    notif.setText("Notify "+cab.getName()+" in-app");

                                    call.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent callin = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",cab.getNumber(),null));
                                            startActivity(callin);
                                        }
                                    });
                                    email.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent mailin = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto",cab.getEmail(),null));
                                            mailin.putExtra(Intent.EXTRA_SUBJECT,"Share Cab");
                                            mailin.putExtra(Intent.EXTRA_TEXT,"Hi "+cab.getName()+",\n This is "+sp.getString("Name","")+
                                                    ".\nI would like to share the cab with you on "+cab.getDate().toString()+
                                                    ".\n Please let me know if it is possible.\nRegards,\n"+sp.getString("Name",""));
                                            startActivity(mailin);
                                        }
                                    });
                                    sms.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent SMS = new Intent(Intent.ACTION_VIEW,Uri.fromParts("sms",cab.getNumber(),null));
                                            SMS.putExtra("sms_body","This is "+sp.getString("Name","")+".\nI would like to share the cab with you on "+cab.getDate().toString()+".\n" +
                                                    " Please let me know if it is possible.");
                                            startActivity(SMS);
                                        }
                                    });
                                    notif.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            try {
                                                JSONObject jo = new JSONObject("{\"alert\" : \""+sp.getString("Name","")+" wants to share a cab with you\"," +
                                                        "\"title\" : \"New cab share request\", \"id\" : \""+tm.getDeviceId()+"\"," +
                                                        "\"new\" : true, \"code\" : 0}");

                                            ParseQuery<ParseInstallation> push = ParseInstallation.getQuery();
                                            push.whereEqualTo("ID",cab.getDevID());
                                                Log.e("ID",cab.getDevID());
                                            ParsePush p = new ParsePush();
                                            p.setQuery(push);
                                            p.setData(jo);
                                            p.setExpirationTime(cab.getTime()/1000);
                                            p.sendInBackground(new SendCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if(e==null)
                                                    {
                                                        Toast.makeText(ShareView.this,"Notification sent",Toast.LENGTH_SHORT).show();
                                                        SharedPreferences sp = getSharedPreferences("Notif",MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sp.edit();
                                                        editor.apply();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(ShareView.this,"There was an error",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                                Log.e("JSON","Error");
                                            }
                                        }
                                    });

                                    dialog.show();

                                }
                            });
                        }
                        else
                        {
                            progressDialog.dismiss();
                            listView.setVisibility(View.GONE);
                            na.setVisibility(View.VISIBLE);
                        }
                        Button button = (Button)findViewById(R.id.nreq);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                cabRequest = new ParseObject("CabRequest");
                                cabRequest.put("ID",tm.getDeviceId());
                                cabRequest.put("Name",sp.getString("Name", "Anonymous"));
                                cabRequest.put("Number",sp.getString("Number","Number not provided"));
                                cabRequest.put("Email",sp.getString("EmailID","EmailID not provided"));
                                cabRequest.put("From",from);
                                cabRequest.put("To",to);
                                cabRequest.put("Date",date1);
                                cabRequest.put("Time",time);
                                cabRequest.put("Pjoin",1);
                                cabRequest.put("FromLat",fromlat);
                                cabRequest.put("FromLon",fromlon);
                                cabRequest.put("ToLat",tolat);
                                cabRequest.put("ToLon",tolon);
                                cabRequest.put("CabServ","Cab not Booked");
                                cabRequest.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null) {
                                            ParsePush.subscribeInBackground("C"+tm.getDeviceId(),new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if(e==null)
                                                    {
                                                        final ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("UserData");
                                                        parseQuery.whereEqualTo("ID",tm.getDeviceId());
                                                        parseQuery.findInBackground(new FindCallback<ParseObject>() {
                                                            @Override
                                                            public void done(List<ParseObject> parseObjects, ParseException e) {
                                                                if(parseObjects.size()!=0&&e==null)
                                                                {
                                                                    ParseObject po = parseObjects.get(0);
                                                                    String id = po.getObjectId();
                                                                    ParseQuery.getQuery("UserData").getInBackground(id, new GetCallback<ParseObject>() {
                                                                        @Override
                                                                        public void done(ParseObject parseObject, ParseException e) {
                                                                            if(e==null) {
                                                                                parseObject.put("channels","C"+tm.getDeviceId());
                                                                                parseObject.saveInBackground();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                else
                                                                {
                                                                    Log.e("Error","ShareView");
                                                                }
                                                            }
                                                        });
                                                        Log.e("DATE1",date1.toString());
                                                        String obid = cabRequest.getObjectId();
                                                        SharedPreferences.Editor ed = sp.edit();
                                                        ed.putString("ObjectID",obid);
                                                        ed.putLong("time", time);
                                                        ed.apply();
                                                        SharedPreferences sharedPreferences = getSharedPreferences("Notif",MODE_PRIVATE);
                                                        SharedPreferences.Editor ned = sharedPreferences.edit();
                                                        ned.putInt("code",0);
                                                        ned.putBoolean("new",false);
                                                        ned.putString("id",tm.getDeviceId());
                                                        ned.apply();
                                                        Intent newintent = new Intent(ShareView.this, ShareYes.class);
                                                        startActivity(newintent);
                                                    }
                                                    else
                                                    {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });

                                        }
                                        else
                                        {
                                            Log.e("Save","Error");
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(ShareView.this,"Error",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
        else
        {
            if(!netconnected())
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
            }
            else {
                Toast.makeText(this, "An error occured", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public boolean netconnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        return ni!=null && ni.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
