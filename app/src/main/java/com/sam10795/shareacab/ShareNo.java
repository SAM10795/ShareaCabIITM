package com.sam10795.shareacab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.sam10795.shareacab.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by SAM10795 on 25-07-2015.
 */
public class ShareNo extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks
{
    String f,t,date,time;
    TextView dv,tv;
    private static final String LOG_TAG = "ShareNo";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView fromAC;
    private AutoCompleteTextView toAC;
    Place from;
    Place to;
    private GoogleApiClient mGoogleApiClient;
    private PlacesAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
             new LatLng(0, 60),new LatLng(40, 100));
    boolean fr,tt;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_no);

        fr = false;
        tt = false;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        fromAC = (AutoCompleteTextView) findViewById(R.id
                .acfrom);
        fromAC.setThreshold(3);
        fromAC.setOnItemClickListener(mAutocompleteClickListener);
        fromAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fr = true;
                tt = false;
            }
        });
        mPlaceArrayAdapter = new PlacesAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        fromAC.setAdapter(mPlaceArrayAdapter);
        toAC = (AutoCompleteTextView)findViewById(R.id.acto);
        toAC.setThreshold(3);
        toAC.setOnItemClickListener(mAutocompleteClickListener);
        toAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fr = false;
                tt = true;
            }
        });
        toAC.setAdapter(mPlaceArrayAdapter);


        Button button = (Button)findViewById(R.id.button);


        /*ft = new ArrayList<>(3);
        ft.add("Insti");
        ft.add("Chennai Airport");
        ft.add("Chennai Railway Station");
        ft2 = new ArrayList<>();

        ArrayAdapter<String> places = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,ft);


        from = (Spinner)findViewById(R.id.From);
        to = (Spinner)findViewById(R.id.To);

        from.setAdapter(places);
        to.setAdapter(places);


        from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                f = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                t = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });*/

        dv = (TextView)findViewById(R.id.date);
        tv = (TextView)findViewById(R.id.time);

        dv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cur = Calendar.getInstance();
                int yr = cur.get(Calendar.YEAR);
                int month = cur.get(Calendar.MONTH);
                int day = cur.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ShareNo.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dv.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                        date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
                    }
                },yr,month,day);
                datePickerDialog.show();
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cur = Calendar.getInstance();
                int hr = cur.get(Calendar.HOUR_OF_DAY);
                int min = cur.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(ShareNo.this,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tv.setText(hourOfDay+":"+minute);
                        time = hourOfDay+":"+minute;
                    }
                },hr,min,true);
                timePickerDialog.show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vdt()&&vft())
                {
                    Intent intent = new Intent(ShareNo.this, ShareView.class);
                    intent.putExtra("FromStr", from.getName());
                    intent.putExtra("ToStr", to.getName());
                    intent.putExtra("FromLat",from.getLatLng().latitude);
                    intent.putExtra("FromLon",from.getLatLng().longitude);
                    intent.putExtra("ToLat",to.getLatLng().latitude);
                    intent.putExtra("ToLon",to.getLatLng().longitude);
                    intent.putExtra("DateTime", date + " " + time);
                    startActivity(intent);
                }
            }
        });

    }

    public boolean vdt()
    {
        DateFormat d1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date d2 = d1.parse(date+" "+time);
            if(d2.after(new Date()))
            {
                return true;
            }
            else
            {
                Toast.makeText(this,"Cannot select past date and time",Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error in Date format. Please retry",Toast.LENGTH_SHORT).show();
            Log.e("Date", "Error2");
            return false;
        }
    }

    public boolean vft()
    {
        if(from!=null&&to!=null)
        {
        if(from.getAddress().equals(to.getAddress()))
        {
            Toast.makeText(this,"Starting point and destination cannot be the same place",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            return true;
        }}
        else
        {
            Toast.makeText(this,"Starting point or destination have not been entered",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Quit App?")
                .setPositiveButton("EXIT",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ShareNo.this,MainActivity.class);
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

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlacesAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            if(fromAC.isFocused()) {
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        Log.i("Place","From");
                        from = places.get(0);
                    }
                });
            }
            if(toAC.isFocused())
            {
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        Log.i("Place","To");
                        to = places.get(0);
                    }
                });
            }
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    /*private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            Log.i("Place","InBuffer");
            Place place = places.get(0);
            if(fromAC.isFocused())
            {
                from = place;
                Log.i("Place","From");
            }
            else if(toAC.isFocused())
            {
                to = place;
                Log.i("Place","To");
            }
        }
    };*/

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }
}
