package com.sobattani;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.sobattani.Utils.ImageLoader;
import com.sobattani.Utils.Referensi;
import com.sobattani.Utils.callURL;
import com.sobattani.adapter.ForumKomentarAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ForumKomentarActivity extends Activity implements ForumKomentarAdapter.ForumKomentarAdapterListener {
	private StaggeredGridView itemList;
	private ForumKomentarAdapter forumKomentarAdapter;
    private String UserId = "", UserType = "", UserName = "", ThreadId = "", LikeCount = "", GcmId = "", Deskripsi = "";
    private ProgressBar progressBar;
    private RequestQueue queue;
	private SharedPreferences sobatTaniPref;
	private TextView txtCountLike, txtLike, txtSend;
	private Typeface fontUbuntuL, fontUbuntuB;
	private EditText txtMessage;
	private ImageView imageOne;
	private String imagepathOne=null;
	private JSONArray jsonArray = new JSONArray();
	@SuppressWarnings("unused")
	private long totalSize = 0;
	private HttpEntity resEntity;
	private int countImage=0;
	private int mMaxWidth  = 480;
	private int mMaxHeight = 480;
	private ImageLoader mImageLoader;
	private JSONArray jArrImages = null;
	private ProgressDialog pDialog;
	private String url = "";
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forum_komentar);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		fontUbuntuL    = FontCache.get(this, "Ubuntu-L");
		fontUbuntuB    = FontCache.get(this, "Ubuntu-B");
		queue 		   = Volley.newRequestQueue(this);
		progressBar    = (ProgressBar) findViewById(R.id.progressBar);
		sobatTaniPref  = getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
		UserId         = sobatTaniPref.getString("UserId", "");
		UserName	   = sobatTaniPref.getString("UserName", "");
		UserType       = sobatTaniPref.getString("UserType", "");
		ThreadId	   = getIntent().getExtras().getString("ThreadId");
		LikeCount	   = getIntent().getExtras().getString("LikeCount");
		GcmId   	   = getIntent().getExtras().getString("GcmId");
		itemList       = (StaggeredGridView) findViewById(R.id.itemList);
		txtCountLike   = (TextView) findViewById(R.id.txtCountLike);
		txtLike		   = (TextView) findViewById(R.id.txtLike);
		txtMessage	   = (EditText) findViewById(R.id.txtMessage);
		txtSend		   = (TextView) findViewById(R.id.txtSend);
		imageOne 	   = (ImageView) findViewById(R.id.imageOne);

		txtCountLike.setTypeface(fontUbuntuB);
		txtLike.setTypeface(fontUbuntuL);
		txtMessage.setTypeface(fontUbuntuL);
		txtSend.setTypeface(fontUbuntuB);
		txtCountLike.setText(LikeCount);

		pDialog = new ProgressDialog(ForumKomentarActivity.this);
		pDialog.setMessage("Working...");
		pDialog.setCancelable(false);

		getAllData();
    }

	@SuppressLint("NewApi")
	public void getAllData() {
		progressBar.setVisibility(View.VISIBLE);
		itemList.setVisibility(View.GONE);

		String url = Referensi.url + "/getAllReply.php?UserId="+UserId+"&ThreadId="+ThreadId;
		JsonArrayRequest jsObjRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				progressBar.setVisibility(View.GONE);
				itemList.setVisibility(View.VISIBLE);

				forumKomentarAdapter = new ForumKomentarAdapter(ForumKomentarActivity.this, response, ForumKomentarActivity.this);
				itemList.setAdapter(forumKomentarAdapter);
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

	public void btnSendClick(View v) {
		if (txtMessage.getText().toString().equalsIgnoreCase("")) {
			txtMessage.setError("Pesan tidak boleh kosong!");
		} else {
			if (imagepathOne!=null) {
				countImage = countImage+1;
			}

			if (countImage==0) {
				new addNewItem().execute();
			} else {
				new UploadImage().execute();
			}
		}
	}

	@Override
	public void onLikeClicked(String ReplyId, String ReplyLikeId, String mDiLike) {
		String diLike;
		if (mDiLike.equalsIgnoreCase("1")) {
			diLike = "0";
		} else {
			diLike = "1";
		}

		if (!ReplyLikeId.equalsIgnoreCase("")) {
			new postLike("UPDATEREPLYLIKE", ReplyId, ReplyLikeId, diLike).execute();
		} else {
			new postLike("ADDNEWREPLYLIKE", ReplyId, ReplyLikeId, diLike).execute();
		}
	}

	private class postLike extends AsyncTask<String, Void, String> {
		String ct, ReplyId, ReplyLikeId, diLike;
		public postLike(String mCt, String mReplyId, String mReplyLikeId, String mDiLike) {
			ct     		= mCt;
			diLike 		= mDiLike;
			ReplyId     = mReplyId;
			ReplyLikeId = mReplyLikeId;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ForumKomentarActivity.this);
			pDialog.setMessage("Working...");
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			if (ct.equalsIgnoreCase("ADDNEWREPLYLIKE")) {
				url = Referensi.url+"/service.php?ct="+ ct +
						"&ReplyId="+ReplyId+
						"&UserId="+UserId+
						"&IsLike="+diLike;
			} else {
				url = Referensi.url+"/service.php?ct="+ ct +
						"&IsLike="+diLike+
						"&ReplyLikeId="+ReplyLikeId;
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
					Toast.makeText(getApplicationContext(), "Like succesfully!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "Unlike succesfully!", Toast.LENGTH_SHORT).show();
				}
				getAllData();
			} else {
				if (diLike.equalsIgnoreCase("1")) {
					Toast.makeText(getApplicationContext(), "Like failed! Try Again!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "Unlike failed! Try Again!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private class UploadImage extends AsyncTask<HttpEntity, Void, HttpEntity> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.show();
		}
		@Override
		protected HttpEntity doInBackground(HttpEntity... params) {
			return doFileUpload();
		}
		@Override
		protected void onPostExecute(HttpEntity result) {
			super.onPostExecute(result);
			if (result != null) {
				countImage=0;
				new addNewItem().execute();
			} else {
				countImage=0;
			}
		}
	}

	private class addNewItem extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			String ItemName  = null;
			String Lokasi    = null;
			try {
				Deskripsi = URLEncoder.encode(txtMessage.getText().toString().replace("\"", "'"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String formattedDate = df.format(c.getTime());
			Date mDate;
			long timeInMilliseconds = 0;
			try {
				mDate = df.parse(formattedDate);
				timeInMilliseconds = mDate.getTime();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			url = Referensi.url + "/service.php?ct=ADDNEWREPLY&ThreadId=" + ThreadId +
					"&UserId=" + UserId +
					"&Text=" + Deskripsi +
					"&Image=" + jsonArray.toString() +
					"&Datee=" + timeInMilliseconds;

			String hasil = callURL.call(url);
			return hasil;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//pDialog.dismiss();
			txtMessage.setText("");
			imageOne.setImageResource(0);
			imageOne.setImageResource(R.drawable.placeholder);
			sendMessage();
		}
	}

	public void imageOneClick(View view) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
	}

	// When Image is selected from Gallery
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (data.getData()!=null) {
				try {
					String path = "" + data.getData();
					if (path.startsWith("content://")) {
						doFileParseFromGoogle(path, requestCode, data.getData());
					} else {
						Uri selectedImageUri = data.getData();
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inPreferredConfig = Bitmap.Config.ARGB_8888;

						if (requestCode==1) {
							imagepathOne = getPath(selectedImageUri);
							Bitmap bitmap=BitmapFactory.decodeFile(imagepathOne, options);
							imageOne.setImageBitmap(bitmap);
						}
					}
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private void doFileParseFromGoogle(String file, int requestCode, Uri selectedImageUri) throws FileNotFoundException {
		Bitmap bitmap = null;
		InputStream is = null;
		is = getContentResolver().openInputStream(Uri.parse(file));
		bitmap = BitmapFactory.decodeStream(is);

		//Resize image
		Matrix m = new Matrix();
		m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, mMaxWidth, mMaxHeight), Matrix.ScaleToFit.CENTER);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

		if (bitmap == null) {
			Toast.makeText(ForumKomentarActivity.this, "Error", Toast.LENGTH_LONG).show();
		} else {
			if (requestCode==1) {
				imagepathOne = getPath(selectedImageUri);
				imageOne.setImageBitmap(bitmap);
			}
		}
	}

	private HttpEntity doFileUpload() {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = null;
			File file1, file2, file3, file4;
			FileBody bin1, bin2, bin3, bin4;

			MultipartEntity reqEntity = new MultipartEntity();
			if (countImage==1) {
				post  = new HttpPost(Referensi.url+"/UploadOne.php");
				file1 = new File(imagepathOne);
				bin1  = new FileBody(file1);
				jsonArray.put(file1.getName());
				reqEntity.addPart("uploadedfile1", bin1);
			}

			reqEntity.addPart("user", new StringBody("User"));
			post.setEntity(reqEntity);

			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();
			@SuppressWarnings("unused")
			final String response_str = EntityUtils.toString(resEntity);
		} catch (Exception ex) {
			countImage=0;
			Log.e("Debug", "error: " + ex.getMessage(), ex);
		}
		return resEntity;
	}

	public void sendMessage() {
		String mUsername = "";
		try {
			mUsername = URLEncoder.encode(UserName.replace("\"", "'"), "utf-8");
		} catch (UnsupportedEncodingException e) {}

		String url = Referensi.url + "/send_message.php?regId="+GcmId+"&sender="+mUsername+"&message="+Deskripsi;
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				pDialog.dismiss();
				getAllData();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
			}
		});
		queue.add(jsObjRequest);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

}
