package com.sobattani;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.Referensi;
import com.sobattani.Utils.TypeFaceSpan;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShopRulesActivity extends AppCompatActivity {
	private WebView txtDetail;
    private String mimeType = "text/html";
    private String encoding = "utf-8";
    private String htmlText = "";
    private Typeface fontUbuntuB;
    private TextView txtNext;
    private SharedPreferences sobatTaniPref;
    private RequestQueue queue;
    private JSONArray str_login  = null;
    private String UserId = "";
    private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		setContentView(R.layout.activity_shop_rules);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initToolbar();

        queue 		  = Volley.newRequestQueue(this);
        sobatTaniPref = getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
		fontUbuntuB   = FontCache.get(ShopRulesActivity.this, "DroidSans-Bold");
		txtDetail     = (WebView) findViewById(R.id.txtDetail);
		txtNext       = (TextView) findViewById(R.id.txtNext);
        UserId        = sobatTaniPref.getString("UserId", "");
        
        htmlText = "<html><body style='text-align:justify'>SYARAT & KETENTUAN JUAL dan BELI APLIKASI SOBAT TANI<br/><br/>"+
                "Setiap pengguna menerima, memahami, menyetujui mematuhi semua isi dalam Syarat & Ketentuan Layanan di bawah ini. Syarat dan Ketentuan mengikat para pengguna Aplikasi Sobat Tani.<br/><br/>"+
                "Aplikasi Sobat Tani  tidak bertanggung jawab atas isi informasi, gambar dan keterangan lainnya yang terdapat atau diterbitkan dalam aplikasi Ini. Aplikasi Sobat Tani tidak bertanggung jawab dan tidak dapat diminta pertanggungjawaban atas kerugian langsung maupun tidak langsung apapun sebagai akibat dari keuntungan berkaitan dengan penggunaan atau kinerja dari informasi dan/atau gambar yang disediakan apikasi ini. Admin berhak memperbaharui dan/atau menghapus dan/atau mengubah isi iklan dan/atau mengubah tampilan dan isinya tanpa melakukan pemberitahuan terlebih dahulu kepada pengguna dan demi memberi pelayanan yang lebih baik kepada setiap pengguna.<br/><br/>"+
                "Untuk informasi materi yang sudah terjual atau sudah tidak tersedia lagi maka pengguna dapat mengirimkan email ke <a href=''>admin@sobattani.com</a>  atau materi informasi jual beli di akan kadaluwarsa (hilang otomatis) setelah jangka waktu 3 bulan. Apabila belum terjual atau masih tersedia, pengguna dipersilahkan memperbaharui materi tersebut, admin tidak menanggung kewajiban untuk memperbaharui materi.<br/><br/>"+
                "Pengguna wajib tunduk dan patuh pada peraturan perundangan-undangan yang berlaku serta hukum yang berlaku di Republik Indonesia. Segala tindakan yang bertentangan, melanggar, tidak sesuai termasuk tidak terbatas pada ketidakpatuhan pada peraturan perundang-undangan menjadi tanggung jawab pengguna sepenuhnya. Aplikasi Sobat Tani berhak mengungkapkan identitas pengguna kepada pihak ketiga dan/atau pihak yang berwenang berkaitan tentang materi yang ditempatkan pengguna ke aplikasi atau jika materi merupakan pelanggaran terhadap Hak Kekayaan Intelektual atau hak pribadi pihak ketiga, sesuai peraturan yang berlaku di Indonesia.<br/><br/>"+
                "Syarat & Ketentuan Layanan dapat diubah dan/atau diperbaharui sewaktu-waktu oleh admin tanpa pemberitahuan terlebih dahulu. Apabila pengguna memiliki hal lain yang ingin disampaikan silakan menghubungi email <a href=''>admin@sobattani.com</a></body></Html>";
        txtDetail.loadData(htmlText, mimeType, encoding);
        txtNext.setTypeface(fontUbuntuB);

        //getUserDetail();
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString(getString(R.string.syarat_dan_ketentuan));
        spanToolbar.setSpan(new TypeFaceSpan(ShopRulesActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
        final ProgressDialog dialog;
        dialog = new ProgressDialog(ShopRulesActivity.this);
        dialog.setMessage("Mohon Menunggu...");
        dialog.show();
        dialog.setCancelable(true);

        String url = Referensi.url+"/getUserDetail.php?UserId="+UserId;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    str_login  = response.getJSONArray("info");
                    for (int i = 0; i < str_login.length(); i++){
                        JSONObject ar = str_login.getJSONObject(i);
                        SharedPreferences.Editor editor = sobatTaniPref.edit();
                        editor.putString("UserType", ar.getString("UserType"));
                        editor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }
	
	public void btnNextClick(View v) {
		startActivityForResult(new Intent(getApplicationContext(), ShopAddActivity.class), 1);
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
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
