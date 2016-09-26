package com.sobattani;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.GPSTracker;
import com.sobattani.Utils.Referensi;
import com.sobattani.Utils.TypeFaceSpan;
import com.sobattani.Weather.JSONWeatherParser;
import com.sobattani.Weather.Weather;
import com.sobattani.Weather.WeatherHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CuacaActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private Typeface fontDroidSandBold, fontDroidSand;
    private GPSTracker gps;
    private Double newLatitude, newLongitude;
    private TextView txtHariIni, txtEsokHari;
    private TextView cityText, lblKeterangan, txtKeterangan, lblKeterangann, txtKeterangann;
    private TextView condDescr, condDescrr;
    private TextView temp, tempp;
    private TextView press, presss, pressLab, pressLabb;
    private TextView windSpeed, windSpeedd, windLab, windLabb;
    private TextView windDeg, windDegg, lblWindDeg, lblWindDegg;
    private TextView hum, humm, humLab, humLabb;
    private ImageView imgView, imgIconToday, imgIconTomorrow;
    private LinearLayout linDetail;
    private RequestQueue queue;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_cuaca);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initToolbar();

        queue    	       = Volley.newRequestQueue(this);
        progressBar        = (ProgressBar) findViewById(R.id.progressBusy);
        fontDroidSandBold  = FontCache.get(this, "DroidSans-Bold");
        fontDroidSand      = FontCache.get(this, "DroidSans");
        txtHariIni         = (TextView) findViewById(R.id.txtHariIni);
        txtEsokHari        = (TextView) findViewById(R.id.txtEsokHari);
        cityText           = (TextView) findViewById(R.id.cityText);
        condDescr          = (TextView) findViewById(R.id.condDescr);
        condDescrr         = (TextView) findViewById(R.id.condDescrr);
        temp               = (TextView) findViewById(R.id.temp);
        tempp              = (TextView) findViewById(R.id.tempp);
        hum                = (TextView) findViewById(R.id.hum);
        humm               = (TextView) findViewById(R.id.humm);
        humLab             = (TextView) findViewById(R.id.humLab);
        humLabb            = (TextView) findViewById(R.id.humLabb);
        press              = (TextView) findViewById(R.id.press);
        presss             = (TextView) findViewById(R.id.presss);
        pressLab           = (TextView) findViewById(R.id.pressLab);
        pressLabb          = (TextView) findViewById(R.id.pressLabb);
        windSpeed          = (TextView) findViewById(R.id.windSpeed);
        windSpeedd         = (TextView) findViewById(R.id.windSpeedd);
        windDeg            = (TextView) findViewById(R.id.windDeg);
        windDegg           = (TextView) findViewById(R.id.windDegg);
        lblWindDeg         = (TextView) findViewById(R.id.lblWindDeg);
        lblWindDegg        = (TextView) findViewById(R.id.lblWindDegg);
        windLab            = (TextView) findViewById(R.id.windLab);
        windLabb           = (TextView) findViewById(R.id.windLabb);
        imgView            = (ImageView) findViewById(R.id.condIcon);
        linDetail          = (LinearLayout) findViewById(R.id.linDetail);
        imgIconToday       = (ImageView) findViewById(R.id.imgIconToday);
        imgIconTomorrow    = (ImageView) findViewById(R.id.imgIconTomorrow);
        lblKeterangan      = (TextView) findViewById(R.id.lblKeterangan);
        txtKeterangan      = (TextView) findViewById(R.id.txtKeterangan);
        lblKeterangann     = (TextView) findViewById(R.id.lblKeterangann);
        txtKeterangann     = (TextView) findViewById(R.id.txtKeterangann);

        txtHariIni.setTypeface(fontDroidSand);
        txtEsokHari.setTypeface(fontDroidSand);
        cityText.setTypeface(fontDroidSandBold);
        condDescr.setTypeface(fontDroidSand);
        condDescrr.setTypeface(fontDroidSand);
        temp.setTypeface(fontDroidSandBold);
        tempp.setTypeface(fontDroidSandBold);
        pressLab.setTypeface(fontDroidSand);
        pressLabb.setTypeface(fontDroidSand);
        press.setTypeface(fontDroidSandBold);
        presss.setTypeface(fontDroidSandBold);
        humLab.setTypeface(fontDroidSand);
        humLabb.setTypeface(fontDroidSand);
        hum.setTypeface(fontDroidSandBold);
        humm.setTypeface(fontDroidSandBold);
        windLab.setTypeface(fontDroidSand);
        windLabb.setTypeface(fontDroidSand);
        windSpeed.setTypeface(fontDroidSandBold);
        windSpeedd.setTypeface(fontDroidSandBold);
        lblWindDeg.setTypeface(fontDroidSand);
        lblWindDegg.setTypeface(fontDroidSand);
        windDeg.setTypeface(fontDroidSandBold);
        windDegg.setTypeface(fontDroidSandBold);
        lblKeterangan.setTypeface(fontDroidSand);
        txtKeterangan.setTypeface(fontDroidSandBold);
        lblKeterangann.setTypeface(fontDroidSand);
        txtKeterangann.setTypeface(fontDroidSandBold);

        initLocationManager();
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString(getString(R.string.menu_informasi_cuaca));
        spanToolbar.setSpan(new TypeFaceSpan(CuacaActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //Initiate Toolbar/ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(spanToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Initialize the location manager.
     */
    private void initLocationManager() {
        // create class object
        gps = new GPSTracker(CuacaActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {
            newLatitude  = gps.getLatitude();
            newLongitude = gps.getLongitude();
            getForecast();
            //JSONWeatherTask task = new JSONWeatherTask();
            //task.execute(new String[]{""+newLatitude, ""+newLongitude});
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    public void getForecast() {
        String url = "http://api.openweathermap.org/data/2.5/forecast?lat="+newLatitude+"&lon="+newLongitude+"&appid=19212741f45ef3673e52e16608b24c61";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.getString("city"));
                    cityText.setText(jsonObject.getString("name") + " (" + jsonObject.getString("country") + ")");

                    Calendar calendar = Calendar.getInstance();

                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    Date today = calendar.getTime();

                    calendar.add(Calendar.DAY_OF_YEAR, 2);
                    Date tomorrow = calendar.getTime();

                    DateFormat dateFormat   = new SimpleDateFormat("dd/MM/yyyy");
                    String todayAsString    = dateFormat.format(today);
                    String tomorrowAsString = dateFormat.format(tomorrow);

                    JSONArray jsonArray = new JSONArray(response.getString("list"));
                    for (int i=0; i<jsonArray.length(); i++) {
                        String date = Referensi.getDate(jsonArray.getJSONObject(i).getLong("dt")*1000);

                        JSONObject jsonObjectSuhu = new JSONObject(jsonArray.getJSONObject(i).getString("main"));
                        JSONObject jsonObjectWind = new JSONObject(jsonArray.getJSONObject(i).getString("wind"));
                        JSONArray jArrWeather     = new JSONArray(jsonArray.getJSONObject(i).getString("weather"));
                        if (date.equalsIgnoreCase(todayAsString)) {

                            String strIcon = jArrWeather.getJSONObject(0).optString("icon");
                            if (strIcon.equalsIgnoreCase("01d")) {
                                // clear sky
                                imgIconToday.setImageResource(R.drawable.sunny);
                                txtKeterangan.setText("Langit Cerah");
                            } else if (strIcon.equalsIgnoreCase("01n")) {
                                // clear sky
                                imgIconToday.setImageResource(R.drawable.sunny_night);
                                txtKeterangan.setText("Langit Cerah");
                            } else if (strIcon.equalsIgnoreCase("02d")) {
                                // few clouds
                                imgIconToday.setImageResource(R.drawable.cloudy);
                                txtKeterangan.setText("Berawan");
                            } else if (strIcon.equalsIgnoreCase("02n")) {
                                // few clouds
                                imgIconToday.setImageResource(R.drawable.cloudy_night);
                                txtKeterangan.setText("Berawan");
                            } else if (strIcon.equalsIgnoreCase("03d")||strIcon.equalsIgnoreCase("03n")) {
                                // scattered clouds
                                imgIconToday.setImageResource(R.drawable.cloudys);
                                txtKeterangan.setText("Cerah");
                            } else if (strIcon.equalsIgnoreCase("04d")) {
                                // broken clouds
                                imgIconToday.setImageResource(R.drawable.mist);
                                txtKeterangan.setText("Awan Kabut");
                            } else if (strIcon.equalsIgnoreCase("04n")) {
                                // broken clouds
                                imgIconToday.setImageResource(R.drawable.mist_night);
                                txtKeterangan.setText("Awan Kabut");
                            } else if (strIcon.equalsIgnoreCase("09d")) {
                                // shower rain
                                imgIconToday.setImageResource(R.drawable.shower);
                                txtKeterangan.setText("Gerimis");
                            } else if (strIcon.equalsIgnoreCase("09n")) {
                                // shower rain
                                imgIconToday.setImageResource(R.drawable.shower_night);
                                txtKeterangan.setText("Gerimis");
                            } else if (strIcon.equalsIgnoreCase("10d")) {
                                // rain
                                imgIconToday.setImageResource(R.drawable.showerr);
                                txtKeterangan.setText("Hujan");
                            } else if (strIcon.equalsIgnoreCase("10n")) {
                                // rain
                                imgIconToday.setImageResource(R.drawable.showerr_night);
                                txtKeterangan.setText("Hujan");
                            } else if (strIcon.equalsIgnoreCase("11d")) {
                                // thunderstorm
                                imgIconToday.setImageResource(R.drawable.tstorm);
                                txtKeterangan.setText("Hujan Badai");
                            } else if (strIcon.equalsIgnoreCase("11n")) {
                                // thunderstorm
                                imgIconToday.setImageResource(R.drawable.tstorm_night);
                                txtKeterangan.setText("Hujan Badai");
                            } else if (strIcon.equalsIgnoreCase("13d")) {
                                // snow
                                imgIconToday.setImageResource(R.drawable.snow);
                                txtKeterangan.setText("Salju");
                            } else if (strIcon.equalsIgnoreCase("13n")) {
                                // snow
                                imgIconToday.setImageResource(R.drawable.snow_night);
                                txtKeterangan.setText("Salju");
                            } else {
                                imgIconToday.setImageResource(R.drawable.overcast);
                                txtKeterangan.setText("Kabut");
                            }

                            if (temp.getText().toString().equalsIgnoreCase("")) {
                                temp.setText(Math.round((jsonObjectSuhu.getInt("temp")-273.15)) + " C");
                            }
                            if (hum.getText().toString().equalsIgnoreCase("")) {
                                hum.setText(jsonObjectSuhu.getString("humidity") + "%");
                            }
                            if (press.getText().toString().equalsIgnoreCase("")) {
                                press.setText(jsonObjectSuhu.getString("pressure") + " hPa");
                            }
                            if (windSpeed.getText().toString().equalsIgnoreCase("")) {
                                windSpeed.setText(jsonObjectWind.getString("speed") + " mps");
                            }
                        } else if (date.equalsIgnoreCase(tomorrowAsString)) {

                            String strIcon = jArrWeather.getJSONObject(0).optString("icon");
                            if (strIcon.equalsIgnoreCase("01d")) {
                                // clear sky
                                imgIconTomorrow.setImageResource(R.drawable.sunny);
                                txtKeterangann.setText("Langit Cerah");
                            } else if (strIcon.equalsIgnoreCase("01n")) {
                                // clear sky
                                imgIconTomorrow.setImageResource(R.drawable.sunny_night);
                                txtKeterangann.setText("Langit Cerah");
                            } else if (strIcon.equalsIgnoreCase("02d")) {
                                // few clouds
                                imgIconTomorrow.setImageResource(R.drawable.cloudy);
                                txtKeterangann.setText("Berawan");
                            } else if (strIcon.equalsIgnoreCase("02n")) {
                                // few clouds
                                imgIconTomorrow.setImageResource(R.drawable.cloudy_night);
                                txtKeterangann.setText("Berawan");
                            } else if (strIcon.equalsIgnoreCase("03d")||strIcon.equalsIgnoreCase("03n")) {
                                // scattered clouds
                                imgIconTomorrow.setImageResource(R.drawable.cloudys);
                                txtKeterangann.setText("Cerah");
                            } else if (strIcon.equalsIgnoreCase("04d")) {
                                // broken clouds
                                imgIconTomorrow.setImageResource(R.drawable.mist);
                                txtKeterangann.setText("Awan Kabut");
                            } else if (strIcon.equalsIgnoreCase("04n")) {
                                // broken clouds
                                imgIconTomorrow.setImageResource(R.drawable.mist_night);
                                txtKeterangann.setText("Awan Kabut");
                            } else if (strIcon.equalsIgnoreCase("09d")) {
                                // shower rain
                                imgIconTomorrow.setImageResource(R.drawable.shower);
                                txtKeterangann.setText("Gerimis");
                            } else if (strIcon.equalsIgnoreCase("09n")) {
                                // shower rain
                                imgIconTomorrow.setImageResource(R.drawable.shower_night);
                                txtKeterangann.setText("Gerimis");
                            } else if (strIcon.equalsIgnoreCase("10d")) {
                                // rain
                                imgIconTomorrow.setImageResource(R.drawable.showerr);
                                txtKeterangann.setText("Hujan");
                            } else if (strIcon.equalsIgnoreCase("10n")) {
                                // rain
                                imgIconTomorrow.setImageResource(R.drawable.showerr_night);
                                txtKeterangann.setText("Hujan");
                            } else if (strIcon.equalsIgnoreCase("11d")) {
                                // thunderstorm
                                imgIconTomorrow.setImageResource(R.drawable.tstorm);
                                txtKeterangann.setText("Hujan Badai");
                            } else if (strIcon.equalsIgnoreCase("11n")) {
                                // thunderstorm
                                imgIconTomorrow.setImageResource(R.drawable.tstorm_night);
                                txtKeterangann.setText("Hujan Badai");
                            } else if (strIcon.equalsIgnoreCase("13d")) {
                                // snow
                                imgIconTomorrow.setImageResource(R.drawable.snow);
                                txtKeterangann.setText("Salju");
                            } else if (strIcon.equalsIgnoreCase("13n")) {
                                // snow
                                imgIconTomorrow.setImageResource(R.drawable.snow_night);
                                txtKeterangann.setText("Salju");
                            } else {
                                imgIconTomorrow.setImageResource(R.drawable.overcast);
                                txtKeterangann.setText("Kabut");
                            }

                            if (tempp.getText().toString().equalsIgnoreCase("")) {
                                tempp.setText(Math.round((jsonObjectSuhu.getInt("temp")-273.15)) + " C");
                            }
                            if (humm.getText().toString().equalsIgnoreCase("")) {
                                humm.setText(jsonObjectSuhu.getString("humidity") + "%");
                            }
                            if (presss.getText().toString().equalsIgnoreCase("")) {
                                presss.setText(jsonObjectSuhu.getString("pressure") + " hPa");
                            }
                            if (windSpeedd.getText().toString().equalsIgnoreCase("")) {
                                windSpeedd.setText(jsonObjectWind.getString("speed") + " mps");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressBar.setVisibility(View.GONE);
                linDetail.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {
        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ( (new WeatherHttpClient()).getWeatherData(params[0], params[1]));

            try {
                weather = JSONWeatherParser.getWeather(data);
                // Let's retrieve the icon
                weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgView.setImageBitmap(img);
            }

            cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
            condDescr.setText(weather.currentCondition.getCondition() + " (" + weather.currentCondition.getDescr() + ")");
            temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + " C");
            hum.setText("" + weather.currentCondition.getHumidity() + "%");
            press.setText("" + weather.currentCondition.getPressure() + " hPa");
            windSpeed.setText("" + weather.wind.getSpeed() + " mps");
            windDeg.setText("" + weather.wind.getDeg() + "");

            progressBar.setVisibility(View.GONE);
            linDetail.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return true;
    }

}
