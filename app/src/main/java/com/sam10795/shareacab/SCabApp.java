package com.sam10795.shareacab;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by SAM10795 on 29-07-2015.
 */
public class SCabApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseCrashReporting.enable(this);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "IDb0Bi3U9UolZIzP0cbzOxjHtF1EKhXju0filPaf", "N2ESgnhsRGxlXWweOHqxKOF5ymwizmNeJNqtmVuQ");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        defaultACL.setPublicWriteAccess(true);
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
