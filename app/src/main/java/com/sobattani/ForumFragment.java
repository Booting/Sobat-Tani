package com.sobattani;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.etsy.android.grid.StaggeredGridView;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.Referensi;
import com.sobattani.Utils.callURL;
import com.sobattani.adapter.ForumAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ForumFragment extends Fragment implements ForumAdapter.ForumAdapterListener, AdapterView.OnItemSelectedListener {
	private StaggeredGridView itemList;
	private ForumAdapter forumAdapter;
    private String UserId = "", UserType = "";
    private ProgressBar progressBar;
    private ImageView btnSell;
    private RequestQueue queue;
	private SharedPreferences sobatTaniPref;
	private ProgressDialog pDialog;
	private String url="";
	private Spinner spinCategori;
	private ArrayList<Kategori> categoriList;
	private int categoriId;
	private TextView txtCategori;
	private Typeface fontUbuntuL;
	private Activity mParentActivity;

	public static ForumFragment newInstance() {
		ForumFragment f = new ForumFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.activity_forum, container, false);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		fontUbuntuL   = FontCache.get(mParentActivity, "DroidSans");
		queue 		  = Volley.newRequestQueue(mParentActivity);
		progressBar   = (ProgressBar) view.findViewById(R.id.progressBar);
		sobatTaniPref = mParentActivity.getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
		UserId        = sobatTaniPref.getString("UserId", "");
		UserType      = sobatTaniPref.getString("UserType", "");
		itemList      = (StaggeredGridView) view.findViewById(R.id.itemList);
		btnSell		  = (ImageView) view.findViewById(R.id.btnSell);
		txtCategori   = (TextView) view.findViewById(R.id.txtCategori);
		spinCategori  = (Spinner) view.findViewById(R.id.spinCategori);

		txtCategori.setTypeface(fontUbuntuL);
		spinCategori.setOnItemSelectedListener(this);

		if (UserType.equalsIgnoreCase("4")) {
			//btnSell.setVisibility(View.GONE);
		} else {
			//btnSell.setVisibility(View.VISIBLE);
		}

		btnSell.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivityForResult(new Intent(mParentActivity, ForumAddActivity.class), 1);
			}
		});

		getCategoriSpinner();

		return view;
    }

	public void getCategoriSpinner() {
		categoriList = new ArrayList<Kategori>();
		String url = Referensi.url + "/getCategori.php";

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response!=null) {
						JSONArray categories = response.getJSONArray("categories");
						Kategori catt = new Kategori(99, "Semua Topik");
						categoriList.add(catt);

						for (int i = 0; i < categories.length(); i++) {
							JSONObject catObj = (JSONObject) categories.get(i);
							Kategori cat = new Kategori(catObj.getInt("CategoriId"), catObj.getString("CategoriName"));
							categoriList.add(cat);
						}
						populateSpinnerCategori();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(mParentActivity, error.toString(), Toast.LENGTH_SHORT).show();
			}
		});
		queue.add(jsObjRequest);
	}

	/**
	 * Adding spinner data
	 * */
	private void populateSpinnerCategori() {
		List<String> lables  = new ArrayList<String>();
		categoriId = 0;

		for (int i = 0; i < categoriList.size(); i++) {
			lables.add(categoriList.get(i).getName());
		}

		ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(mParentActivity, R.layout.spinnerlayout, lables) {
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				((TextView) v).setTypeface(fontUbuntuL);
				return v;
			}
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				View v = super.getDropDownView(position, convertView, parent);
				((TextView) v).setTypeface(fontUbuntuL);
				return v;
			}
		};
		countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinCategori.setAdapter(countryAdapter);
	}

	@SuppressLint("NewApi")
	public void getAllData() {
		progressBar.setVisibility(View.VISIBLE);
		itemList.setVisibility(View.GONE);

		String url = Referensi.url + "/getAllThread.php?UserId="+UserId+"&Categori="+categoriId;
		JsonArrayRequest jsObjRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				progressBar.setVisibility(View.GONE);
				itemList.setVisibility(View.VISIBLE);

				forumAdapter = new ForumAdapter(mParentActivity, response, ForumFragment.this);
				itemList.setAdapter(forumAdapter);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				progressBar.setVisibility(View.GONE);
				Toast.makeText(mParentActivity, error.toString(), Toast.LENGTH_SHORT).show();
			}
		});
		queue.add(jsObjRequest);
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == mParentActivity.RESULT_OK) {
				getAllData();
            }
        }
	}

	@Override
	public void onLikeClicked(String ThreadId, String ThreadLikeId, String mDiLike) {
		String diLike;
		if (mDiLike.equalsIgnoreCase("1")) {
			diLike = "0";
		} else {
			diLike = "1";
		}

		if (!ThreadLikeId.equalsIgnoreCase("")) {
			new postLike("UPDATETHREADLIKE", ThreadId, ThreadLikeId, diLike).execute();
		} else {
			new postLike("ADDNEWTHREADLIKE", ThreadId, ThreadLikeId, diLike).execute();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		parent.getItemAtPosition(position);

		switch (parent.getId()) {
			case R.id.spinCategori:
				categoriId = categoriList.get(position).getId();
				getAllData();
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private class postLike extends AsyncTask<String, Void, String> {
		String ct, ThreadId, ThreadLikeId, diLike;
		public postLike(String mCt, String mThreadId, String mThreadLikeId, String mDiLike) {
			ct     		 = mCt;
			diLike 		 = mDiLike;
			ThreadId     = mThreadId;
			ThreadLikeId = mThreadLikeId;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(mParentActivity);
			pDialog.setMessage("Working...");
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			if (ct.equalsIgnoreCase("ADDNEWTHREADLIKE")) {
				url = Referensi.url+"/service.php?ct="+ ct +
						"&ThreadId="+ThreadId+
						"&UserId="+UserId+
						"&IsLike="+diLike;
			} else {
				url = Referensi.url+"/service.php?ct="+ ct +
						"&IsLike="+diLike+
						"&ThreadLikeId="+ThreadLikeId;
			}

			String hasil = callURL.call(url);
			return hasil;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if (result.equalsIgnoreCase("true")) {
				if (diLike.equalsIgnoreCase("1")) {
					Toast.makeText(mParentActivity, "Like succesfully!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mParentActivity, "Unlike succesfully!", Toast.LENGTH_SHORT).show();
				}
				getAllData();
			} else {
				if (diLike.equalsIgnoreCase("1")) {
					Toast.makeText(mParentActivity, "Like failed! Try Again!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mParentActivity, "Unlike failed! Try Again!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof TopFragment)
			mParentActivity = (TopFragment) activity;
		if (mParentActivity == null)
			mParentActivity = TopFragment.getInstance();
	}
}
