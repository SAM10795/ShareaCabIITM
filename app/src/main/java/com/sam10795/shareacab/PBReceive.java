package com.sam10795.shareacab;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SAM10795 on 09-08-2015.
 */
public class PBReceive extends ParsePushBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i("Push","Received");
        Toast.makeText(context,"Received",Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = context.getSharedPreferences("Notif",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String JsonData = intent.getExtras().getString(ParsePushBroadcastReceiver.KEY_PUSH_DATA);
        try {
            JSONObject jo = new JSONObject(JsonData);
            editor.putString("alert",jo.getString("alert"));
            editor.putBoolean("new",jo.getBoolean("new"));
            editor.putString("id",jo.getString("id"));
            editor.putInt("code",jo.getInt("code"));
            editor.apply();
            if(jo.getInt("code")==3)
            {
                ParsePush.unsubscribeInBackground("C"+jo.getString("id"), new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null) {
                            editor.putInt("code", 2);
                            editor.apply();
                        }
                        else
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context,intent);
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        Log.i("Push","Received");
        Toast.makeText(context,"Dismissed",Toast.LENGTH_SHORT).show();
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        Log.i("Push","Received");
        Toast.makeText(context,"Opened",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(context,ShareYes.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
