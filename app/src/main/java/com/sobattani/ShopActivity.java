package com.sobattani;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.etsy.android.grid.StaggeredGridView;
import com.sobattani.Utils.Referensi;
import com.sobattani.Utils.TypeFaceSpan;
import com.sobattani.adapter.ShopAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShopActivity extends ActionBarActivity {
	private StaggeredGridView itemList;
	private ShopAdapter shopAdapter;
    private String UserId = "", UserType = "";
    private ProgressBar progressBar;
    private ImageView btnSell;
    private RequestQueue queue;
	private SharedPreferences sobatTaniPref;
	private JSONArray str_login  = null;
	private Toolbar toolbar;
	private SwipeRefreshLayout swipe;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		setContentView(R.layout.activity_shop);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		initToolbar();

		queue 		  = Volley.newRequestQueue(this);
		progressBar   = (ProgressBar) findViewById(R.id.progressBar);
		sobatTaniPref = getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
		UserId        = sobatTaniPref.getString("UserId", "");
		itemList      = (StaggeredGridView) findViewById(R.id.itemList);
		btnSell		  = (ImageView) findViewById(R.id.btnSell);
		swipe		  = (SwipeRefreshLayout) findViewById(R.id.swipe);

		btnSell.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivityForResult(new Intent(getApplicationContext(), ShopRulesActivity.class), 1);
			}
		});

		swipe.setSize(SwipeRefreshLayout.DEFAULT);
		swipe.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light,
				android.R.color.holo_green_light,
				android.R.color.holo_red_light
		);

		swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				shopAdapter = null;
				getUserDetail();
			}
		});

		getUserDetail();
    }

	private void initToolbar() {
		SpannableString spanToolbar = new SpannableString(getString(R.string.menu_pasar_sobat));
		spanToolbar.setSpan(new TypeFaceSpan(ShopActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

	public void getUserDetail() {
		progressBar.setVisibility(View.VISIBLE);
		swipe.setVisibility(View.GONE);

		String url = Referensi.url+"/getUserDetail.php?UserId="+UserId;
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if (swipe.isRefreshing()) {
						swipe.setRefreshing(false);
					}

					str_login  = response.getJSONArray("info");
					for (int i = 0; i < str_login.length(); i++){
						JSONObject ar = str_login.getJSONObject(i);
						SharedPreferences.Editor editor = sobatTaniPref.edit();
						editor.putString("UserType", ar.getString("UserType"));
						editor.commit();
					}

					UserType = sobatTaniPref.getString("UserType", "");
					if (UserType.equalsIgnoreCase("4")) {
						btnSell.setVisibility(View.GONE);
					} else {
						btnSell.setVisibility(View.VISIBLE);
					}

					getAllData();
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

	@SuppressLint("NewApi")
	public void getAllData() {
        String url;
		if (UserType.equalsIgnoreCase("1")) {
			url = Referensi.url + "/getAllItemsPetani.php";
		} else {
			url = Referensi.url + "/getAllItems.php";
		}

		JsonArrayRequest jsObjRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
            	progressBar.setVisibility(View.GONE);
				swipe.setVisibility(View.VISIBLE);
        		
            	shopAdapter = new ShopAdapter(ShopActivity.this, response, UserType);
				itemList.setAdapter(shopAdapter);
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
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
			super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
				getUserDetail();
            }
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
