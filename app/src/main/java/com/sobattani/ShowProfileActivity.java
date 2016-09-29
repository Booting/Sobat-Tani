package com.sobattani;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.LoopViewPager;
import com.sobattani.Utils.NestedListView;
import com.sobattani.Utils.Referensi;
import com.sobattani.Utils.TypeFaceSpan;
import com.sobattani.Utils.callURL;
import com.sobattani.adapter.CommentAdapter;
import com.viewpagerindicator.CirclePageIndicator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class ShowProfileActivity extends AppCompatActivity {
	private Typeface fontUbuntuL, fontUbuntuB;
    private TextView txtKategori, txtName, txtDescription, txtLike, txtCountLike, txtComment,txtCountComment, txtIsLike;
    private ImageView imgProfile;
    private LoopViewPager imagePager;
    private CirclePageIndicator cIndicator;
    private ImagePagerAdapter productImageAdapter;
    @SuppressWarnings("unused")
	private int imagePosition = 0;
    private EditText txtWriteComment;
    private Button btnPost;
    private NestedListView lsvList;
    private CommentAdapter commentAdapter;
    private ProgressBar progressBar;
    private RequestQueue queue;
    private JSONArray str_login  = null;
    private LinearLayout linList;
	private String url="", UserId, WhoCommentId, LikeId, strGcmId;
    private SharedPreferences sobatTaniPref;
    private ImageSwitcher imgLike;
    private RelativeLayout relLike;
    private boolean isAvailable=false;
    private Toolbar toolbar;
    private Dialog dialog;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		setContentView(R.layout.activity_show_profile);

		if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        sobatTaniPref   = getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
		fontUbuntuL     = FontCache.get(ShowProfileActivity.this, "Ubuntu-L");
		fontUbuntuB     = FontCache.get(ShowProfileActivity.this, "Ubuntu-B");
		imagePager      = (LoopViewPager) findViewById(R.id.pagerItemImages);
        cIndicator      = (CirclePageIndicator) findViewById(R.id.indicator);
        txtKategori     = (TextView) findViewById(R.id.txtKategori);
        txtName		    = (TextView) findViewById(R.id.txtName);
        txtDescription  = (TextView) findViewById(R.id.txtDescription);
        txtLike		    = (TextView) findViewById(R.id.txtLike);
        txtCountLike    = (TextView) findViewById(R.id.txtCountLike);
        txtComment      = (TextView) findViewById(R.id.txtComment);
        txtCountComment = (TextView) findViewById(R.id.txtCountComment);
		imgProfile      = (ImageView) findViewById(R.id.imgProfile);
		txtWriteComment = (EditText) findViewById(R.id.txtWriteComment);
		btnPost			= (Button) findViewById(R.id.btnPost);
		lsvList			= (NestedListView) findViewById(R.id.lsvList);
		progressBar  	= (ProgressBar) findViewById(R.id.progressBusy);
		queue 			= Volley.newRequestQueue(this);
		linList			= (LinearLayout) findViewById(R.id.linList);
		UserId			= getIntent().getExtras().getString("UserId");
		WhoCommentId    = sobatTaniPref.getString("UserId", "");
		imgLike         = (ImageSwitcher) findViewById(R.id.imgLike);
		relLike			= (RelativeLayout) findViewById(R.id.relLike);
		txtIsLike		= (TextView) findViewById(R.id.txtIsLike);
		
		txtName.setTypeface(fontUbuntuB);
		txtKategori.setTypeface(fontUbuntuL);
		txtDescription.setTypeface(fontUbuntuL);
		txtLike.setTypeface(fontUbuntuL);
		txtCountLike.setTypeface(fontUbuntuL);
		txtComment.setTypeface(fontUbuntuL);
		txtCountComment.setTypeface(fontUbuntuL);
		txtWriteComment.setTypeface(fontUbuntuL);
		btnPost.setTypeface(fontUbuntuB);
		
		imgLike.setFactory(new ViewSwitcher.ViewFactory() {
            @SuppressLint("InlinedApi")
			@SuppressWarnings("deprecation")
			@Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT));
                myView.setImageResource(R.drawable.thumb_up_outline);
                return myView;
            }
        });
		
		relLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String diLike;
				if (txtIsLike.getText().toString().equalsIgnoreCase("1")) {
					diLike = "0";
				} else {
					diLike = "1";
				}
				
				if (isAvailable) {
					new postLike("UPDATELIKE", diLike).execute();
				} else {
					new postLike("ADDNEWLIKE", diLike).execute();
				}
			}
		});

        btnPost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (txtWriteComment.getText().toString().equalsIgnoreCase("")) {
					txtWriteComment.setError("Comment is required!");
				} else {
					new postComment().execute();
				}
			}
		});

        showDialog();
        getUserDetail();
        getUserLike();
	}

    public void getUserDetail() {
        String url = Referensi.url+"/getUserDetail.php?UserId=" + UserId;
        System.out.println("getUserDetail (url) : "+url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("getUserDetail (response) : "+response);
                try {
                    JSONArray jArrInfo  = response.getJSONArray("info");
                    JSONObject jObjInfo = jArrInfo.getJSONObject(0);

                    JSONArray jsonArray = null;
                    try {
                        if (jObjInfo.optString("Pictures").equalsIgnoreCase("")) {
                            jsonArray = new JSONArray("[\"http://phandroid.s3.amazonaws.com/wp-content/uploads/2014/07/lightbulb.png\"]");
                        } else {
                            String[] splitPic = jObjInfo.optString("Pictures").split(",");
                            jsonArray = new JSONArray();
                            for (int i=0; i<splitPic.length; i++) {
                                jsonArray.put(splitPic[i]);
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    productImageAdapter = new ImagePagerAdapter(jsonArray);
                    imagePager.setAdapter(productImageAdapter);
                    cIndicator.setViewPager(imagePager);
                    cIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
                        @Override
                        public void onPageSelected(int position) {
                            imagePosition = position;
                        }
                        @Override
                        public void onPageScrollStateChanged(int state) { }
                    });

                    strGcmId = jObjInfo.optString("GcmId");
                    txtName.setText(jObjInfo.optString("Nama"));
                    txtKategori.setText(jObjInfo.optString("UserTypeName"));
                    txtDescription.setText(jObjInfo.optString("Description"));
                    Glide.with(ShowProfileActivity.this).load("https://graph.facebook.com/" + jObjInfo.optString("UserId") + "/picture?type=large")
                            .placeholder(R.drawable.img_loader).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                            .dontAnimate().into(imgProfile);
                    initToolbar(jObjInfo.optString("Nama"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try { dialog.dismiss(); } catch (Exception e) {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try { dialog.dismiss(); } catch (Exception e) {}
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }

    private void initToolbar(String strName) {
        SpannableString spanToolbar = new SpannableString(strName + " " + getString(R.string.profile));
        spanToolbar.setSpan(new TypeFaceSpan(ShowProfileActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
	
	public void getUserLike() {
        String url = Referensi.url+"/getUserLike.php?UserId=" + UserId;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            	try {
            		int count = 0;
                    str_login = response.getJSONArray("info");
                    for (int i=0; i<str_login.length(); i++) {
                    	// CEK DI LIKE ATAU TIDAK
                    	if (str_login.optJSONObject(i).optString("WhoLikeId").equalsIgnoreCase(WhoCommentId)) {
                    		LikeId      = str_login.optJSONObject(i).optString("LikeId");
                    		isAvailable = true;
                    		if (str_login.optJSONObject(i).optString("LikeStatus").equalsIgnoreCase("1")) {
                    			imgLike.setImageResource(R.drawable.thumb_up);
                    			txtIsLike.setText("1");
                    		} else {
                    			imgLike.setImageResource(R.drawable.thumb_up_outline);
                    			txtIsLike.setText("0");
                    		}
                    	} else {
                    		txtIsLike.setText("0");
                    	}
                    	
                    	// GET COUNT
                    	if (str_login.optJSONObject(i).optString("LikeStatus").equalsIgnoreCase("1")) {
                    		count = count + 1;
                    	}
                    }
                    txtCountLike.setText(""+count);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            	getUserComment(UserId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	getUserComment(UserId);
            }
        });
        queue.add(jsObjRequest);
    }
	
	public void getUserComment(String UserId) {
        String url = Referensi.url+"/getUserComment.php?UserId="+UserId;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            	try {
                    str_login      = response.getJSONArray("info");
                    commentAdapter = new CommentAdapter(ShowProfileActivity.this, str_login);
                    lsvList.setAdapter(commentAdapter);
                    txtCountComment.setText(""+str_login.length());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
                linList.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
        queue.add(jsObjRequest);
    }
	
	private class postComment extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
        	try {
				String mComment = URLEncoder.encode(txtWriteComment.getText().toString().replace("\"", "'"), "utf-8");
	        	url = Referensi.url+"/service.php?ct=ADDNEWCOMMENT" +
	        			"&UserId="+UserId+
	        			"&WhoCommentId="+WhoCommentId+
	        			"&CommentText="+mComment;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        	String hasil = callURL.call(url);
        	return hasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equalsIgnoreCase("true")) {
            	txtWriteComment.setText("");
            	Toast.makeText(getApplicationContext(), "Post comment succesfully!", Toast.LENGTH_SHORT).show();
            	getUserComment(UserId);
                if (!UserId.equalsIgnoreCase(WhoCommentId)) {
                    sendMessageComment();
                }
            } else {
            	Toast.makeText(getApplicationContext(), "Post comment failed! Try Again!", Toast.LENGTH_SHORT).show();
            }
        }
    }
	
	private class postLike extends AsyncTask<String, Void, String> {
		String ct, diLike;
		public postLike(String mCt, String mDiLike) {
			ct     = mCt;
			diLike = mDiLike;
		}
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try { dialog.show(); } catch (Exception e) {}
        }
        @Override
        protected String doInBackground(String... params) {
        	if (ct.equalsIgnoreCase("ADDNEWLIKE")) {
	    		url = Referensi.url+"/service.php?ct="+ ct +
	        			"&UserId="+UserId+
	        			"&WhoLikeId="+WhoCommentId+
	        			"&LikeStatus="+diLike;
    		} else {
    			url = Referensi.url+"/service.php?ct="+ ct +
	        			"&LikeStatus="+diLike+
	        			"&LikeId="+LikeId;
    		}
        	
        	String hasil = callURL.call(url);
        	return hasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try { dialog.dismiss(); } catch (Exception e) {}
            if (result.equalsIgnoreCase("true")) {
            	if (diLike.equalsIgnoreCase("1")) {
            		Toast.makeText(getApplicationContext(), "Like succesfully!", Toast.LENGTH_SHORT).show();
                    if (!UserId.equalsIgnoreCase(WhoCommentId)) {
                        sendMessageLike();
                    }
            	} else {
            		Toast.makeText(getApplicationContext(), "Unlike succesfully!", Toast.LENGTH_SHORT).show();
            	}
            	getUserLike();
            } else {
            	if (diLike.equalsIgnoreCase("1")) {
            		Toast.makeText(getApplicationContext(), "Like failed! Try Again!", Toast.LENGTH_SHORT).show();
                    imgLike.setImageResource(R.drawable.thumb_up_outline);
            	} else {
            		Toast.makeText(getApplicationContext(), "Unlike failed! Try Again!", Toast.LENGTH_SHORT).show();
                    imgLike.setImageResource(R.drawable.thumb_up);
            	}
            }
        }
    }

    public void sendMessageLike() {
        String mUsername = "";
        try {
            mUsername = URLEncoder.encode(sobatTaniPref.getString("UserName", "").replace("\"", "'"), "utf-8");
        } catch (UnsupportedEncodingException e) {}

        String url = Referensi.url + "/send_message_like.php?regId=" + strGcmId + "&sender=" + mUsername + "&UserId=" + UserId;
        System.out.println("sendMessageLike (url) : "+url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }

    public void sendMessageComment() {
        String mUsername = "";
        try {
            mUsername = URLEncoder.encode(sobatTaniPref.getString("UserName", "").replace("\"", "'"), "utf-8");
        } catch (UnsupportedEncodingException e) {}

        String url = Referensi.url + "/send_message_comment.php?regId=" + strGcmId + "&sender=" + mUsername + "&UserId=" + UserId;
        System.out.println("sendMessageComment (url) : "+url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }
	
	private class ImagePagerAdapter extends PagerAdapter {
        private JSONArray jsonProductImages;
        
        public ImagePagerAdapter(JSONArray jsonProductImages) {
            this.jsonProductImages = jsonProductImages;
        }
        @Override
        public int getCount() {
            return jsonProductImages.length();
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(ShowProfileActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if (jsonProductImages.optString(position).contains("http")) {
                Glide.with(ShowProfileActivity.this).load(jsonProductImages.optString(position)).placeholder(R.drawable.img_loader)
                        .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(500, 500)
                        .dontAnimate().into(imageView);
            } else {
                Glide.with(ShowProfileActivity.this).load(Referensi.URL_CLOUDINARY + jsonProductImages.optString(position)).placeholder(R.drawable.img_loader)
                        .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(500, 500)
                        .dontAnimate().into(imageView);
            }
            
            ((ViewPager) container).addView(imageView, 0);
            
            imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i = new Intent(ShowProfileActivity.this, PhotoViewer.class);
		            i.putExtra("image_list", jsonProductImages.toString());
		            i.putExtra("position", position);
		            startActivity(i);
				}
			});
            
            return imageView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }

    private void showDialog() {
        dialog = new Dialog(ShowProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        TextView lblTitle = (TextView) dialog.findViewById(R.id.lblTitle);
        lblTitle.setTypeface(fontUbuntuL);

        try {
            dialog.show();
        } catch (Exception e) {}
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
