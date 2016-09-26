package com.sobattani;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.GPSTracker;
import com.sobattani.Utils.GoogleMapV2Direction;
import com.sobattani.Utils.Referensi;
import com.sobattani.Utils.TypeFaceSpan;

public class FindFriendLocationActivity extends ActionBarActivity implements OnMarkerClickListener {
	private GoogleMap mMap, mMapTwo;
    private ArrayList<Marker> markers = new ArrayList<>();
    private Double newLatitude, newLongitude;
    private JSONArray str_login  = null;
	private SupportMapFragment fragment, fragmentTwo;
    private GoogleMapV2Direction md;
    private RequestQueue queue;
    private ProgressBar progressBar;
    private GPSTracker gps;
    private LinearLayout linDesc, linRute, linCall, linWhatsApp, linFilter, linShowProfile;
    private TextView txtRute, txtCall, txtWhatsApp, txtTitle, txtDetail, txtShowProfile;
    private Typeface fontUbuntuL, fontUbuntuB;
    private ImageView imgProfile;
	private RelativeLayout relMaps;
    private CheckBox chkPetani, chkAgronomis, chkPPL, chkTokoPertanian, chkUmum;
    private LatLng fromPosition;
    private Bitmap bitmapPetani, bitmapAgricon, bitmapBasf, bitmapBayer, bitmapDow, bitmapDupont, bitmapEastWestSeed,
            bitmapMonsanto, bitmapNufarm, bitmapPetrokimiaKayaku, bitmapPetrosidaGersik, bitmapRoyalAgro,
            bitmapSyngenta, bitmapYara, bitmapKamentan;
    private Toolbar toolbar;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		setContentView(R.layout.activity_find_friend_location);

		if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initToolbar();

		queue    	     = Volley.newRequestQueue(this);
        fragment 	     = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        mMap 	         = fragment.getMap();
		progressBar      = (ProgressBar) findViewById(R.id.progressBusy);
		linDesc		     = (LinearLayout) findViewById(R.id.linDesc);
        linRute          = (LinearLayout) findViewById(R.id.linRute);
        linCall          = (LinearLayout) findViewById(R.id.linCall);
		linWhatsApp      = (LinearLayout) findViewById(R.id.linWhatsApp);
        txtRute          = (TextView) findViewById(R.id.txtRute);
        txtCall          = (TextView) findViewById(R.id.txtCall);
		txtWhatsApp      = (TextView) findViewById(R.id.txtWhatsApp);
		txtTitle         = (TextView) findViewById(R.id.txtTitle);
		txtDetail	     = (TextView) findViewById(R.id.txtDetail);
        fontUbuntuL      = FontCache.get(this, "DroidSans");
        fontUbuntuB      = FontCache.get(this, "DroidSans-Bold");
        imgProfile       = (ImageView) findViewById(R.id.imgProfile);
		relMaps          = (RelativeLayout) findViewById(R.id.relMaps);
        chkPetani        = (CheckBox) findViewById(R.id.chkPetani);
        chkAgronomis     = (CheckBox) findViewById(R.id.chkAgronomis);
        chkPPL           = (CheckBox) findViewById(R.id.chkPPL);
        chkTokoPertanian = (CheckBox) findViewById(R.id.chkTokoPertanian);
        chkUmum          = (CheckBox) findViewById(R.id.chkUmum);
        linFilter        = (LinearLayout) findViewById(R.id.linFilter);
        linShowProfile   = (LinearLayout) findViewById(R.id.linShowProfile);
        txtShowProfile   = (TextView) findViewById(R.id.txtShowProfile);

		mMap.setOnMarkerClickListener(this);
		txtTitle.setTypeface(fontUbuntuB);
        txtDetail.setTypeface(fontUbuntuL);
        txtRute.setTypeface(fontUbuntuB);
        txtCall.setTypeface(fontUbuntuB);
        txtWhatsApp.setTypeface(fontUbuntuB);
        chkPetani.setTypeface(fontUbuntuL);
        chkAgronomis.setTypeface(fontUbuntuL);
        chkPPL.setTypeface(fontUbuntuL);
        chkTokoPertanian.setTypeface(fontUbuntuL);
        chkUmum.setTypeface(fontUbuntuL);
        txtShowProfile.setTypeface(fontUbuntuB);

