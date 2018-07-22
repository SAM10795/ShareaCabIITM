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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SendCallback;
import com.sam10795.shareacab.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by SAM10795 on 25-07-2015.
 */
public class ShareYes extends Activity {
    int pjoin = 1;
    String Cabserv = "Cab not booked";
    boolean booked = false;
    EditText pj,cab;
    CheckBox book;
    ProgressDialog progressDialog;
    TextView intro, from , to, date, cabs, pejo;
    String name, phone, email, dt,id;
    ListView listView;
    ArrayList<Person> persons = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_yes);
        progressDialog = new ProgressDialog(ShareYes.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        final SharedPreferences sp = getSharedPreferences("Details",MODE_PRIVATE);
        final SharedPreferences.Editor ed = sp.edit();
        booked = sp.getBoolean("Booked",false);
        intro = (TextView)findViewById(R.id.textView);
        pj = (EditText)findViewById(R.id.pjoin);
        pejo = (TextView)findViewById(R.id.textView6);
        book = (CheckBox)findViewById(R.id.booked);
        book.setVisibility(View.GONE);
        cab = (EditText)findViewById(R.id.editText);
        cab.setVisibility(View.GONE);
        Button b = (Button) findViewById(R.id.send);
        Button del = (Button) findViewById(R.id.del);
        listView = (ListView)findViewById(R.id.listView2);

        from = (TextView)findViewById(R.id.textView2);
        to = (TextView)findViewById(R.id.textView3);
        date = (TextView) findViewById(R.id.textView4);
        cabs = (TextView)findViewById(R.id.textView7);


        if(netconnected()) {

            final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            progressDialog.show();

            cab.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus)
                    {
                        cab.setHint("");
                    }
                    else
                    {
                        cab.setHint(Cabserv);
                    }
                }
            });


            final SharedPreferences notifs = getSharedPreferences("Notif",MODE_PRIVATE);
            final SharedPreferences.Editor editor = notifs.edit();
            ParseQuery<ParseObject> q0 = ParseQuery.getQuery("CabRequest");

            if (notifs.getInt("code",0)==0) {
                q0.whereEqualTo("ID", telephonyManager.getDeviceId());
                id = telephonyManager.getDeviceId();
                cab.setVisibility(View.VISIBLE);
                book.setVisibility(View.VISIBLE);
            } else {
                q0.whereEqualTo("ID", notifs.getString("id", ""));
                ParsePush.subscribeInBackground("C" + notifs.getString("id", ""));
                id = notifs.getString("id","");
                ParseQuery.getQuery("UserData").whereEqualTo("ID",telephonyManager.getDeviceId()).findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        if(parseObjects.size()!=0&&e==null)
                        {
                            String obid = parseObjects.get(0).getObjectId();
                            ParseQuery.getQuery("UserData").getInBackground(obid,new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if(e==null)
                                    {
                                        parseObject.put("channels",id);
                                        parseObject.saveInBackground();
                                    }

                                }
                            });
                        }
                    }
                });
                pj.setVisibility(View.GONE);
                b.setText("Contact Creator");
                del.setText("Leave Group");
            }
            q0.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        if (parseObjects.size() != 0) {
                            ParseObject p = parseObjects.get(0);
                            if (!sp.getString("ObjectID", "").equals(p.getObjectId())) {
                                ed.putString("ObjectID", p.getObjectId());
                                ed.apply();
                                progressDialog.dismiss();
                                recreate();
                            }
                        }
                        else
                        {
                            progressDialog.dismiss();
                            editor.putInt("code",2);
                            Toast.makeText(ShareYes.this,"Request not found",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ShareYes.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            });


            ParseQuery<ParseObject> q1 = ParseQuery.getQuery("CabRequest");
            q1.getInBackground(sp.getString("ObjectID", ""), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (parseObject != null) {
                        dt = parseObject.getDate("Date").toString();
                        from.setText("From: " + parseObject.getString("From"));
                        to.setText("To: " + parseObject.getString("To"));
                        date.setText("Date and Time: " + dt.substring(0, dt.indexOf("GMT") - 1));
                        name = parseObject.getString("Name");
                        phone = parseObject.getString("Number");
                        email = parseObject.getString("Email");
                        ed.putLong("time",parseObject.getLong("Time"));
                        if(notifs.getInt("code",0)==0) {
                            pj.setHint(Integer.toString(parseObject.getInt("Pjoin")));
                            pj.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (hasFocus) {
                                        pj.setHint("");
                                    }
                                    else
                                    {
                                        pj.setHint(Integer.toString(pjoin));
                                    }
                                }
                            });
                        }
                        else
                        {
                            intro.setText("Group Creator: "+name);
                            pejo.setText("People Joined: "+parseObject.getInt("Pjoin"));
                            cabs.setText("Cab service: " + parseObject.getString("CabServ"));
                        }
                        Cabserv = parseObject.getString("CabServ");
                        cab.setHint(Cabserv);
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        to.setText("Object not found");
                    }
                }
            });

            ParseQuery<ParseObject> pq = ParseQuery.getQuery("UserData");
            pq.whereEqualTo("channels", "C" + id);
            pq.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null && parseObjects.size() != 0) {
                        for (int i = 0; i < parseObjects.size(); i++) {
                            Person person = new Person();
                            ParseObject po = parseObjects.get(i);
                            person.setName(po.getString("Name"));
                            person.setEmail(po.getString("Email"));
                            person.setPhone(po.getString("Phone"));
                            persons.add(person);
                        }

                    } else {
                        Toast.makeText(ShareYes.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if(notifs.getInt("code",0)==0)
            {
                Button message = (Button)findViewById(R.id.button2);
                message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog message = new Dialog(ShareYes.this);
                        message.setContentView(R.layout.message);
                        message.setTitle("Send a notification message to everyone in group");
                        final EditText editText = (EditText)message.findViewById(R.id.editText2);
                        final Button button = (Button)message.findViewById(R.id.button3);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ParseQuery<ParseInstallation> piq = ParseInstallation.getQuery();
                                piq.whereEqualTo("channels","C"+id);
                                ParsePush.sendMessageInBackground(editText.getText().toString(),piq,new SendCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null)
                                        {
                                            Toast.makeText(ShareYes.this,"Sent",Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(ShareYes.this,"Error",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }

            Log.e("Size",Integer.toString(persons.size()));
            listView.setAdapter(new JoinAdapter(ShareYes.this,persons,notifs.getBoolean("response",false)));
            listView.setVisibility(View.GONE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Person person = (Person)parent.getItemAtPosition(position);
                    ImageView del = (ImageView)view.findViewById(R.id.delname);
                    ImageView contact = (ImageView)findViewById(R.id.contact);
                    if(person.getName().equals(sp.getString("Name","")))
                    {
                        del.setVisibility(View.GONE);
                        contact.setVisibility(View.GONE);
                    }
                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(ShareYes.this)
                                    .setTitle("Remove from group")
                                    .setMessage("Remove person from group?")
                                    .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("Remove",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ParseQuery<ParseInstallation> pi = ParseInstallation.getQuery();
                                            pi.whereEqualTo("Email",person.getEmail());
                                            pi.findInBackground(new FindCallback<ParseInstallation>() {
                                                @Override
                                                public void done(List<ParseInstallation> parseInstallations, ParseException e) {
                                                    if(e==null&&parseInstallations.size()!=0)
                                                    {
                                                        try {
                                                            JSONObject jo = new JSONObject("{\"alert\" : \""+sp.getString("Name","")+" has removed you from their share group\"," +
                                                                    "\"title\" : \"Removed from group\", \"id\" : \""+telephonyManager.getDeviceId()+"\","+
                                                                    "\"new\" : false, \"code\" : 3}");
                                                            ParsePush push = new ParsePush();
                                                            push.setData(jo);
                                                            push.sendInBackground();
                                                        } catch (JSONException e1) {
                                                            e1.printStackTrace();
                                                        }

                                                    }
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                    contact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialog dialog = new Dialog(ShareYes.this);
                            dialog.setTitle("Contact "+person.getName());
                            dialog.setContentView(R.layout.contact);
                            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.notify);
                            linearLayout.setVisibility(View.GONE);
                            TextView call = (TextView)dialog.findViewById(R.id.call);
                            final TextView emailid = (TextView)dialog.findViewById(R.id.email);
                            TextView sms = (TextView)dialog.findViewById(R.id.message);
                            Log.e("LOG", person.getName());
                            call.setText("Call "+person.getName());
                            emailid.setText("Email "+person.getName());
                            sms.setText("Send "+person.getName()+" an SMS");

                            call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent callin = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", person.getPhone(), null));
                                    startActivity(callin);
                                }
                            });
                            emailid.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent mailin = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto",person.getEmail(),null));
                                    mailin.putExtra(Intent.EXTRA_SUBJECT,"Cab Sharing");
                                    startActivity(mailin);
                                }
                            });
                            sms.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent SMS = new Intent(Intent.ACTION_VIEW,Uri.fromParts("sms",person.getPhone(),null));
                                    startActivity(SMS);
                                }
                            });
                            dialog.show();
                        }
                    });
                }
            });


            ImageView exp = (ImageView)findViewById(R.id.expand);
            exp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listView.getVisibility()==View.GONE)
                    {
                        listView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        listView.setVisibility(View.GONE);
                    }
                }
            });

            if (notifs.getInt("code", 0)==1) {
                b.setVisibility(View.GONE);
                book.setVisibility(View.GONE);
            }
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    if (notifs.getInt("code", 0)==0) {
                        try {
                            pjoin = Math.max(Integer.parseInt(pj.getText().toString()),persons.size());
                        } catch (Exception e) {
                            pjoin = 1;
                        }
                        String objectid = sp.getString("ObjectID", "");
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("CabRequest");
                        query.getInBackground(objectid, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e == null && parseObject != null) {
                                    parseObject.put("Pjoin", pjoin);
                                    parseObject.put("Booked", booked);
                                    if (!booked) {
                                        Cabserv = "Cab not booked";
                                    }
                                    parseObject.put("CabServ", cab.getText().toString());
                                    parseObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(ShareYes.this, "Updated", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                recreate();
                                            } else {
                                                progressDialog.dismiss();
                                                e.printStackTrace();
                                                Toast.makeText(ShareYes.this, "Error in saving. Please try later", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(ShareYes.this, "Error processing request", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Dialog dialog = new Dialog(ShareYes.this);
                        dialog.setTitle("Contact "+name);
                        dialog.setContentView(R.layout.contact);
                        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.notify);
                        linearLayout.setVisibility(View.GONE);
                        TextView call = (TextView)dialog.findViewById(R.id.call);
                        final TextView emailid = (TextView)dialog.findViewById(R.id.email);
                        TextView sms = (TextView)dialog.findViewById(R.id.message);
                        Log.e("LOG", name);
                        call.setText("Call "+name);
                        emailid.setText("Email "+name);
                        sms.setText("Send "+name+" an SMS");

                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent callin = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                                startActivity(callin);
                            }
                        });
                        emailid.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent mailin = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto",email,null));
                                mailin.putExtra(Intent.EXTRA_SUBJECT,"Cab Sharing");
                                startActivity(mailin);
                            }
                        });
                        sms.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent SMS = new Intent(Intent.ACTION_VIEW,Uri.fromParts("sms",phone,null));
                                startActivity(SMS);
                            }
                        });
                        dialog.show();
                    }
                }
            });


            book.setChecked(sp.getBoolean("Booked", booked));
            book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        String text = cab.getText().toString();
                        if(text.equals("")||text.equals(" "))
                        {
                            Cabserv = "No Cab Booked";
                        }
                        else
                        {
                            Cabserv = text;
                        }
                    ed.putBoolean("Booked", book.isChecked());
                    booked = book.isChecked();
                    ed.apply();
                }
            });
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notifs.getInt("code",0)==0) {
                        new AlertDialog.Builder(ShareYes.this)
                                .setTitle("Delete from database?")
                                .setMessage("Deletion cannot be undone")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        progressDialog.show();
                                        String objectid = sp.getString("ObjectID", "");
                                        ParseQuery<ParseObject> pq = ParseQuery.getQuery("CabRequest");
                                        pq.getInBackground(objectid, new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject parseObject, ParseException e) {
                                                if (e == null) {
                                                    parseObject.deleteInBackground(new DeleteCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(ShareYes.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(ShareYes.this, MainActivity.class);
                                                                startActivity(intent);
                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(ShareYes.this, "Error deleting", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ShareYes.this, "Error processing request", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                    else
                    {
                        new AlertDialog.Builder(ShareYes.this)
                                .setTitle("Leave Group?")
                                .setMessage("You'll need to resend request to join again")
                                .setPositiveButton("Leave",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog.show();
                                        ParseQuery<ParseInstallation> pq = ParseInstallation.getQuery();
                                        pq.whereEqualTo("ID",notifs.getString("id",""));
                                        ParsePush push = new ParsePush();
                                        push.setMessage(sp.getString("Name","")+" has left the group");
                                        push.sendInBackground(new SendCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e==null)
                                                {
                                                    ParsePush.unsubscribeInBackground("C"+id);
                                                    ParseQuery.getQuery("UserData").whereEqualTo("ID",telephonyManager.getDeviceId()).findInBackground(new FindCallback<ParseObject>() {
                                                        @Override
                                                        public void done(List<ParseObject> parseObjects, ParseException e) {
                                                            if(parseObjects.size()!=0&&e==null)
                                                            {
                                                                String obid = parseObjects.get(0).getObjectId();
                                                                ParseQuery.getQuery("UserData").getInBackground(obid,new GetCallback<ParseObject>() {
                                                                    @Override
                                                                    public void done(ParseObject parseObject, ParseException e) {
                                                                        if(e==null)
                                                                        {
                                                                            parseObject.put("channels","");
                                                                            parseObject.saveInBackground();
                                                                        }

                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                    editor.putBoolean("new",false);
                                                    editor.putInt("code",2);
                                                    editor.apply();
                                                }
                                                else
                                                {
                                                    Toast.makeText(ShareYes.this,"Error",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                }
            });

            if (notifs.getBoolean("new", false)) {

                final Dialog notif = new Dialog(this);
                notif.setTitle("New Share Request");
                notif.setContentView(R.layout.notif_receive);
                TextView accept = (TextView)notif.findViewById(R.id.accept);
                TextView decline = (TextView)notif.findViewById(R.id.decline);
                TextView head = (TextView)notif.findViewById(R.id.notifhead);
                head.setText(notifs.getString("alert",""));
                accept.setText("Accept");
                decline.setText("Decline");
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        ParsePush.subscribeInBackground(telephonyManager.getDeviceId(), new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null)
                                {
                                    JSONObject jo = null;
                                    try {
                                        jo = new JSONObject("{\"alert\" : \""+sp.getString("Name","")+" has accepted your share request\"," +
                                                "\"title\" : \"Request accepted\", \"id\" : \""+telephonyManager.getDeviceId()+"\","+
                                                "\"new\" : false, \"code\" : 1}");

                                        ParsePush push = new ParsePush();
                                        push.setQuery(ParseInstallation.getQuery().whereEqualTo("ID", notifs.getString("id", "")));
                                        push.setData(jo);
                                        push.sendInBackground(new SendCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e==null)
                                                {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ShareYes.this,"Member will be added after they have seen the accept notification",Toast.LENGTH_SHORT).show();
                                                    editor.putBoolean("new",false);
                                                    editor.putInt("code", 0);
                                                    editor.apply();
                                                    Intent intent = new Intent(ShareYes.this,MainActivity.class);
                                                    startActivity(intent);
                                                }
                                                else
                                                {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ShareYes.this,"Error. Please try again later",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    catch (JSONException e1) {
                                        progressDialog.dismiss();
                                        e1.printStackTrace();
                                    }
                                }
                                else
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(ShareYes.this,"Error. Please try later",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        notif.dismiss();
                    }
                });
                decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        try{
                            JSONObject jo2 = new JSONObject("{\"alert\" : \""+sp.getString("Name","")+" has declined your share request\"," +
                                    "\"title\" : \"Request declined\", \"id\" : \""+telephonyManager.getDeviceId()+"\","+
                                    "\"new\" : false, \"code\" : 2}");
                            ParsePush push = new ParsePush();
                            push.setQuery(ParseInstallation.getQuery().whereEqualTo("ID", notifs.getString("id", "")));
                            push.setData(jo2);
                            push.sendInBackground(new SendCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e==null)
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(ShareYes.this,"Declined",Toast.LENGTH_SHORT).show();
                                        editor.putBoolean("new",false);
                                        editor.apply();
                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(ShareYes.this,"Error. Please try again later",Toast.LENGTH_SHORT).show();
                                    }
                                    notif.dismiss();
                                }
                            });
                        }
                        catch(JSONException je)
                        {
                            progressDialog.dismiss();
                            je.printStackTrace();
                        }
                    }
                });
                notif.show();
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Quit App?")
                .setPositiveButton("EXIT",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ShareYes.this,MainActivity.class);
                        intent.putExtra("Exit",true);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
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
