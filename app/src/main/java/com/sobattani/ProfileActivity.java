package com.sobattani;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sobattani.Utils.CircleImageView;
import com.sobattani.Utils.FontCache;
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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private SharedPreferences sobatTaniPref;
    private ProgressBar progressBar;
    private Typeface fontDroidSandBold, fontDroidSand;
    private CircleImageView imgProfile;
    private TextView lblUploadPhotos, lblName, lblEmail, lblUsername, lblStatus, lblAlamat, lblNoTelp, lblKategori, lblTanaman, lblHamaPenyakit, lblNamaPerusahaan,
            lblKategoriProduk, txtSimpan, lblView;
    private EditText editUsername, editStatus, editAlamat, editNoTelp;
    private String UserId, UserName, Email, userCategori, tanaman, hamaPenyakit, namaPerusahaan, kategoriProduk;
    private int idUserCategori, idTanaman, idHamaPenyakit, idPerusahaan, idKategoriProduk;
    private Spinner spinKategori, spinTanaman, spinHamaPenyakit, spinNamaPerusahaan, spinKategoriProduk;
    private RequestQueue queue;
    private ArrayList<Kategori> userCategoriList, tanamanList, hamaPenyakitList, namaPerusahaanList, kategoriProdukList;
    private RelativeLayout relTanaman, relhamaPenyakit, relNamaPerusahaan, relKategoriProduk;
    private ProgressDialog pDialog;
    private JSONArray str_login  = null;
    private ImageView imageOne, imageTwo, imageThree, imageFour;
    private String imagepathOne=null, imagepathTwo=null, imagepathThree=null, imagepathFour=null;
    private String pathOne="", pathTwo="", pathThree="", pathFour="";
    private HttpEntity resEntity;
    private int countImage=0;
    private String oldPictures="";
    private String[] splitPic;
    private int mMaxWidth  = 480;
    private int mMaxHeight = 480;
    private Toolbar toolbar;
    private LinearLayout linPetani, linAgronomis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_profile);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initToolbar();

        queue    	       = Volley.newRequestQueue(this);
        sobatTaniPref      = getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
        progressBar        = (ProgressBar) findViewById(R.id.progressBusy);
        UserId             = sobatTaniPref.getString("UserId", "");
        UserName           = sobatTaniPref.getString("UserName", "");
        Email              = sobatTaniPref.getString("Email", "");
        fontDroidSandBold  = FontCache.get(this, "DroidSans-Bold");
        fontDroidSand      = FontCache.get(this, "DroidSans");
        imgProfile         = (CircleImageView) findViewById(R.id.imgProfile);
        lblUploadPhotos    = (TextView) findViewById(R.id.lblUploadPhotos);
        lblName            = (TextView) findViewById(R.id.lblName);
        lblEmail           = (TextView) findViewById(R.id.lblEmail);
        lblUsername        = (TextView) findViewById(R.id.lblUsername);
        lblAlamat          = (TextView) findViewById(R.id.lblAlamat);
        lblNoTelp          = (TextView) findViewById(R.id.lblNoTelp);
        lblKategori        = (TextView) findViewById(R.id.lblKategori);
        lblTanaman         = (TextView) findViewById(R.id.lblTanaman);
        lblHamaPenyakit    = (TextView) findViewById(R.id.lblHamaPenyakit);
        lblNamaPerusahaan  = (TextView) findViewById(R.id.lblNamaPerusahaan);
        lblKategoriProduk  = (TextView) findViewById(R.id.lblKategoriProduk);
        editUsername       = (EditText) findViewById(R.id.editUsername);
        editAlamat         = (EditText) findViewById(R.id.editAlamat);
        editNoTelp         = (EditText) findViewById(R.id.editNoTelp);
        spinKategori       = (Spinner) findViewById(R.id.spinKategori);
        spinTanaman        = (Spinner) findViewById(R.id.spinTanaman);
        spinHamaPenyakit   = (Spinner) findViewById(R.id.spinHamaPenyakit);
        spinNamaPerusahaan = (Spinner) findViewById(R.id.spinNamaPerusahaan);
        spinKategoriProduk = (Spinner) findViewById(R.id.spinKategoriProduk);
        relTanaman         = (RelativeLayout) findViewById(R.id.relTanaman);
        relhamaPenyakit    = (RelativeLayout) findViewById(R.id.relhamaPenyakit);
        relNamaPerusahaan  = (RelativeLayout) findViewById(R.id.relNamaPerusahaan);
        relKategoriProduk  = (RelativeLayout) findViewById(R.id.relKategoriProduk);
        txtSimpan          = (TextView) findViewById(R.id.txtSimpan);
        imageOne	       = (ImageView) findViewById(R.id.imageOne);
        imageTwo	       = (ImageView) findViewById(R.id.imageTwo);
        imageThree         = (ImageView) findViewById(R.id.imageThree);
        imageFour          = (ImageView) findViewById(R.id.imageFour);
        lblView            = (TextView) findViewById(R.id.lblView);
        linPetani          = (LinearLayout) findViewById(R.id.linPetani);
        linAgronomis       = (LinearLayout) findViewById(R.id.linAgronomis);
        lblStatus          = (TextView) findViewById(R.id.lblStatus);
        editStatus         = (EditText) findViewById(R.id.editStatus);

        spinKategori.setOnItemSelectedListener(this);
        spinTanaman.setOnItemSelectedListener(this);
        spinHamaPenyakit.setOnItemSelectedListener(this);
        spinNamaPerusahaan.setOnItemSelectedListener(this);
        spinKategoriProduk.setOnItemSelectedListener(this);
        lblUploadPhotos.setTypeface(fontDroidSandBold);
        lblName.setTypeface(fontDroidSandBold);
        lblEmail.setTypeface(fontDroidSand);
        lblUsername.setTypeface(fontDroidSandBold);
        lblStatus.setTypeface(fontDroidSandBold);
        lblAlamat.setTypeface(fontDroidSandBold);
        lblNoTelp.setTypeface(fontDroidSandBold);
        lblKategori.setTypeface(fontDroidSandBold);
        lblTanaman.setTypeface(fontDroidSandBold);
        lblHamaPenyakit.setTypeface(fontDroidSandBold);
        lblNamaPerusahaan.setTypeface(fontDroidSandBold);
        lblKategoriProduk.setTypeface(fontDroidSandBold);
        editUsername.setTypeface(fontDroidSand);
        editStatus.setTypeface(fontDroidSand);
        editAlamat.setTypeface(fontDroidSand);
        editNoTelp.setTypeface(fontDroidSand);
        txtSimpan.setTypeface(fontDroidSandBold);
        lblView.setTypeface(fontDroidSand);

        lblEmail.setText(Email);
        Glide.with(this).load("https://graph.facebook.com/" + UserId + "/picture?type=large")
                .placeholder(R.drawable.img_loader).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                .dontAnimate().into(imgProfile);

        pDialog = new ProgressDialog(ProfileActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        getUserCategoriSpinner();

        txtSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editAlamat.getText().toString().equalsIgnoreCase("")) {
                    editAlamat.setError("Alamat tidak boleh kosong!");
                } else if (editNoTelp.getText().toString().equalsIgnoreCase("")) {
                    editNoTelp.setError("No Telp tidak boleh kosong!");
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
                        new updateUserDetail().execute();
                    } else {
                        new UploadImage().execute();
                    }
                }
            }
        });

        lblView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ShowProfileActivity.class);
                intent.putExtra("UserId", sobatTaniPref.getString("UserId", ""));
                startActivity(intent);
            }
        });
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString(getString(R.string.menu_data_sobat));
        spanToolbar.setSpan(new TypeFaceSpan(ProfileActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    public void getUserCategoriSpinner() {
        pDialog.show();

        String url = Referensi.url+"/getUserCategori.php";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray categories = response.getJSONArray("categories");

                    userCategoriList = new ArrayList<Kategori>();
                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject catObj = (JSONObject) categories.get(i);
                        Kategori cat = new Kategori(catObj.getInt("UserTypeId"), catObj.getString("UserTypeName"));
                        userCategoriList.add(cat);
                    }
                    populateSpinnerUserCategori();
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

    private void populateSpinnerUserCategori() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < userCategoriList.size(); i++) {
            lables.add(userCategoriList.get(i).getName());
        }

        // Creating adapter for spinner
        spinKategori.setAdapter(null);
        ArrayAdapter<String> spinnerAdapter  = new ArrayAdapter<String>(this, R.layout.spinner_item, lables);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinKategori.setAdapter(spinnerAdapter);

        getTanamanSpinner();
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
        getHamaPenyakitSpinner();
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
        getNamaPerusahaanSpinner();
    }

    public void getNamaPerusahaanSpinner() {
        String url = Referensi.url+"/getNamaPerusahaan.php";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray categories = response.getJSONArray("categories");

                    namaPerusahaanList = new ArrayList<Kategori>();
                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject catObj = (JSONObject) categories.get(i);
                        Kategori cat = new Kategori(catObj.getInt("AgronomisId"), catObj.getString("AgronomisName"));
                        namaPerusahaanList.add(cat);
                    }
                    populateSpinnerNamaPerusahaan();
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

    private void populateSpinnerNamaPerusahaan() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < namaPerusahaanList.size(); i++) {
            lables.add(namaPerusahaanList.get(i).getName());
        }

        // Creating adapter for spinner
        spinNamaPerusahaan.setAdapter(null);
        ArrayAdapter<String> spinnerAdapter  = new ArrayAdapter<String>(this, R.layout.spinner_item, lables);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinNamaPerusahaan.setAdapter(spinnerAdapter);
        getKategoriProdukSpinner();
    }

    public void getKategoriProdukSpinner() {
        String url = Referensi.url+"/getKategoriProduk.php";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray categories = response.getJSONArray("categories");

                    kategoriProdukList = new ArrayList<Kategori>();
                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject catObj = (JSONObject) categories.get(i);
                        Kategori cat = new Kategori(catObj.getInt("ProdukCategoriId"), catObj.getString("ProdukCategoriName"));
                        kategoriProdukList.add(cat);
                    }
                    populateSpinnerKategoriProduk();
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

    private void populateSpinnerKategoriProduk() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < kategoriProdukList.size(); i++) {
            lables.add(kategoriProdukList.get(i).getName());
        }

        // Creating adapter for spinner
        spinKategoriProduk.setAdapter(null);
        ArrayAdapter<String> spinnerAdapter  = new ArrayAdapter<String>(this, R.layout.spinner_item, lables);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinKategoriProduk.setAdapter(spinnerAdapter);
        getUserDetail();
    }

    public void getUserDetail() {
        String url = Referensi.url+"/getUserDetail.php?UserId="+UserId;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    str_login  = response.getJSONArray("info");
                    for (int i = 0; i < str_login.length(); i++){
                        JSONObject ar = str_login.getJSONObject(i);

                        setSpinnerKategoriUser(ar.getString("UserType"));

                        if (!ar.getString("Tanaman").isEmpty()) {
                            for (int a = 0; a < tanamanList.size(); a++) {
                                if (ar.getInt("Tanaman")==tanamanList.get(a).getId()) {
                                    spinTanaman.setSelection(a);
                                }
                            }
                        }

                        if (!ar.getString("HamaDanPenyakit").isEmpty()) {
                            for (int a = 0; a < hamaPenyakitList.size(); a++) {
                                if (ar.getInt("HamaDanPenyakit")==hamaPenyakitList.get(a).getId()) {
                                    spinHamaPenyakit.setSelection(a);
                                }
                            }
                        }

                        if (!ar.getString("NamaPerusahaan").equalsIgnoreCase("")) {
                            for (int a = 0; a < namaPerusahaanList.size(); a++) {
                                if (ar.getInt("NamaPerusahaan")==namaPerusahaanList.get(a).getId()) {
                                    spinNamaPerusahaan.setSelection(a);
                                }
                            }
                        }

                        if (!ar.getString("KategoriProduk").isEmpty()) {
                            for (int a = 0; a < kategoriProdukList.size(); a++) {
                                if (ar.getInt("KategoriProduk")==kategoriProdukList.get(a).getId()) {
                                    spinKategoriProduk.setSelection(a);
                                }
                            }
                        }

                        editUsername.setText(ar.getString("Nama"));
                        editStatus.setText(ar.getString("Description"));
                        lblName.setText(ar.getString("Nama"));

                        if (ar.isNull("Alamat")) {
                            editAlamat.setText("");
                        } else {
                            editAlamat.setText(ar.getString("Alamat"));
                        }

                        if (ar.isNull("NoTelp")) {
                            editNoTelp.setText("");
                        } else {
                            editNoTelp.setText(ar.getString("NoTelp"));
                        }

                        oldPictures = ar.getString("Pictures");
                        splitPic    = ar.getString("Pictures").split(",");

                        if (!oldPictures.equalsIgnoreCase("") || !oldPictures.isEmpty()) {
                            for (int j=0; j<splitPic.length; j++) {
                                if (j==0) {
                                    Glide.with(ProfileActivity.this).load(Referensi.url+"/pictures/"+splitPic[j])
                                            .placeholder(R.drawable.img_loader).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .override(300, 300).dontAnimate().into(imageOne);
                                } else if (j==1) {
                                    Glide.with(ProfileActivity.this).load(Referensi.url+"/pictures/"+splitPic[j])
                                            .placeholder(R.drawable.img_loader).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .override(300, 300).dontAnimate().into(imageTwo);
                                } else if (j==2) {
                                    Glide.with(ProfileActivity.this).load(Referensi.url+"/pictures/"+splitPic[j])
                                            .placeholder(R.drawable.img_loader).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .override(300, 300).dontAnimate().into(imageThree);
                                } else {
                                    Glide.with(ProfileActivity.this).load(Referensi.url+"/pictures/"+splitPic[j])
                                            .placeholder(R.drawable.img_loader).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .override(300, 300).dontAnimate().into(imageFour);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }

    public void setSpinnerKategoriUser(String kategoriUser) {
        switch (kategoriUser) {
            case "1":
                spinKategori.setSelection(0);
                break;
            case "2":
                spinKategori.setSelection(1);
                break;
            case "3":
                spinKategori.setSelection(2);
                break;
            case "4":
                spinKategori.setSelection(3);
                break;
            default:
                spinKategori.setSelection(0);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);

        switch (parent.getId()) {
            case R.id.spinKategori:
                idUserCategori = userCategoriList.get(position).getId();
                userCategori   = userCategoriList.get(position).getName();

                if (userCategori.equalsIgnoreCase("Petani")) {
                    linPetani.setVisibility(View.VISIBLE);
                    linAgronomis.setVisibility(View.GONE);
                } else if (userCategori.equalsIgnoreCase("Toko Pertanian")) {
                    linPetani.setVisibility(View.GONE);
                    linAgronomis.setVisibility(View.GONE);
                } else if (userCategori.equalsIgnoreCase("Agronomis")) {
                    linPetani.setVisibility(View.GONE);
                    linAgronomis.setVisibility(View.VISIBLE);
                } else {
                    linPetani.setVisibility(View.GONE);
                    linAgronomis.setVisibility(View.GONE);
                }

                break;
            case R.id.spinTanaman:
                idTanaman = tanamanList.get(position).getId();
                tanaman   = tanamanList.get(position).getName();
                break;
            case R.id.spinHamaPenyakit:
                idHamaPenyakit = hamaPenyakitList.get(position).getId();
                hamaPenyakit   = hamaPenyakitList.get(position).getName();
                break;
            case R.id.spinNamaPerusahaan:
                idPerusahaan   = namaPerusahaanList.get(position).getId();
                namaPerusahaan = namaPerusahaanList.get(position).getName();
                break;
            case R.id.spinKategoriProduk:
                idKategoriProduk = kategoriProdukList.get(position).getId();
                kategoriProduk   = kategoriProdukList.get(position).getName();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class updateUserDetail extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (splitPic.length!=0) {
                if (imagepathOne!=null) {
                    oldPictures = oldPictures.replace(splitPic[0], pathOne);
                } if (imagepathTwo!=null) {
                    try {
                        oldPictures = oldPictures.replace(splitPic[1], pathTwo);
                    } catch (Exception e) {
                        oldPictures = oldPictures+","+pathTwo;
                    }
                } if (imagepathThree!=null) {
                    try {
                        oldPictures = oldPictures.replace(splitPic[2], pathThree);
                    } catch (Exception e) {
                        oldPictures = oldPictures+","+pathThree;
                    }
                } if (imagepathFour!=null) {
                    try {
                        oldPictures = oldPictures.replace(splitPic[3], pathFour);
                    } catch (Exception e) {
                        oldPictures = oldPictures+","+pathFour;
                    }
                }
            } else {
                if (countImage==1) {
                    oldPictures = pathOne;
                } else if (countImage==2) {
                    oldPictures = pathOne+","+pathTwo;
                } else if (countImage==3) {
                    oldPictures = pathOne+","+pathTwo+","+pathThree;
                } else {
                    oldPictures = pathOne+","+pathTwo+","+pathThree+","+pathFour;
                }
            }
        }
        @Override
        protected String doInBackground(String... params) {
            String url = "";
            try {
                String mNama   = URLEncoder.encode(editUsername.getText().toString().replace("\"", "'"), "utf-8");
                String mDesc   = URLEncoder.encode(editStatus.getText().toString().replace("\"", "'"), "utf-8");
                String mAlamat = URLEncoder.encode(editAlamat.getText().toString().replace("\"", "'"), "utf-8");
                String mNoTelp = URLEncoder.encode(editNoTelp.getText().toString().replace("\"", "'"), "utf-8");

                if (userCategori.equalsIgnoreCase("Petani")) {
                    url = Referensi.url + "/service.php?ct=UPDATEUSERPETANI" +
                            "&UserType="+idUserCategori+
                            "&Nama="+mNama+
                            "&Description="+mDesc+
                            "&Alamat="+mAlamat+
                            "&NoTelp="+mNoTelp+
                            "&Tanaman="+idTanaman +
                            "&HamaDanPenyakit="+idHamaPenyakit+
                            "&Pictures="+oldPictures+
                            "&UserId=" + UserId;
                } /*else if (userCategori.equalsIgnoreCase("Toko Pertanian")) {
                    url = Referensi.url + "/service.php?ct=UPDATEUSERTOKOPERTANIAN" +
                            "&UserType="+idUserCategori+
                            "&Alamat="+mAlamat+
                            "&NoTelp="+mNoTelp+
                            "&KategoriProduk="+idKategoriProduk +
                            "&Pictures="+oldPictures+
                            "&UserId=" + UserId;
                }*/ else if (userCategori.equalsIgnoreCase("Agronomis")) {
                    url = Referensi.url + "/service.php?ct=UPDATEUSERAGRONOMIS" +
                            "&UserType="+idUserCategori+
                            "&Nama="+mNama+
                            "&Description="+mDesc+
                            "&Alamat="+mAlamat+
                            "&NoTelp="+mNoTelp+
                            "&KategoriProduk="+idKategoriProduk +
                            "&NamaPerusahaan="+idPerusahaan +
                            "&UserId=" + UserId;
                } else {
                    url = Referensi.url + "/service.php?ct=UPDATEUSERPPL" +
                            "&UserType="+idUserCategori+
                            "&Nama="+mNama+
                            "&Description="+mDesc+
                            "&Alamat="+mAlamat+
                            "&NoTelp="+mNoTelp+
                            "&Pictures="+oldPictures+
                            "&UserId=" + UserId;
                }
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
            pDialog.dismiss();
            if (result.equalsIgnoreCase("true")) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
                alertDialog.setTitle("Success");
                alertDialog.setMessage("Update data succesfully!");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        setResult(Activity.RESULT_OK);
                        dialog.cancel();
                        SharedPreferences.Editor editor = sobatTaniPref.edit();
                        editor.putString("isAMember", "true");
                        editor.commit();
                    }
                });
                if (!isFinishing()) { alertDialog.show(); }
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Update data error! Please try again or close apps and open again.");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
                if (!isFinishing()) { alertDialog.show(); }
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
                new updateUserDetail().execute();
            } else {
                countImage=0;
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
            } else {
                Toast.makeText(ProfileActivity.this, "Maaf, gambar yang bisa diupload hanya dari Gallery saja!", Toast.LENGTH_LONG).show();
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
            Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_LONG).show();
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
            File file1 = null, file2, file3, file4;
            FileBody bin1 = null, bin2, bin3, bin4;

            MultipartEntity reqEntity = new MultipartEntity();
            if (countImage==1) {
                post = new HttpPost(Referensi.url+"/UploadOne.php");
                if (imagepathOne!=null) {
                    file1     = new File(imagepathOne);
                    pathOne   = file1.getName();
                } else if (imagepathTwo!=null) {
                    file1     = new File(imagepathTwo);
                    pathTwo   = file1.getName();
                } else if (imagepathThree!=null) {
                    file1     = new File(imagepathThree);
                    pathThree = file1.getName();
                } else {
                    file1     = new File(imagepathFour);
                    pathFour  = file1.getName();
                }

                bin1 = new FileBody(file1);
                reqEntity.addPart("uploadedfile1", bin1);
            } else if (countImage==2) {
                post = new HttpPost(Referensi.url+"/UploadTwo.php");

                if (imagepathOne!=null) {
                    file1     = new File(imagepathOne);
                    pathOne   = file1.getName();
                } else if (imagepathTwo!=null) {
                    file1     = new File(imagepathTwo);
                    pathTwo   = file1.getName();
                } else if (imagepathThree!=null) {
                    file1     = new File(imagepathThree);
                    pathThree = file1.getName();
                } else {
                    file1     = new File(imagepathFour);
                    pathFour  = file1.getName();
                }

                if (imagepathTwo!=null) {
                    file2     = new File(imagepathTwo);
                    pathTwo   = file2.getName();
                } else if (imagepathThree!=null) {
                    file2     = new File(imagepathThree);
                    pathThree = file2.getName();
                } else if (imagepathFour!=null) {
                    file2     = new File(imagepathFour);
                    pathFour  = file2.getName();
                } else {
                    file2     = new File(imagepathOne);
                    pathOne   = file2.getName();
                }

                bin1 = new FileBody(file1);
                bin2 = new FileBody(file2);
                reqEntity.addPart("uploadedfile1", bin1);
                reqEntity.addPart("uploadedfile2", bin2);
            } else if (countImage==3) {
                post = new HttpPost(Referensi.url+"/UploadThree.php");

                if (imagepathOne!=null) {
                    file1	   = new File(imagepathOne);
                    pathOne   = file1.getName();
                } else if (imagepathTwo!=null) {
                    file1 	   = new File(imagepathTwo);
                    pathTwo   = file1.getName();
                } else if (imagepathThree!=null) {
                    file1 	   = new File(imagepathThree);
                    pathThree = file1.getName();
                } else {
                    file1 	   = new File(imagepathFour);
                    pathFour  = file1.getName();
                }

                if (imagepathTwo!=null) {
                    file2     = new File(imagepathTwo);
                    pathTwo   = file2.getName();
                } else if (imagepathThree!=null) {
                    file2     = new File(imagepathThree);
                    pathThree = file2.getName();
                } else if (imagepathFour!=null) {
                    file2     = new File(imagepathFour);
                    pathFour  = file2.getName();
                } else {
                    file2     = new File(imagepathOne);
                    pathOne   = file2.getName();
                }

                if (imagepathThree!=null) {
                    file3 	   = new File(imagepathThree);
                    pathThree = file3.getName();
                } else if (imagepathFour!=null) {
                    file3     = new File(imagepathFour);
                    pathFour  = file3.getName();
                } else if (imagepathOne!=null) {
                    file3 	   = new File(imagepathOne);
                    pathOne   = file3.getName();
                } else {
                    file3 	   = new File(imagepathTwo);
                    pathTwo   = file3.getName();
                }

                bin1 = new FileBody(file1);
                bin2 = new FileBody(file2);
                bin3 = new FileBody(file3);
                reqEntity.addPart("uploadedfile1", bin1);
                reqEntity.addPart("uploadedfile2", bin2);
                reqEntity.addPart("uploadedfile3", bin3);
            } else if (countImage==4) {
                post = new HttpPost(Referensi.url+"/UploadFour.php");

                if (imagepathOne!=null) {
                    file1 	   = new File(imagepathOne);
                    pathOne   = file1.getName();
                } else if (imagepathTwo!=null) {
                    file1 	   = new File(imagepathTwo);
                    pathTwo   = file1.getName();
                } else if (imagepathThree!=null) {
                    file1 	   = new File(imagepathThree);
                    pathThree = file1.getName();
                } else {
                    file1 	   = new File(imagepathFour);
                    pathFour  = file1.getName();
                }

                if (imagepathTwo!=null) {
                    file2 	   = new File(imagepathTwo);
                    pathTwo   = file2.getName();
                } else if (imagepathThree!=null) {
                    file2 	   = new File(imagepathThree);
                    pathThree = file2.getName();
                } else if (imagepathFour!=null) {
                    file2 	   = new File(imagepathFour);
                    pathFour  = file2.getName();
                } else {
                    file2 = new File(imagepathOne);
                    pathOne   = file2.getName();
                }

                if (imagepathThree!=null) {
                    file3     = new File(imagepathThree);
                    pathThree = file3.getName();
                } else if (imagepathFour!=null) {
                    file3     = new File(imagepathFour);
                    pathFour  = file3.getName();
                } else if (imagepathOne!=null) {
                    file3     = new File(imagepathOne);
                    pathOne   = file3.getName();
                } else {
                    file3 	   = new File(imagepathTwo);
                    pathTwo   = file3.getName();
                }

                if (imagepathFour!=null) {
                    file4     = new File(imagepathFour);
                    pathFour  = file4.getName();
                } else if (imagepathOne!=null) {
                    file4     = new File(imagepathOne);
                    pathOne   = file4.getName();
                } else if (imagepathTwo!=null) {
                    file4     = new File(imagepathTwo);
                    pathTwo   = file4.getName();
                } else {
                    file4     = new File(imagepathThree);
                    pathThree = file4.getName();
                }

                bin1 = new FileBody(file1);
                bin2 = new FileBody(file2);
                bin3 = new FileBody(file3);
                bin4 = new FileBody(file4);
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