        chkPetani.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i = 0; i < markers.size(); i++) {
                    if (markers.get(i).getTitle().contains("#1")) {
                        if (isChecked) {
                            markers.get(i).setVisible(true);
                        } else {
                            markers.get(i).setVisible(false);
                        }
                    }
                }
            }
        });

        chkTokoPertanian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i=0; i<markers.size(); i++) {
                    if (markers.get(i).getTitle().contains("#2")) {
                        if (isChecked) {
                            markers.get(i).setVisible(true);
                        } else {
                            markers.get(i).setVisible(false);
                        }
                    }
                }
            }
        });

        chkAgronomis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i=0; i<markers.size(); i++) {
                    if (markers.get(i).getTitle().contains("#3")) {
                        if (isChecked) {
                            markers.get(i).setVisible(true);
                        } else {
                            markers.get(i).setVisible(false);
                        }
                    }
                }
            }
        });

        chkPPL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i=0; i<markers.size(); i++) {
                    if (markers.get(i).getTitle().contains("#4")) {
                        if (isChecked) {
                            markers.get(i).setVisible(true);
                        } else {
                            markers.get(i).setVisible(false);
                        }
                    }
                }
            }
        });

        chkUmum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i=0; i<markers.size(); i++) {
                    if (markers.get(i).getTitle().contains("#5")) {
                        if (isChecked) {
                            markers.get(i).setVisible(true);
                        } else {
                            markers.get(i).setVisible(false);
                        }
                    }
                }
            }
        });

        initLocationManager();
	}

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString(getString(R.string.menu_cari_sobat));
        spanToolbar.setSpan(new TypeFaceSpan(FindFriendLocationActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //Initiate Toolbar/ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(spanToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressedAction();
            }
        });
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
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(marker.getPosition(), 13, 90, 0));
            mMap.animateCamera(cameraUpdate);
            mMap.getUiSettings().setCompassEnabled(false);
	    	
	    	final String[] splitMoreDetail = marker.getTitle().split("\\|");
            final String strName     = splitMoreDetail[0];
	    	final String phoneNumber = splitMoreDetail[1];
	    	final String userId      = splitMoreDetail[2];
            final String strKategori = splitMoreDetail[3];
            final String strDetail   = splitMoreDetail[4];
            final String strPictures = splitMoreDetail[5];
            final String strGcmId    = splitMoreDetail[6];
            final String userType    = splitMoreDetail[7].replace("#", "");
	    	
	    	if (marker.getTitle()!=null) {
		    	txtTitle.setText(splitMoreDetail[0]);
		    	txtDetail.setText(Html.fromHtml(marker.getSnippet()));
		    	Glide.with(FindFriendLocationActivity.this).load("https://graph.facebook.com/"+userId+"/picture?type=normal")
                        .placeholder(R.drawable.img_loader).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(200, 200)
                        .dontAnimate().into(imgProfile);
	    	} else {
	    		linDesc.setVisibility(View.GONE);
	    	}

            linRute.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    linDesc.setVisibility(View.GONE);
                    linFilter.setVisibility(View.GONE);
                    openMap(fromPosition, marker.getPosition(), splitMoreDetail[0], Integer.parseInt(userType));
                }
            });

            linCall.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
                    if (phoneNumber.equalsIgnoreCase("")) {
                        Toast.makeText(getBaseContext(), "No. Telp is Required!", Toast.LENGTH_SHORT).show();
                    } else {
                        String uri = "tel:" + phoneNumber.replace("-","").replace(" ","");
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }
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

            linShowProfile.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FindFriendLocationActivity.this, ShowProfileActivity.class);
                    intent.putExtra("UserId", userId);
                    startActivity(intent);
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
        gps = new GPSTracker(FindFriendLocationActivity.this);

        // check if GPS enabled     
        if (gps.canGetLocation()) {
            newLatitude  = gps.getLatitude();
            newLongitude = gps.getLongitude();
            new generateIcon().execute();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    private class generateIcon extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            int height = 100;
            int width  = 80;
            BitmapDrawable bitmapdrawPetani = (BitmapDrawable)getResources().getDrawable(R.drawable.petani);
            Bitmap bPetani = bitmapdrawPetani.getBitmap();
            bitmapPetani = Bitmap.createScaledBitmap(bPetani, width, height, false);

            // Agricon
            BitmapDrawable bitmapdrawAgricon = (BitmapDrawable)getResources().getDrawable(R.drawable.agricon);
            Bitmap bAgricon = bitmapdrawAgricon.getBitmap();
            bitmapAgricon = Bitmap.createScaledBitmap(bAgricon, width, height, false);

            // BASF
            BitmapDrawable bitmapdrawBasf = (BitmapDrawable)getResources().getDrawable(R.drawable.basf);
            Bitmap bBasf = bitmapdrawBasf.getBitmap();
            bitmapBasf = Bitmap.createScaledBitmap(bBasf, width, height, false);

            // Bayer
            BitmapDrawable bitmapdrawBayer = (BitmapDrawable)getResources().getDrawable(R.drawable.bayer);
            Bitmap bBayer = bitmapdrawBayer.getBitmap();
            bitmapBayer = Bitmap.createScaledBitmap(bBayer, width, height, false);

            // Dow
            BitmapDrawable bitmapdrawDow = (BitmapDrawable)getResources().getDrawable(R.drawable.dow);
            Bitmap bDow = bitmapdrawDow.getBitmap();
            bitmapDow = Bitmap.createScaledBitmap(bDow, width, height, false);

            // Dupont
            BitmapDrawable bitmapdrawDupont = (BitmapDrawable)getResources().getDrawable(R.drawable.dupont);
            Bitmap bDupont = bitmapdrawDupont.getBitmap();
            bitmapDupont = Bitmap.createScaledBitmap(bDupont, width, height, false);

            // East West Seed
            BitmapDrawable bitmapdrawEastWestSeed = (BitmapDrawable)getResources().getDrawable(R.drawable.east_west_seed);
            Bitmap bEastWestSeed = bitmapdrawEastWestSeed.getBitmap();
            bitmapEastWestSeed = Bitmap.createScaledBitmap(bEastWestSeed, width, height, false);

            // Monsanto
            BitmapDrawable bitmapdrawMonsanto = (BitmapDrawable)getResources().getDrawable(R.drawable.monsanto);
            Bitmap bMonsanto = bitmapdrawMonsanto.getBitmap();
            bitmapMonsanto = Bitmap.createScaledBitmap(bMonsanto, width, height, false);

            // Nufarm
            BitmapDrawable bitmapdrawNufarm = (BitmapDrawable)getResources().getDrawable(R.drawable.nufarm);
            Bitmap bNufarm = bitmapdrawNufarm.getBitmap();
            bitmapNufarm = Bitmap.createScaledBitmap(bNufarm, width, height, false);

            // PetrokimiaKayaku
            BitmapDrawable bitmapdrawPetrokimiaKayaku = (BitmapDrawable)getResources().getDrawable(R.drawable.petrokimia_kayaku);
            Bitmap bPetrokimiaKayaku = bitmapdrawPetrokimiaKayaku.getBitmap();
            bitmapPetrokimiaKayaku = Bitmap.createScaledBitmap(bPetrokimiaKayaku, width, height, false);

            // PetrosidaGersik
            BitmapDrawable bitmapdrawPetrosidaGersik = (BitmapDrawable)getResources().getDrawable(R.drawable.petrosida_gersik);
            Bitmap bPetrosidaGersik = bitmapdrawPetrosidaGersik.getBitmap();
            bitmapPetrosidaGersik = Bitmap.createScaledBitmap(bPetrosidaGersik, width, height, false);

            // RoyalAgro
            BitmapDrawable bitmapdrawRoyalAgro = (BitmapDrawable)getResources().getDrawable(R.drawable.royal_agro);
            Bitmap bRoyalAgro = bitmapdrawRoyalAgro.getBitmap();
            bitmapRoyalAgro = Bitmap.createScaledBitmap(bRoyalAgro, width, height, false);

            // Syngenta
            BitmapDrawable bitmapdrawSyngenta = (BitmapDrawable)getResources().getDrawable(R.drawable.syngenta);
            Bitmap bSyngenta = bitmapdrawSyngenta.getBitmap();
            bitmapSyngenta = Bitmap.createScaledBitmap(bSyngenta, width, height, false);

            // Yara
            BitmapDrawable bitmapdrawYara = (BitmapDrawable)getResources().getDrawable(R.drawable.yara);
            Bitmap bYara = bitmapdrawYara.getBitmap();
            bitmapYara = Bitmap.createScaledBitmap(bYara, width, height, false);

            // Kamentan
            BitmapDrawable bitmapdrawKamentan = (BitmapDrawable)getResources().getDrawable(R.drawable.kamentan);
            Bitmap bKamentan = bitmapdrawKamentan.getBitmap();
            bitmapKamentan = Bitmap.createScaledBitmap(bKamentan, width, height, false);

            return "true";
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            getAllData();
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
        String strUserName       = ar.optString("Nama");
        String strPhoneNo        = ar.optString("NoTelp");
        int intUserType          = ar.optInt("UserType");
        String strUserTypeName   = ar.optString("UserTypeName");
        String strUserId         = ar.optString("UserId");
        String strJenisTanaman   = ar.optString("TanamanName");
        String strNamaPerusahaan = ar.optString("AgronomisName");
        int intAgronomisId       = ar.optInt("AgronomisId");
        String strPictures       = ar.optString("Pictures");
        String strGcmId          = ar.optString("gcm_registration_id");

        // For marker
        String strTitle;
        if (intUserType==1) {
            strTitle = strUserName + "|" + strPhoneNo + "|" + strUserId + "|" + strUserTypeName +
                       "|" + strJenisTanaman + "|" + strPictures + "|" + strGcmId + "|#" + intUserType;
        } else if (intUserType==3) {
            strTitle = strUserName + "|" + strPhoneNo + "|" + strUserId + "|" + strUserTypeName +
                    "|" + strNamaPerusahaan + "|" + strPictures + "|" + strGcmId + "|#" + intUserType;
        } else {
            strTitle = strUserName + "|" + strPhoneNo + "|" + strUserId + "|" + strUserTypeName +
                    "|" + "empty" + "|" + strPictures + "|" + strGcmId + "|#" + intUserType;
        }
        String strSnippetPetani        = "Kategori User : " + strUserTypeName + "<br>" + "Jenis Tanaman : " + strJenisTanaman;
        String strSnippetTokoPertanian = "Kategori User : " + strUserTypeName;
        String strSnippetAgronomis     = "Kategori User : " + strUserTypeName + "<br>" + "Perusahaan : " + strNamaPerusahaan;

        if (mMap!=null) {
            fromPosition = new LatLng(newLatitude, newLongitude);
			final LatLng latLngPosition = new LatLng(lat1, lng1);
          
            // Move the camera instantly to hamburg with a zoom of 10.
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(fromPosition, 13, 90, 0));
            mMap.animateCamera(cameraUpdate);
            mMap.getUiSettings().setCompassEnabled(false);

            if (intUserType==1) {
                /*** UserType Petani ***/
                markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetPetani)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmapPetani))));
            } else if (intUserType==2) {
                /*** UserType Toko Pertanian ***/
                markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetTokoPertanian)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.tokopertanian_off))));
            } else if (intUserType==3) {
                /*** UserType Agronomis ***/
                if (intAgronomisId==10) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapAgricon))));
                } else if (intAgronomisId==1) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapBasf))));
                } else if (intAgronomisId==2) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapBayer))));
                } else if (intAgronomisId==3) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapDow))));
                } else if (intAgronomisId==4) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapDupont))));
                } else if (intAgronomisId==47) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapEastWestSeed))));
                } else if (intAgronomisId==6) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapMonsanto))));
                } else if (intAgronomisId==7) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapNufarm))));
                } else if (intAgronomisId==31) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapPetrokimiaKayaku))));
                } else if (intAgronomisId==32) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapPetrosidaGersik))));
                } else if (intAgronomisId==45) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapRoyalAgro))));
                } else if (intAgronomisId==8) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapSyngenta))));
                } else if (intAgronomisId==42) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapYara))));
                } else {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetAgronomis)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.agronomis_off))));
                }
            } else if (intUserType==4) {
                /*** UserType PPL ***/
                markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetTokoPertanian)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmapKamentan))));
            } else {
                /*** UserType Umum ***/
                markers.add(mMap.addMarker(new MarkerOptions().position(latLngPosition).title(strTitle).snippet(strSnippetTokoPertanian)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.male_off))));
            }
        } else {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.container)).getMap();
            Toast.makeText(getApplicationContext(), "MAP NULL", Toast.LENGTH_LONG).show();
        }
    }

    public void openMap(LatLng fromPosition, LatLng toPosition, String name, int UserType) {
        try {
            md = new GoogleMapV2Direction();

            // Getting reference to SupportMapFragment
            fragmentTwo = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
            mMapTwo 	= fragmentTwo.getMap();

            fragment.getView().setVisibility(View.GONE);
            fragmentTwo.getView().setVisibility(View.VISIBLE);

            if (mMapTwo!=null) {
                String _Lat  = String.valueOf(toPosition.latitude);
                String _Long = String.valueOf(toPosition.longitude);

                if ((_Lat.equals("") || (_Long.equals("")))) {
                    Toast.makeText(getBaseContext(), "Lokasi Tujuan tidak mempunyai Latitude dan Longitude", Toast.LENGTH_LONG).show();
                } else {
                    md = new GoogleMapV2Direction();

                    // Getting reference to SupportMapFragment
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(fromPosition, 13, 90, 0));
                    mMapTwo.animateCamera(cameraUpdate);
                    mMapTwo.getUiSettings().setCompassEnabled(false);
                    mMapTwo.addMarker(new MarkerOptions().position(fromPosition).title("Your Position")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

                    /*** UserType Petani ***/
                    if (UserType==1) {
                        mMapTwo.addMarker(new MarkerOptions()
                                .position(toPosition)
                                .title(name)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.petani_on)));
                        /*** UserType Toko Pertanian ***/
                    } else if (UserType==2) {
                        mMapTwo.addMarker(new MarkerOptions()
                                .position(toPosition)
                                .title(name)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.tokopertanian_on)));
                        /*** UserType Agronomis ***/
                    } else if (UserType==3) {
                        mMapTwo.addMarker(new MarkerOptions()
                                .position(toPosition)
                                .title(name)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.agronomis_on)));
                        /*** UserType PPL ***/
                    } else {
                        mMapTwo.addMarker(new MarkerOptions()
                                .position(toPosition)
                                .title(name)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ppl_on)));
                    }
                    getDirectionMap(fromPosition, toPosition);
                }
            } else {
                Toast.makeText(getBaseContext(), "No Map", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            Toast.makeText(FindFriendLocationActivity.this, "Can't get gps location, make sure your gps is enable",Toast.LENGTH_LONG).show();
        }
    }

    private void getDirectionMap(LatLng from, LatLng to) {
        LatLng fromto[] = { from, to };
        new LongOperation().execute(fromto);
    }

    private class LongOperation extends AsyncTask<LatLng, Void, Document> {
        @Override
        protected Document doInBackground(LatLng... params) {
            Document doc = md.getDocument(params[0], params[1], GoogleMapV2Direction.MODE_DRIVING);
            return doc;
        }
        @Override
        protected void onPostExecute(Document result) {
            setResult(result);
        }
        @Override
        protected void onPreExecute() { }
        @Override
        protected void onProgressUpdate(Void... values) { }
    }

    public void setResult(Document doc) {
        ArrayList<LatLng> directionPoint = md.getDirection(doc);
        PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.RED);

        for (int i = 0; i < directionPoint.size(); i++) {
            rectLine.add(directionPoint.get(i));
        }

        mMapTwo.addPolyline(rectLine);
    }

    public void onBackPressedAction() {
        if (fragment.getView().getVisibility()!=View.GONE) {
            finish();
        } else {
            fragment.getView().setVisibility(View.VISIBLE);
            fragmentTwo.getView().setVisibility(View.GONE);
            linDesc.setVisibility(View.VISIBLE);
            linFilter.setVisibility(View.VISIBLE);
            mMapTwo.clear();
        }
    }

    @Override
    public void onBackPressed() {
        onBackPressedAction();
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
