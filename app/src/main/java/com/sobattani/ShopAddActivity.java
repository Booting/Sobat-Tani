package com.sobattani;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.Referensi;
import com.sobattani.Utils.TypeFaceSpan;
import com.sobattani.Utils.callURL;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ShopAddActivity extends AppCompatActivity {
	private Typeface fontUbuntuL, fontUbuntuB;
    private TextView lblUpload, lblTellUs, lblOriginalPrice, txtCategori, txtCancel, txtSubmit,
			lblNamaBarang, lblKategori, lblDeskripsi, lblAlamat, lblNoTelp, lblStock;
	private EditText txtItemName, txtDescription, txtOriginalPrice, txtContactNumber, txtStock, txtAddress;
    private Spinner spinCategori;
    private ArrayList<Kategori> categoriList;
	private int categoriId;
    private String UserId="", ItemId="", UserType="";
	private String url = "";
	private ImageView imageOne, imageTwo, imageThree, imageFour;
	private String imagepathOne=null, imagepathTwo=null, imagepathThree=null, imagepathFour=null;
    private JSONArray jsonArray = new JSONArray();
    @SuppressWarnings("unused")
	private long totalSize = 0;
    private int countImage=0;
    private int mMaxWidth  = 480;
    private int mMaxHeight = 480;
	private RequestQueue queue;
	private SharedPreferences sobatTaniPref;
	private JSONArray jArrImages = null;
	private Toolbar toolbar;
	private Cloudinary cloudinary;
	private ArrayList<File> arrayListFile = new ArrayList<>();
	private Dialog dialog;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activityslideup, R.anim.no_anim);
		setContentView(R.layout.activity_shop_add);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Map config = new HashMap();
		config.put("cloud_name", "sobattani");
		config.put("api_key", "513198426757233");
		config.put("api_secret", "0tDv2knaPbNuIQu7ZfWswxB7RF0");
		cloudinary = new Cloudinary(config);

		initToolbar();

		sobatTaniPref 		 = getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
		queue 		 		 = Volley.newRequestQueue(this);
		fontUbuntuL      	 = FontCache.get(ShopAddActivity.this, "DroidSans");
		fontUbuntuB      	 = FontCache.get(ShopAddActivity.this, "DroidSans-Bold");
		lblUpload        	 = (TextView) findViewById(R.id.lblUpload);
		lblTellUs        	 = (TextView) findViewById(R.id.lblTellUs);
		lblOriginalPrice 	 = (TextView) findViewById(R.id.lblOriginalPrice);
		txtItemName      	 = (EditText) findViewById(R.id.txtItemName);
		txtDescription       = (EditText) findViewById(R.id.txtDescription);
		txtOriginalPrice     = (EditText) findViewById(R.id.txtOriginalPrice);
		txtContactNumber     = (EditText) findViewById(R.id.txtContactNumber);
		txtStock			 = (EditText) findViewById(R.id.txtStock);
		txtAddress           = (EditText) findViewById(R.id.txtAddress);
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
		lblNamaBarang		 = (TextView) findViewById(R.id.lblNamaBarang);
		lblKategori			 = (TextView) findViewById(R.id.lblKategori);
		lblDeskripsi		 = (TextView) findViewById(R.id.lblDeskripsi);
		lblAlamat			 = (TextView) findViewById(R.id.lblAlamat);
		lblNoTelp			 = (TextView) findViewById(R.id.lblNoTelp);
		lblStock			 = (TextView) findViewById(R.id.lblStock);

		txtCancel.setTypeface(fontUbuntuB); 
		txtSubmit.setTypeface(fontUbuntuB);
		lblUpload.setTypeface(fontUbuntuB);
		lblTellUs.setTypeface(fontUbuntuB);
		txtCategori.setTypeface(fontUbuntuL);
		lblOriginalPrice.setTypeface(fontUbuntuB);
		txtItemName.setTypeface(fontUbuntuL);
		txtDescription.setTypeface(fontUbuntuL);
		txtOriginalPrice.setTypeface(fontUbuntuL);
		txtContactNumber.setTypeface(fontUbuntuL);
		txtStock.setTypeface(fontUbuntuL);
		txtAddress.setTypeface(fontUbuntuL);
		lblNamaBarang.setTypeface(fontUbuntuB);
		lblKategori.setTypeface(fontUbuntuB);
		lblDeskripsi.setTypeface(fontUbuntuB);
		lblAlamat.setTypeface(fontUbuntuB);
		lblNoTelp.setTypeface(fontUbuntuB);
		lblStock.setTypeface(fontUbuntuB);

		showDialog();
		txtCategori.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				txtCategori.setVisibility(View.GONE);
				spinCategori.setVisibility(View.VISIBLE);
				spinCategori.performClick();
			}
		});
		txtOriginalPrice.addTextChangedListener(new CurrencyTextWatcher());
		
		getCategoriSpinner();

		if (getIntent().getExtras()!=null) {
			try {
				jArrImages = new JSONArray(getIntent().getExtras().getString("ItemPicture"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			txtItemName.setText(getIntent().getExtras().getString("ItemName"));
			txtDescription.setText(getIntent().getExtras().getString("ItemDeskripsi"));
			txtOriginalPrice.setText(getIntent().getExtras().getString("HargaDiskon").replace("Rp ", "").replace(".", ""));
			txtContactNumber.setText(getIntent().getExtras().getString("ItemContact"));
			txtAddress.setText(getIntent().getExtras().getString("ItemLocation"));
			txtStock.setText(getIntent().getExtras().getString("NoRekening"));
			ItemId = getIntent().getExtras().getString("ItemId");

			for (int i=0; i<jArrImages.length(); i++) {
				new downloading(i).execute();
				//downloadFile(Referensi.url+"/pictures/"+jArrImages.optString(i), i);
			}
		}
    }

	class CurrencyTextWatcher implements TextWatcher {
		boolean mEditing;
		public CurrencyTextWatcher() {
			mEditing = false;
		}
		public synchronized void afterTextChanged(Editable s) {
			if(!mEditing) {
				mEditing = true;
				if (s.length()!=0) {
					txtOriginalPrice.setText(Referensi.currencyFormater(Double.parseDouble(s.toString().replace(",", ""))));
					txtOriginalPrice.setSelection(txtOriginalPrice.getText().length());
				}
				mEditing = false;
			}
		}
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
	}

	private void initToolbar() {
		SpannableString spanToolbar = new SpannableString(getString(R.string.form_pasar_sobat));
		spanToolbar.setSpan(new TypeFaceSpan(ShopAddActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
			try { dialog.show(); } catch (Exception e) {}
		}
		@Override
		protected String doInBackground(String... params) {
			downloadFile(Referensi.url+"/pictures/"+jArrImages.optString(i), i);
			return "";
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try { dialog.dismiss(); } catch (Exception e) {}
		}
	}

	public void getCategoriSpinner() {
		categoriList = new ArrayList<Kategori>();
		String url;
		if (UserType.equalsIgnoreCase("1")) {
			url = Referensi.url + "/getTanaman.php";
		} else {
			url = Referensi.url + "/getKategoriProduk.php";
		}

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response!=null) {
						JSONArray categories = response.getJSONArray("categories");
						for (int i = 0; i < categories.length(); i++) {
							JSONObject catObj = (JSONObject) categories.get(i);
							Kategori cat = null;
							if (UserType.equalsIgnoreCase("1")) {
								cat = new Kategori(catObj.getInt("TanamanId"), catObj.getString("TanamanName"));
							} else {
								cat = new Kategori(catObj.getInt("ProdukCategoriId"), catObj.getString("ProdukCategoriName"));
							}
							categoriList.add(cat);
						}
						populateSpinnerCategori();
					}
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
	
	/**
     * Adding spinner data
     * */
    private void populateSpinnerCategori() {
        List<String> lables  = new ArrayList<String>();
        categoriId = 0;

        for (int i = 0; i < categoriList.size(); i++) {
            lables.add(categoriList.get(i).getName());
        }

		ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(ShopAddActivity.this, R.layout.spinnerlayout, lables) {
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
		if (imagepathOne==null && imagepathTwo==null && imagepathThree==null && imagepathFour==null) {
			Toast.makeText(getBaseContext(), "Gambar tidak boleh kosong!", Toast.LENGTH_SHORT).show();
		} else if (txtItemName.getText().toString().equalsIgnoreCase("")) {
			txtItemName.setError("Nama Barang tidak boleh kosong!");
		} else if (txtDescription.getText().toString().equalsIgnoreCase("")) {
			txtDescription.setError("Deskripsi tidak boleh kosong!");
		} else if (txtOriginalPrice.getText().toString().equalsIgnoreCase("")) {
			txtOriginalPrice.setError("Harga Barang tidak boleh kosong!");
		} else if (txtContactNumber.getText().toString().equalsIgnoreCase("")) {
			txtContactNumber.setError("Nomor Telepon tidak boleh kosong!");
		} else if (txtStock.getText().toString().equalsIgnoreCase("")) {
			txtStock.setError("Jumlah Barang tidak boleh kosong!!");
		} else if (txtAddress.getText().toString().equalsIgnoreCase("")) {
			txtAddress.setError("Alamat tidak boleh kosong!");
		} else if (txtCategori.getVisibility()==View.VISIBLE) {
			Toast.makeText(ShopAddActivity.this, "Silahkan pilih kategori", Toast.LENGTH_SHORT).show();
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

			try { dialog.show(); } catch (Exception e) {}
			for (int i=0; i<arrayListFile.size(); i++) {
				new Upload(cloudinary, arrayListFile.get(i), i).execute();
			}
		}
	}

	private class addNewItem extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
        	String ItemName  = null;
			String Deskripsi = null;
			String Lokasi    = null;
			try {
				ItemName  = URLEncoder.encode(txtItemName.getText().toString().replace("\"", "'"), "utf-8");
				Deskripsi = URLEncoder.encode(txtDescription.getText().toString().replace("\"", "'"), "utf-8");
				Lokasi    = URLEncoder.encode(txtAddress.getText().toString().replace("\"", "'"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (getIntent().getExtras()==null) {
				url = Referensi.url + "/service.php?ct=ADDNEWITEM&SellerId=" + UserId +
						"&CategoriId=" + categoriId +
						"&ItemName=" + ItemName +
						"&Deskripsi=" + Deskripsi +
						"&HargaAsli=" + txtOriginalPrice.getText().toString() +
						"&Lokasi=" + Lokasi +
						"&Contact=" + txtContactNumber.getText().toString() +
						"&Stock=" + txtStock.getText().toString() +
						"&Picture=" + jsonArray.toString();
			} else {
				url = Referensi.url + "/service.php?ct=UPDATEITEM&&CategoriId=" + categoriId +
						"&ItemName=" + ItemName +
						"&Deskripsi=" + Deskripsi +
						"&HargaAsli=" + txtOriginalPrice.getText().toString() +
						"&Lokasi=" + Lokasi +
						"&Contact=" + txtContactNumber.getText().toString() +
						"&Stock=" + txtStock.getText().toString() +
						"&Picture=" + jsonArray.toString() +
						"&ItemId=" + ItemId;
			}
        	String hasil = callURL.call(url);
        	return hasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
			try { dialog.dismiss(); } catch (Exception e) {}
			if (result.equalsIgnoreCase("true")) {
				Toast.makeText(ShopAddActivity.this, "Add new item succesfully!", Toast.LENGTH_LONG).show();
				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(ShopAddActivity.this, "Add new item error! Please try again or close apps and open again.", Toast.LENGTH_LONG).show();
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
							// Get image path
	    		            imagepathOne = getPath(selectedImageUri);
							addFileToArrayList(imagepathOne, 0);

							// Set image to image view
	    		            Bitmap bitmap=BitmapFactory.decodeFile(imagepathOne, options);
	    		            imageOne.setImageBitmap(bitmap);
    		            } else if (requestCode==2) {
							// Get image path
    		            	imagepathTwo = getPath(selectedImageUri);
							addFileToArrayList(imagepathTwo, 1);

							// Set image to image view
	    		            Bitmap bitmap=BitmapFactory.decodeFile(imagepathTwo, options);
	    		            imageTwo.setImageBitmap(bitmap);
    		            } else if (requestCode==3) {
							// Get image path
    		            	imagepathThree = getPath(selectedImageUri);
							addFileToArrayList(imagepathThree, 2);

							// Set image to image view
	    		            Bitmap bitmap=BitmapFactory.decodeFile(imagepathThree, options);
	    		            imageThree.setImageBitmap(bitmap);
    		            } else if (requestCode==4) {
							// Get image path
    		            	imagepathFour = getPath(selectedImageUri);
							addFileToArrayList(imagepathFour, 2);

							// Set image to image view
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
        	Toast.makeText(ShopAddActivity.this, "Error", Toast.LENGTH_LONG).show();
        } else {
        	if (requestCode==1) {
				// Get image path
		        imagepathOne = getPath(selectedImageUri);
				addFileToArrayList(imagepathOne, 0);

				// Set image to image view
        		imageOne.setImageBitmap(bitmap);
        	} else if (requestCode==2) {
				// Get image path
		        imagepathTwo = getPath(selectedImageUri);
				addFileToArrayList(imagepathTwo, 1);

				// Set image to image view
        		imageTwo.setImageBitmap(bitmap);
        	} else if (requestCode==3) {
				// Get image path
		        imagepathThree = getPath(selectedImageUri);
				addFileToArrayList(imagepathThree, 2);

				// Set image to image view
        		imageThree.setImageBitmap(bitmap);
        	} else if (requestCode==4) {
				// Get image path
		        imagepathFour = getPath(selectedImageUri);
				addFileToArrayList(imagepathFour, 3);

				// Set image to image view
        		imageFour.setImageBitmap(bitmap);
        	}
        }
    }

	private class Upload extends AsyncTask<String, Void, String> {
		private Cloudinary mCloudinary;
		private File mFile;
		private int mPosition;

		public Upload(Cloudinary cloudinary, File file, int position) {
			super();
			mCloudinary = cloudinary;
			mFile       = file;
			mPosition   = position;
		}

		@Override
		protected String doInBackground(String... urls) {
			JSONObject result = null;
			try {
				result = new JSONObject(mCloudinary.uploader().upload(mFile, ObjectUtils.asMap("transformation",
						new Transformation().width(850).height(850).crop("limit"))));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result.toString();
		}

		@Override
		protected void onPostExecute(String strResult) {
			try {
				JSONObject jsonObject = new JSONObject(strResult);
				String strFileName    = jsonObject.getString("url").substring(jsonObject.getString("url").lastIndexOf('/') + 1);
				jsonArray.put(strFileName);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (mPosition==(arrayListFile.size()-1)) {
				countImage=0;
				new addNewItem().execute();
			}
		}
	}

	public void addFileToArrayList(String strPath, int intPosition) {
		if (arrayListFile.size()==0) {
			arrayListFile.add(new File(strPath));
		} else {
			try {
				if (arrayListFile.get(intPosition) != null) {
					arrayListFile.add(new File(strPath));
				} else {
					arrayListFile.set(intPosition, new File(strPath));
				}
			} catch (IndexOutOfBoundsException e) {
				arrayListFile.add(new File(strPath));
			}
		}
	}

	private void showDialog() {
		dialog = new Dialog(ShopAddActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_dialog);
		dialog.setCancelable(false);

		TextView lblTitle = (TextView) dialog.findViewById(R.id.lblTitle);
		lblTitle.setTypeface(fontUbuntuL);

		try {
			dialog.show();
		} catch (Exception e) {}
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
			Uri selectedImage = getImageUri(ShopAddActivity.this, bitmap);
			//String[] filePathColumn = {MediaStore.Images.Media.DATA};
			//Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			//cursor.moveToFirst();

			//int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			//String filePath = cursor.getString(columnIndex);
			//cursor.close();

			//Uri uriFromPath = Uri.fromFile(new File(filePath));
			if (i==0) {
				Glide.with(this).load(fileUrl).placeholder(R.drawable.img_loader).centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300).dontAnimate().into(imageOne);
				imagepathOne = getPath(selectedImage);
			} else if (i==1) {
				Glide.with(this).load(fileUrl).placeholder(R.drawable.img_loader).centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300).dontAnimate().into(imageTwo);
				imagepathTwo = getPath(selectedImage);
			} else if (i==2) {
				Glide.with(this).load(fileUrl).placeholder(R.drawable.img_loader).centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300).dontAnimate().into(imageThree);
				imagepathThree = getPath(selectedImage);
			} else {
				Glide.with(this).load(fileUrl).placeholder(R.drawable.img_loader).centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300).dontAnimate().into(imageFour);
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
