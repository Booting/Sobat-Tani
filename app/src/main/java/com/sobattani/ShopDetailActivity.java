package com.sobattani;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.LoopViewPager;
import com.sobattani.Utils.Referensi;
import com.sobattani.Utils.TypeFaceSpan;
import com.viewpagerindicator.CirclePageIndicator;
import org.json.JSONArray;
import org.json.JSONException;

public class ShopDetailActivity extends AppCompatActivity {
	private Typeface fontUbuntuL, fontUbuntuB;
    private TextView txtItemName, txtHargaDiskon, txtCategori, txtCategoriDetail, txtDeskripsi, txtDeskripsiDetail,
			txtNoRekening, txtNoRekeningDetail, txtContact, txtContactDetail, txtLokasi, txtLokasiDetail, txtEdit, txtCall;
    private LoopViewPager imagePager;
    private CirclePageIndicator cIndicator;
    private ImagePagerAdapter productImageAdapter;
    @SuppressWarnings("unused")
	private int imagePosition = 0;
	private SharedPreferences sobatTaniPref;
	private String UserId = "";
	private LinearLayout linEdit;
	private ProgressDialog pDialog;
	private Toolbar toolbar;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		setContentView(R.layout.activity_shop_detail);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		initToolbar();

		sobatTaniPref 		= getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
		UserId       		= sobatTaniPref.getString("UserId", "");
		fontUbuntuL  	    = FontCache.get(ShopDetailActivity.this, "DroidSans");
		fontUbuntuB  	    = FontCache.get(ShopDetailActivity.this, "DroidSans-Bold");
		imagePager  	    = (LoopViewPager) findViewById(R.id.pagerItemImages);
        cIndicator  	    = (CirclePageIndicator) findViewById(R.id.indicator);
		txtItemName  	    = (TextView) findViewById(R.id.txtItemName);
		txtHargaDiskon      = (TextView) findViewById(R.id.txtHargaDiskon);
		txtDeskripsi        = (TextView) findViewById(R.id.txtDeskripsi);
		txtDeskripsiDetail  = (TextView) findViewById(R.id.txtDeskripsiDetail);
		txtCategori		    = (TextView) findViewById(R.id.txtCategori);
		txtCategoriDetail   = (TextView) findViewById(R.id.txtCategoriDetail);
		txtNoRekening		= (TextView) findViewById(R.id.txtNoRekening);
		txtNoRekeningDetail = (TextView) findViewById(R.id.txtNoRekeningDetail);
		txtContact		    = (TextView) findViewById(R.id.txtContact);
		txtContactDetail    = (TextView) findViewById(R.id.txtContactDetail);
		txtLokasi		    = (TextView) findViewById(R.id.txtLokasi);
		txtLokasiDetail	    = (TextView) findViewById(R.id.txtLokasiDetail);
		txtEdit				= (TextView) findViewById(R.id.txtEdit);
		txtCall			    = (TextView) findViewById(R.id.txtCall);
		linEdit				= (LinearLayout) findViewById(R.id.linEdit);
		
		txtItemName.setTypeface(fontUbuntuB);
		txtHargaDiskon.setTypeface(fontUbuntuL);
		txtDeskripsi.setTypeface(fontUbuntuB);
		txtDeskripsiDetail.setTypeface(fontUbuntuL);
		txtCategori.setTypeface(fontUbuntuB);
		txtCategoriDetail.setTypeface(fontUbuntuL);
		txtNoRekening.setTypeface(fontUbuntuB);
		txtNoRekeningDetail.setTypeface(fontUbuntuL);
		txtContact.setTypeface(fontUbuntuB);
		txtContactDetail.setTypeface(fontUbuntuL);
		txtLokasi.setTypeface(fontUbuntuB);
		txtLokasiDetail.setTypeface(fontUbuntuL);
		txtEdit.setTypeface(fontUbuntuB);
		txtCall.setTypeface(fontUbuntuB);

		pDialog = new ProgressDialog(ShopDetailActivity.this);
		pDialog.setMessage("Working...");
		pDialog.setCancelable(false);
		
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(getIntent().getExtras().getString("ItemPicture"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		productImageAdapter = new ImagePagerAdapter(jsonArray);
        imagePager.setAdapter(productImageAdapter);
        cIndicator.setViewPager(imagePager);
        cIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				imagePosition = position;
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		
        txtItemName.setText(getIntent().getExtras().getString("ItemName"));
		txtHargaDiskon.setText(getIntent().getExtras().getString("HargaDiskon"));
        txtDeskripsiDetail.setText(getIntent().getExtras().getString("ItemDeskripsi"));
        txtLokasiDetail.setText(getIntent().getExtras().getString("ItemLocation"));
        txtNoRekeningDetail.setText(getIntent().getExtras().getString("NoRekening"));
        txtCategoriDetail.setText(getIntent().getExtras().getString("ItemKategori"));
        if (getIntent().getExtras().getString("ItemContact").equalsIgnoreCase("") || 
        		getIntent().getExtras().getString("ItemContact").equalsIgnoreCase("-")) {
        	txtContactDetail.setText(getIntent().getExtras().getString("ItemSeller")+" (-)");
        } else {
        	txtContactDetail.setText(getIntent().getExtras().getString("ItemSeller")+" ( "+getIntent().getExtras().getString("ItemContact")+" )");
        }

		if (getIntent().getExtras().getString("SellerId").equalsIgnoreCase(UserId)) {
			linEdit.setVisibility(View.VISIBLE);
		} else {
			linEdit.setVisibility(View.GONE);
		}
    }

	private void initToolbar() {
		SpannableString spanToolbar = new SpannableString(getString(R.string.pasar_sobat_detail));
		spanToolbar.setSpan(new TypeFaceSpan(ShopDetailActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

	public void btnEditClick(View v) {
		new intentEdit().execute();
	}

	private class intentEdit extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			startActivityForResult(new Intent(getApplicationContext(), ShopAddActivity.class)
					.putExtra("ItemId", getIntent().getExtras().getString("ItemId"))
					.putExtra("ItemName", getIntent().getExtras().getString("ItemName"))
					.putExtra("ItemDeskripsi", getIntent().getExtras().getString("ItemDeskripsi"))
					.putExtra("HargaDiskon", getIntent().getExtras().getString("HargaDiskon"))
					.putExtra("ItemContact", getIntent().getExtras().getString("ItemContact"))
					.putExtra("NoRekening", getIntent().getExtras().getString("NoRekening"))
					.putExtra("ItemLocation", getIntent().getExtras().getString("ItemLocation"))
					.putExtra("ItemKategori", getIntent().getExtras().getString("ItemKategori"))
					.putExtra("ItemPicture", getIntent().getExtras().getString("ItemPicture")), 1);
			return "";
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();
		}
	}

	public void btnCallClick(View v) {
		String uri = "tel:" + getIntent().getExtras().getString("ItemContact").trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
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
            ImageView imageView = new ImageView(ShopDetailActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);        
            Glide.with(ShopDetailActivity.this).load(Referensi.url + "/pictures/" + jsonProductImages.optString(position))
					.placeholder(R.drawable.img_loader).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
					.dontAnimate().into(imageView);

			((ViewPager) container).addView(imageView, 0);
            
            imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i = new Intent(ShopDetailActivity.this, PhotoViewer.class);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK);
				finish();
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
