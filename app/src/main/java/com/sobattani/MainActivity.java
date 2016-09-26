package com.sobattani;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.etsy.android.grid.StaggeredGridView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.onesignal.OneSignal;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.GPSTracker;
import com.sobattani.Utils.JSONParser;
import com.sobattani.Utils.Referensi;
import com.sobattani.Utils.callURL;
import com.sobattani.adapter.MenuAdapter;
import com.sobattani.app.Config;
import com.sobattani.gcm.GcmIntentService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {
    private StaggeredGridView itemList;
    private MenuAdapter menuAdapter;
    private String url="", url1="", url2="";
    double Latitude, Longitude;
    private long timeInMilliseconds;
    private JSONArray str_login = null;
    private boolean isMember=false;
    private String UserId="", UserName="", Email="";
    private SharedPreferences sobatTaniPref;
    private GPSTracker gps;
    private ProgressBar progressBar;
    private Typeface fontDroidSandBold;
    private String token;

    private String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                String text = "OneSignal UserID:\n" + userId + "\n\n";

                if (registrationId != null)
                    text += "Google Registration Id:\n" + registrationId;
                else
                    text += "Google Registration Id:\nCould not subscribe for push";
            }
        });

        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        TextView mTitle    = (TextView) toolbarTop.findViewById(R.id.lblTitle);
        sobatTaniPref      = getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
        itemList           = (StaggeredGridView) findViewById(R.id.itemList);
        progressBar        = (ProgressBar) findViewById(R.id.progressBusy);
        UserId             = sobatTaniPref.getString("UserId", "");
        UserName           = sobatTaniPref.getString("UserName", "");
        Email              = sobatTaniPref.getString("Email", "");
        fontDroidSandBold  = FontCache.get(this, "DroidSans-Bold");

        mTitle.setTypeface(fontDroidSandBold);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        Date mDate;
        try {
            mDate = df.parse(formattedDate);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    token = intent.getStringExtra("token");
                    initLocationManager();
                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                }
            }
        };

        if (checkPlayServices()) {
            registerGCM();
        }
    }

    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Initialize the location manager.
     */
    private void initLocationManager() {
        // create class object
        gps = new GPSTracker(MainActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {
            Latitude  = gps.getLatitude();
            Longitude = gps.getLongitude();
            new cekDataUserIfAvailable().execute();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    private class cekDataUserIfAvailable extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            url = Referensi.url+"/getUser.php?UserId="+UserId;
            String hasil = callURL.call(url);
            return hasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equalsIgnoreCase("false")) {
                new addNewUser().execute();
            } else {
                new updateUserLocation().execute();
            }
        }
    }

    private class updateUserLocation extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            url1 = Referensi.url+"/service.php?ct=UPDATELOCATION&Email="+Email+
                    "&Latitude="+Latitude+
                    "&Longitude="+Longitude+
                    "&LastUpdate="+timeInMilliseconds+
                    "&gcm_registration_id="+token+
                    "&UserId="+UserId;
            String hasil = callURL.call(url1);
            checkIfAMember();
            return hasil;
        }
        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            itemList.setVisibility(View.VISIBLE);
            menuAdapter = new MenuAdapter(MainActivity.this, isMember);
            itemList.setAdapter(menuAdapter);
        }
    }

    private class addNewUser extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            String Nama = null;
            try {
                Nama = URLEncoder.encode(UserName, "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            url2 = Referensi.url+"/service.php?ct=ADDNEWUSER&UserId="+UserId+"&Nama="+Nama;
            String hasil = callURL.call(url2);
            return hasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new updateUserLocation().execute();
        }
    }

    private void checkIfAMember() {
        JSONParser jParser = new JSONParser();
        String link_url = Referensi.url+"/getUserDetail.php?UserId="+UserId;
        JSONObject json = jParser.AmbilJson(link_url);

        try {
            str_login = json.getJSONArray("info");

            for (int i = 0; i < str_login.length(); i++){
                JSONObject ar = str_login.getJSONObject(0);

                if (ar.getString("UserType").equalsIgnoreCase("")) {
                    isMember=false;
                } else {
                    isMember=true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        if (sobatTaniPref.getString("isAMember", "").equalsIgnoreCase("true")) {
            itemList.setVisibility(View.GONE);
            SharedPreferences.Editor editor = sobatTaniPref.edit();
            editor.putString("isAMember", "false");
            editor.commit();
            initLocationManager();
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
