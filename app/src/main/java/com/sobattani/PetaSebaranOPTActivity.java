package com.sobattani;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.GPSTracker;
import com.sobattani.Utils.ImageLoader;
import com.sobattani.Utils.Referensi;
import com.sobattani.Utils.TypeFaceSpan;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PetaSebaranOPTActivity extends ActionBarActivity implements OnMarkerClickListener, AdapterView.OnItemSelectedListener {
	
	private GoogleMap mMap;
    private ArrayList<Marker> markers = new ArrayList<>();
    private Double newLatitude, newLongitude;
    private JSONArray str_login  = null;
	private SupportMapFragment fragment;
    private RequestQueue queue;
    private ProgressBar progressBar;
    private GPSTracker gps;
    private LinearLayout linDesc, linShowMyCar, linWhatsApp;
    private TextView txtShowMyCar, txtWhatsApp, txtTitle, txtDetail;
    private Typeface fontUbuntuL, fontUbuntuB;
    private ImageView imgProfile;
    private ImageLoader mImageLoader;
	private RelativeLayout relMaps;
    private Spinner spinHamaPenyakit, spinTanaman;
    private ArrayList<Kategori> tanamanList, hamaPenyakitList;
    private int idTanaman, idHamaPenyakit;
    private Toolbar toolbar;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		setContentView(R.layout.activity_peta_sebaran_opt);

		if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initToolbar();

        mImageLoader     = new ImageLoader(this);
		queue    	     = Volley.newRequestQueue(this);
        fragment 	     = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        mMap 	         = fragment.getMap();
		progressBar      = (ProgressBar) findViewById(R.id.progressBusy);
		linDesc		     = (LinearLayout) findViewById(R.id.linDesc);
		linShowMyCar     = (LinearLayout) findViewById(R.id.linShowMyCar);
		linWhatsApp      = (LinearLayout) findViewById(R.id.linWhatsApp);
		txtShowMyCar     = (TextView) findViewById(R.id.txtShowMyCar);
		txtWhatsApp      = (TextView) findViewById(R.id.txtWhatsApp);
		txtTitle         = (TextView) findViewById(R.id.txtTitle);
		txtDetail	     = (TextView) findViewById(R.id.txtDetail);
        fontUbuntuL      = FontCache.get(this, "DroidSans");
        fontUbuntuB      = FontCache.get(this, "DroidSans-Bold");
        imgProfile       = (ImageView) findViewById(R.id.imgProfile);
		relMaps          = (RelativeLayout) findViewById(R.id.relMaps);
        spinHamaPenyakit = (Spinner) findViewById(R.id.spinHamaPenyakit);
        spinTanaman      = (Spinner) findViewById(R.id.spinTanaman);

		mMap.setOnMarkerClickListener(this);
        spinTanaman.setOnItemSelectedListener(this);
        spinHamaPenyakit.setOnItemSelectedListener(this);
		txtTitle.setTypeface(fontUbuntuB);
        txtDetail.setTypeface(fontUbuntuL);
        txtShowMyCar.setTypeface(fontUbuntuB);
        txtWhatsApp.setTypeface(fontUbuntuB);

        getHamaPenyakitSpinner();
	}

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString(getString(R.string.menu_peta_sebaran_opt));
        spanToolbar.setSpan(new TypeFaceSpan(PetaSebaranOPTActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    public void getTanamanSpinner() {
        String url = Referensi.url+"/getTanaman.php";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray categories = response.getJSONArray("categories");

                    tanamanList = new ArrayList<Kategori>();
                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject catObj = (JSONObject) categories.get(i);
                        Kategori cat = new Kategori(catObj.getInt("TanamanId"), catObj.getString("TanamanName"));
                        tanamanList.add(cat);
                    }
                    populateSpinnerTanaman();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }

    private void populateSpinnerTanaman() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < tanamanList.size(); i++) {
            lables.add(tanamanList.get(i).getName());
        }

        // Creating adapter for spinner
        spinTanaman.setAdapter(null);
        ArrayAdapter<String> spinnerAdapter  = new ArrayAdapter<String>(this, R.layout.spinner_item, lables);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinTanaman.setAdapter(spinnerAdapter);
        initLocationManager();
    }

    public void getHamaPenyakitSpinner() {
        String url = Referensi.url+"/getHamaPenyakit.php";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray categories = response.getJSONArray("categories");

                    hamaPenyakitList = new ArrayList<Kategori>();
                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject catObj = (JSONObject) categories.get(i);
                        Kategori cat = new Kategori(catObj.getInt("HamaPenyakitUmumId"), catObj.getString("HamaPenyakitUmumName"));
                        hamaPenyakitList.add(cat);
                    }
                    populateSpinnerHamaPenyakit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }

    private void populateSpinnerHamaPenyakit() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < hamaPenyakitList.size(); i++) {
            lables.add(hamaPenyakitList.get(i).getName());
        }

        // Creating adapter for spinner
        spinHamaPenyakit.setAdapter(null);
        ArrayAdapter<String> spinnerAdapter  = new ArrayAdapter<String>(this, R.layout.spinner_item, lables);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinHamaPenyakit.setAdapter(spinnerAdapter);
        getTanamanSpinner();
    }
	
	@Override
    public boolean onMarkerClick(final Marker marker) {
    	try {
    		final Handler handler = new Handler();
            final long startTime  = SystemClock.uptimeMillis();
            final long duration   = 2000;
            
            Projection proj = mMap.getProjection();
            final LatLng markerLatLng = marker.getPosition();
            Point startPoint = proj.toScreenLocation(markerLatLng);
            startPoint.offset(0, -100);
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final Interpolator interpolator = new BounceInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - startTime;
                    float t = interpolator.getInterpolation((float) elapsed / duration);
                    double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                    double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                    marker.setPosition(new LatLng(lat, lng));
                    if (t < 1.0) {
                        handler.postDelayed(this, 16);
                    }
                }
            });
    		
	    	linDesc.setVisibility(View.VISIBLE);
	    	mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 13));
	    	
	    	final String[] splitMoreDetail = marker.getTitle().split("\\|");
	    	final String phoneNumber = splitMoreDetail[1];
	    	final String userId      = splitMoreDetail[2];
	    	
	    	if (marker.getTitle()!=null) {
		    	txtTitle.setText(splitMoreDetail[0]);
		    	txtDetail.setText(Html.fromHtml(marker.getSnippet()));
		    	int loader = R.drawable.img_loader;
		        mImageLoader.DisplayImage("https://graph.facebook.com/"+userId+"/picture?type=normal", loader, imgProfile);
	    	} else {
	    		linDesc.setVisibility(View.GONE);
	    	}
	    	
	    	linShowMyCar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					/*String[] splitSnippet = marker.getSnippet().split("<br>");
					startActivity(new Intent(FindFriendLocationActivity.this, ShowMyCarActivity.class).
							putExtra("UserId", userId).
							putExtra("Name", splitMoreDetail[0]).
							putExtra("Description", Description).
							putExtra("Pictures", Pictures).
	            			putExtra("PlatNo", splitSnippet[0].replace("Plat No : ", "")));*/
				}
			});
	    	
	    	linWhatsApp.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (phoneNumber.equalsIgnoreCase("")) {
						Toast.makeText(getBaseContext(), "No. Telp is Required!", Toast.LENGTH_SHORT).show();
					} else {
						try {
							Uri uri = Uri.parse("smsto:"+ "+62"+phoneNumber.substring(1));
		                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
		                    i.setPackage("com.whatsapp");
		                    i.putExtra("sms_body", "The text goes here");
		                    i.putExtra("chat",true);
		                    startActivity(i);
						} catch (ActivityNotFoundException e) {
		                    Toast.makeText(getApplicationContext(), "no whatsapp!", Toast.LENGTH_SHORT).show();
		                }
					}
				}
			});
    	} catch (Exception e) {
			linDesc.setVisibility(View.GONE);
		}
    	return true;
    }
	
	/**
     * Initialize the location manager.
     */
    private void initLocationManager() {
    	// create class object
        gps = new GPSTracker(PetaSebaranOPTActivity.this);

        // check if GPS enabled     
        if (gps.canGetLocation()) {
            newLatitude  = gps.getLatitude();
            newLongitude = gps.getLongitude();
            getAllData();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }
    
    public void getAllData() {
        String url = Referensi.url+"/getAllUser.php";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            	try {
                    str_login = response.getJSONArray("info");

                    for (int i = 0; i < str_login.length(); i++) {
                        JSONObject ar    = str_login.getJSONObject(i);
                        String Longitude = ar.getString("Longitude");
                        String Latitude  = ar.getString("Latitude");
                        initializeMap(Double.parseDouble(Latitude), Double.parseDouble(Longitude), ar);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            	progressBar.setVisibility(View.GONE);
            	relMaps.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	progressBar.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }

    @SuppressLint("NewApi")
	private void initializeMap(double lat1, double lng1, JSONObject ar) {
        String UserName        = ar.optString("Nama");
        String PhoneNo         = ar.optString("NoTelp");
        int UserType           = ar.optInt("UserType");
        String UserTypeName    = ar.optString("UserTypeName");
        String UserId          = ar.optString("UserId");
        String Tanaman         = ar.optString("Tanaman");
        String HamaDanPenyakit = ar.optString("HamaDanPenyakit");

        if (mMap!=null) {
            final LatLng fromPosition = new LatLng(newLatitude, newLongitude);
			@SuppressWarnings("unused")
			final LatLng toPosition   = new LatLng(lat1, lng1);

            Location locationA = new Location("point A");
            locationA.setLatitude(newLatitude);
            locationA.setLongitude(newLongitude);

            Location locationB = new Location("point B");
            locationB.setLatitude(lat1);
            locationB.setLongitude(lng1);

            @SuppressWarnings("unused")
			double distance = locationA.distanceTo(locationB);
          
            // Move the camera instantly to hamburg with a zoom of 15.
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fromPosition, 10));
            
            long lastUpdate     = Long.parseLong(ar.optString("LastUpdate"));
            long remainingDays  = Referensi.getRemainingDays(lastUpdate);
            Date dateLastUpdate = new Date(lastUpdate);
            
            if (remainingDays == 0) {
            	Date dtLastUpdate1 = null;
                try {
                	dtLastUpdate1 = Referensi.getSimpleDateFormatHours().parse(Referensi.getSimpleDateFormatHours().format(dateLastUpdate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long eventMillis1   = dtLastUpdate1.getTime();
                long diffMillis1    = Referensi.getCurrentMillis() - eventMillis1;
                long remainingHours = TimeUnit.MILLISECONDS.toHours(diffMillis1);

                if (remainingHours == 0) {
                	long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis1);
                	if (remainingMinutes <= 10) {
                        /*** UserType Petani ***/
                        if (UserType==1) {
                            markers.add(mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat1, lng1))
                                    .title(UserName + "|" + PhoneNo + "|" + UserId + "|" + HamaDanPenyakit+"|"+Tanaman)
                                    .snippet("Kategori User : " + UserTypeName + "<br>" + "Last Update : " + remainingMinutes + " minutes")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.petani_on))));
                        }
                	} else {
                        /*** UserType Petani ***/
                        if (UserType==1) {
                            markers.add(mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat1, lng1))
                                    .title(UserName+"|"+PhoneNo+"|"+UserId+"|" + HamaDanPenyakit+"|"+Tanaman)
                                    .snippet("Kategori User : " + UserTypeName + "<br>" + "Last Update : " + remainingMinutes + " minutes")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.petani_idle))));
                        }
                	}
                } else {
                    /*** UserType Petani ***/
                    if (UserType==1) {
                        markers.add(mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat1, lng1))
                                .title(UserName + "|" + PhoneNo + "|" + UserId + "|" + HamaDanPenyakit+"|"+Tanaman)
                                .snippet("Kategori User : " + UserTypeName + "<br>" + "Last Update : " + remainingHours + " hours")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.petani_off))));
                    }
                }
            } else {
                /*** UserType Petani ***/
                if (UserType==1) {
                    markers.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat1, lng1))
                            .title(UserName + "|" + PhoneNo + "|" + UserId + "|" + HamaDanPenyakit+"|"+Tanaman)
                            .snippet("Kategori User : " + UserTypeName + "<br>" + "Last Update : " + remainingDays + " days")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.petani_off))));
                }
            }
        } else {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.container)).getMap();
            Toast.makeText(getApplicationContext(), "MAP NULL", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);

        switch (parent.getId()) {
            case R.id.spinTanaman:
                idTanaman = tanamanList.get(position).getId();

                for (int i=0; i<markers.size(); i++) {
                    final String[] splitMoreDetail = markers.get(i).getTitle().split("\\|");
                    if (Integer.parseInt(splitMoreDetail[4])==idTanaman && Integer.parseInt(splitMoreDetail[3])==idHamaPenyakit) {
                        markers.get(i).setVisible(true);
                    } else {
                        markers.get(i).setVisible(false);
                    }
                }

                break;
            case R.id.spinHamaPenyakit:
                idHamaPenyakit = hamaPenyakitList.get(position).getId();

                for (int i=0; i<markers.size(); i++) {
                    final String[] splitMoreDetail = markers.get(i).getTitle().split("\\|");
                    if (Integer.parseInt(splitMoreDetail[4])==idTanaman && Integer.parseInt(splitMoreDetail[3])==idHamaPenyakit) {
                        markers.get(i).setVisible(true);
                    } else {
                        markers.get(i).setVisible(false);
                    }
                }

                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

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
