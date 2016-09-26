package com.sobattani;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.ImageLoader;
import com.sobattani.Utils.Referensi;
import com.sobattani.Utils.TypeFaceSpan;
import com.sobattani.Utils.callURL;
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
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ForumAddActivity extends AppCompatActivity {
	private Typeface fontUbuntuL, fontUbuntuB;
    private TextView lblUpload, lblTellUs, txtCategori, txtCancel, txtSubmit;
	private EditText txtDescription;
    private Spinner spinCategori;
    private ArrayList<Kategori> categoriList;
	private int categoriId;
    private String UserId="", ItemId="", UserType="";
    private ProgressDialog pDialog;
	private String url = "";
	private ImageView imageOne, imageTwo, imageThree, imageFour;
	private String imagepathOne=null, imagepathTwo=null, imagepathThree=null, imagepathFour=null;
    private JSONArray jsonArray = new JSONArray();
    @SuppressWarnings("unused")
	private long totalSize = 0;
    private HttpEntity resEntity;
    private int countImage=0;
    private int mMaxWidth  = 480;
    private int mMaxHeight = 480;
	private RequestQueue queue;
	private SharedPreferences sobatTaniPref;
	private ImageLoader mImageLoader;
	private JSONArray jArrImages = null;
	private Toolbar toolbar;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activityslideup, R.anim.no_anim);
		setContentView(R.layout.activity_forum_add);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		initToolbar();

		sobatTaniPref 		 = getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
		queue 		 		 = Volley.newRequestQueue(this);
		mImageLoader         = new ImageLoader(this);
		fontUbuntuL      	 = FontCache.get(ForumAddActivity.this, "DroidSans");
		fontUbuntuB      	 = FontCache.get(ForumAddActivity.this, "DroidSans-Bold");
		lblUpload        	 = (TextView) findViewById(R.id.lblUpload);
		lblTellUs        	 = (TextView) findViewById(R.id.lblTellUs);
		txtDescription       = (EditText) findViewById(R.id.txtDescription);
		txtCategori			 = (TextView) findViewById(R.id.txtCategori);
		txtCancel     		 = (TextView) findViewById(R.id.txtCancel);
		txtSubmit     		 = (TextView) findViewById(R.id.txtSubmit);
		spinCategori	     = (Spinner) findViewById(R.id.spinCategori);
		imageOne      		 = (ImageView) findViewById(R.id.imageOne);
		imageTwo      		 = (ImageView) findViewById(R.id.imageTwo);
		imageThree    		 = (ImageView) findViewById(R.id.imageThree);
		imageFour     		 = (ImageView) findViewById(R.id.imageFour);
		UserId       		 = sobatTaniPref.getString("UserId", "");
		UserType			 = sobatTaniPref.getString("UserType", "");

		txtCancel.setTypeface(fontUbuntuB); 
		txtSubmit.setTypeface(fontUbuntuB);
		lblUpload.setTypeface(fontUbuntuB);
		lblTellUs.setTypeface(fontUbuntuB);
		txtCategori.setTypeface(fontUbuntuL);
		txtDescription.setTypeface(fontUbuntuL);

		pDialog = new ProgressDialog(ForumAddActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

		txtCategori.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				txtCategori.setVisibility(View.GONE);
				spinCategori.setVisibility(View.VISIBLE);
				spinCategori.performClick();
			}
		});
		
		getCategoriSpinner();

		if (getIntent().getExtras()!=null) {
			try {
				jArrImages = new JSONArray(getIntent().getExtras().getString("ItemPicture"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			txtDescription.setText(getIntent().getExtras().getString("ItemDeskripsi"));
			ItemId = getIntent().getExtras().getString("ItemId");

			for (int i=0; i<jArrImages.length(); i++) {
				new downloading(i).execute();
			}
		}
    }

	private void initToolbar() {
		SpannableString spanToolbar = new SpannableString(getString(R.string.menu_tanya_sobat));
		spanToolbar.setSpan(new TypeFaceSpan(ForumAddActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

	private class downloading extends AsyncTask<String, Void, String> {
		int i;
		public downloading(int mI) {
			super();
			i=mI;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			downloadFile(Referensi.url+"/pictures/"+jArrImages.optString(i), i);
			return "";
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();
		}
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
				Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
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

		ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(ForumAddActivity.this, R.layout.spinnerlayout, lables) {
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
		spinCategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				categoriId = categoriList.get(position).getId();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		spinCategori.setEnabled(true);

		if (getIntent().getExtras()!=null) {
			for (int a = 0; a < categoriList.size(); a++) {
				if (getIntent().getExtras().getString("ItemKategori").equalsIgnoreCase(categoriList.get(a).getName())) {
					spinCategori.setSelection(a);
				}
			}
			txtCategori.setVisibility(View.GONE);
			spinCategori.setVisibility(View.VISIBLE);
		}
    }

	public void btnCancelClick(View v) {
		finish();
	}
	
	public void btnSubmitClick(View v) {
		if (txtDescription.getText().toString().equalsIgnoreCase("")) {
			txtDescription.setError("Deskripsi tidak boleh kosong!");
		} else if (txtCategori.getVisibility()==View.VISIBLE) {
			Toast.makeText(ForumAddActivity.this, "Silahkan pilih kategori", Toast.LENGTH_SHORT).show();
		} else {
			if (imagepathOne!=null) {
				countImage = countImage+1;
			} if (imagepathTwo!=null) {
				countImage = countImage+1;
			} if (imagepathThree!=null) {
				countImage = countImage+1;
			} if (imagepathFour!=null) {
				countImage = countImage+1;
			}

			if (countImage==0) {
				new addNewItem().execute();
			} else {
				new UploadImage().execute();
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
			String Deskripsi = null;
			String Lokasi    = null;
			try {
				Deskripsi = URLEncoder.encode(txtDescription.getText().toString().replace("\"", "'"), "utf-8");
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

			url = Referensi.url + "/service.php?ct=ADDNEWFORUM&Categori=" + categoriId +
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
            pDialog.dismiss();
            if (result.equalsIgnoreCase("true")) {
            	AlertDialog.Builder alertDialog = new AlertDialog.Builder(ForumAddActivity.this);
                alertDialog.setTitle("Success");
                alertDialog.setMessage("Add new topik succesfully!");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                    	dialog.cancel();
                    	setResult(RESULT_OK);
                    	finish();
                    }
                });
                if (!isFinishing()) { alertDialog.show(); }
            } else {
            	AlertDialog.Builder alertDialog = new AlertDialog.Builder(ForumAddActivity.this);
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Add new topik error! Please try again or close apps and open again.");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                    	dialog.cancel();
                    }
                });
                if (!isFinishing()) { alertDialog.show(); }
            }
        }
    }
	
	public void imageOneClick(View view) {
		Intent intent = new Intent(); 
		intent.setType("image/*"); 
		intent.setAction(Intent.ACTION_GET_CONTENT); 
		startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1); 
    }
	
	public void imageTwoClick(View view) {
		Intent intent = new Intent(); 
		intent.setType("image/*"); 
		intent.setAction(Intent.ACTION_GET_CONTENT); 
		startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2); 
    }
	
	public void imageThreeClick(View view) {
		Intent intent = new Intent(); 
		intent.setType("image/*"); 
		intent.setAction(Intent.ACTION_GET_CONTENT); 
		startActivityForResult(Intent.createChooser(intent, "Complete action using"), 3); 
    }
	
	public void imageFourClick(View view) {
		Intent intent = new Intent(); 
		intent.setType("image/*"); 
		intent.setAction(Intent.ACTION_GET_CONTENT); 
		startActivityForResult(Intent.createChooser(intent, "Complete action using"), 4); 
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
    		            } else if (requestCode==2) {
    		            	imagepathTwo = getPath(selectedImageUri);
	    		            Bitmap bitmap=BitmapFactory.decodeFile(imagepathTwo, options);
	    		            imageTwo.setImageBitmap(bitmap);
    		            } else if (requestCode==3) {
    		            	imagepathThree = getPath(selectedImageUri);
	    		            Bitmap bitmap=BitmapFactory.decodeFile(imagepathThree, options);
	    		            imageThree.setImageBitmap(bitmap);
    		            } else if (requestCode==4) {
    		            	imagepathFour = getPath(selectedImageUri);
	    		            Bitmap bitmap=BitmapFactory.decodeFile(imagepathFour, options);
	    		            imageFour.setImageBitmap(bitmap);
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
        	Toast.makeText(ForumAddActivity.this, "Error", Toast.LENGTH_LONG).show();
        } else {
        	if (requestCode==1) {
		        imagepathOne = getPath(selectedImageUri);
        		imageOne.setImageBitmap(bitmap);
        	} else if (requestCode==2) {
		        imagepathTwo = getPath(selectedImageUri);
        		imageTwo.setImageBitmap(bitmap);
        	} else if (requestCode==3) {
		        imagepathThree = getPath(selectedImageUri);
        		imageThree.setImageBitmap(bitmap);
        	} else if (requestCode==4) {
		        imagepathFour = getPath(selectedImageUri);
        		imageFour.setImageBitmap(bitmap);
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
             } else if (countImage==2) {
            	 post  = new HttpPost(Referensi.url+"/UploadTwo.php");
            	 file1 = new File(imagepathOne);
            	 file2 = new File(imagepathTwo);
            	 bin1  = new FileBody(file1);
            	 bin2  = new FileBody(file2);
            	 jsonArray.put(file1.getName());
            	 jsonArray.put(file2.getName());
            	 reqEntity.addPart("uploadedfile1", bin1);
            	 reqEntity.addPart("uploadedfile2", bin2);
             } else if (countImage==3) {
            	 post  = new HttpPost(Referensi.url+"/UploadThree.php");
            	 file1 = new File(imagepathOne);
            	 file2 = new File(imagepathTwo);
            	 file3 = new File(imagepathThree);
            	 bin1  = new FileBody(file1);
            	 bin2  = new FileBody(file2);
            	 bin3  = new FileBody(file3);
            	 jsonArray.put(file1.getName());
            	 jsonArray.put(file2.getName());
            	 jsonArray.put(file3.getName());
            	 reqEntity.addPart("uploadedfile1", bin1);
            	 reqEntity.addPart("uploadedfile2", bin2);
            	 reqEntity.addPart("uploadedfile3", bin3);
             } else if (countImage==4) {
            	 post  = new HttpPost(Referensi.url+"/UploadFour.php");
            	 file1 = new File(imagepathOne);
            	 file2 = new File(imagepathTwo);
            	 file3 = new File(imagepathThree);
            	 file4 = new File(imagepathFour);
            	 bin1  = new FileBody(file1);
            	 bin2  = new FileBody(file2);
            	 bin3  = new FileBody(file3);
            	 bin4  = new FileBody(file4);
            	 jsonArray.put(file1.getName());
            	 jsonArray.put(file2.getName());
            	 jsonArray.put(file3.getName());
            	 jsonArray.put(file4.getName());
            	 reqEntity.addPart("uploadedfile1", bin1);
            	 reqEntity.addPart("uploadedfile2", bin2);
            	 reqEntity.addPart("uploadedfile3", bin3);
            	 reqEntity.addPart("uploadedfile4", bin4);
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

	public void downloadFile(String fileUrl, int i) {
		URL myFileUrl = null;

		try {
			myFileUrl = new URL(fileUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			uploadImageFromServer(BitmapFactory.decodeStream(is), fileUrl, i);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
		String path = "";
		boolean imageSaved = false;
		ContentValues values = new ContentValues(3);

		if (inImage != null && !inImage.isRecycled()) {
			File storagePath = new File(Environment.getExternalStorageDirectory() + "/SobatTani/Pictures/");
			storagePath.mkdirs();

			Random generator = new Random();
			int n = 10000;
			n = generator.nextInt(n);
			String fname = "sobattani-" + n + ".jpg";
			FileOutputStream out = null;
			File imageFile = new File(storagePath, fname);
			try {
				out = new FileOutputStream(imageFile);
				imageSaved = inImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
			} catch (Exception e) {
			} finally {
				if (out != null) {
					try {
						out.flush();
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			values.put(MediaStore.Images.Media.TITLE, "SobatTani");
			values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
			values.put("_data", imageFile.getAbsolutePath());
		}
		return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}

	public void uploadImageFromServer(Bitmap bitmap, String fileUrl, int i) {
		try {
			Uri selectedImage = getImageUri(ForumAddActivity.this, bitmap);
			//String[] filePathColumn = {MediaStore.Images.Media.DATA};
			//Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			//cursor.moveToFirst();

			//int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			//String filePath = cursor.getString(columnIndex);
			//cursor.close();

			//Uri uriFromPath = Uri.fromFile(new File(filePath));
			int loader 		= R.drawable.img_loader;
			if (i==0) {
				mImageLoader.DisplayImage(fileUrl.replace(" ", "%20"), loader, imageOne);
				imagepathOne = getPath(selectedImage);
			} else if (i==1) {
				mImageLoader.DisplayImage(fileUrl.replace(" ", "%20"), loader, imageTwo);
				imagepathTwo = getPath(selectedImage);
			} else if (i==2) {
				mImageLoader.DisplayImage(fileUrl.replace(" ", "%20"), loader, imageThree);
				imagepathThree = getPath(selectedImage);
			} else {
				mImageLoader.DisplayImage(fileUrl.replace(" ", "%20"), loader, imageFour);
				imagepathFour = getPath(selectedImage);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	@Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.no_anim, R.anim.activityslidedown);
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
