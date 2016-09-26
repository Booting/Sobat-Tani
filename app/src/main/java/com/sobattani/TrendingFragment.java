package com.sobattani;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.etsy.android.grid.StaggeredGridView;
import com.sobattani.Utils.Referensi;
import com.sobattani.Utils.callURL;
import com.sobattani.adapter.ForumAdapter;

import org.json.JSONArray;

public class TrendingFragment extends Fragment implements ForumAdapter.ForumAdapterListener {
    private ProgressBar progressBar;
    private Activity mParentActivity;
    private StaggeredGridView itemList;
    private ForumAdapter forumAdapter;
    private RequestQueue queue;
    private String UserId = "";
    private SharedPreferences sobatTaniPref;
    private ProgressDialog pDialog;
    private String url="";

    public static TrendingFragment newInstance() {
        TrendingFragment f = new TrendingFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.trending_fragment, container, false);

        progressBar   = (ProgressBar) view.findViewById(R.id.progressBusy);
        itemList      = (StaggeredGridView) view.findViewById(R.id.itemList);
        queue 		  = Volley.newRequestQueue(mParentActivity);
        sobatTaniPref = mParentActivity.getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
        UserId        = sobatTaniPref.getString("UserId", "");

        getAllData();

        return view;
    }

    public void getAllData() {
        progressBar.setVisibility(View.VISIBLE);
        itemList.setVisibility(View.GONE);

        String url = Referensi.url + "/getTrending.php?UserId="+UserId;
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                itemList.setVisibility(View.VISIBLE);

                forumAdapter = new ForumAdapter(mParentActivity, response, TrendingFragment.this);
                itemList.setAdapter(forumAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
        queue.add(jsObjRequest);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof TopFragment)
            mParentActivity = (TopFragment) activity;
        if (mParentActivity == null)
            mParentActivity = TopFragment.getInstance();
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
}