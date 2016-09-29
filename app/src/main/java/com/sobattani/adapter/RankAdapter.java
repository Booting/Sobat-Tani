package com.sobattani.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sobattani.R;
import com.sobattani.ShowProfileActivity;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.ImageLoader;
import org.json.JSONArray;
import java.util.ArrayList;

public class RankAdapter extends BaseAdapter {

    private final String USERID          = "UserId";
    private final String USERTYPE        = "UserType";
    private final String USERTYPENAME    = "UserTypeName";
    private final String EMAIL 	         = "Email";
    private final String NAMA            = "Nama";
    private final String ALAMAT          = "Alamat";
    private final String NOTELP          = "NoTelp";
    private final String TANAMAN         = "Tanaman";
    private final String TANAMANNAME     = "TanamanName";
    private final String HAMADANPENYAKIT = "HamaDanPenyakit";
    private final String KATEGORIPRODUK  = "KategoriProduk";
    private final String NAMAPERUSAHAAN  = "NamaPerusahaan";
    private final String AGRONOMISNAME   = "AgronomisName";

    private ArrayList<String> arrayUserId = new ArrayList<String>(),
            arrayUserType        = new ArrayList<String>(),
            arrayUserTypeName    = new ArrayList<String>(),
            arrayEmail           = new ArrayList<String>(),
            arrayNama            = new ArrayList<String>(),
            arrayAlamat          = new ArrayList<String>(),
            arrayNoTelp          = new ArrayList<String>(),
            arrayTanaman         = new ArrayList<String>(),
            arrayTanamanName     = new ArrayList<String>(),
            arrayHamaDanPenyakit = new ArrayList<String>(),
            arrayKategoriProduk  = new ArrayList<String>(),
            arrayNamaPerusahaan  = new ArrayList<String>(),
            arrayAgronomisName   = new ArrayList<String>();

    private final LayoutInflater mLayoutInflater;
    private final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
    private ImageLoader mImageLoader;
    public JSONArray jsonItemList;
    private Context _context;
    private Typeface fontUbuntuL, fontUbuntuB;
    private Activity activity;

    public RankAdapter(Context context, JSONArray jsonItemList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.jsonItemList    = jsonItemList;
        mImageLoader         = new ImageLoader(context);
        _context             = context;
        fontUbuntuL 		 = FontCache.get(context, "Ubuntu-L");
		fontUbuntuB 		 = FontCache.get(context, "Ubuntu-B");
		activity			 = (Activity) _context;

        for (int i = 0; i < this.jsonItemList.length(); i++) {
            arrayUserId.add(this.jsonItemList.optJSONObject(i).optString(USERID));
            arrayUserType.add(this.jsonItemList.optJSONObject(i).optString(USERTYPE));
            arrayUserTypeName.add(this.jsonItemList.optJSONObject(i).optString(USERTYPENAME));
            arrayEmail.add(this.jsonItemList.optJSONObject(i).optString(EMAIL));
            arrayNama.add(this.jsonItemList.optJSONObject(i).optString(NAMA));
            arrayAlamat.add(this.jsonItemList.optJSONObject(i).optString(ALAMAT));
            arrayNoTelp.add(this.jsonItemList.optJSONObject(i).optString(NOTELP));
            arrayTanaman.add(this.jsonItemList.optJSONObject(i).optString(TANAMAN));
            arrayTanamanName.add(this.jsonItemList.optJSONObject(i).optString(TANAMANNAME));
            arrayHamaDanPenyakit.add(this.jsonItemList.optJSONObject(i).optString(HAMADANPENYAKIT));
            arrayKategoriProduk.add(this.jsonItemList.optJSONObject(i).optString(KATEGORIPRODUK));
            arrayNamaPerusahaan.add(this.jsonItemList.optJSONObject(i).optString(NAMAPERUSAHAAN));
            arrayAgronomisName.add(this.jsonItemList.optJSONObject(i).optString(AGRONOMISNAME));
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return jsonItemList.length();
    }

    @Override
    public Object getItem(int position) {
        return this.jsonItemList.optString(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.rank_cell, parent, false);
            vh = new ViewHolder();

            vh.relItem	   = (RelativeLayout) convertView.findViewById(R.id.relItem);
            vh.imgProfile  = (ImageView) convertView.findViewById(R.id.imgProfile);
            vh.txtPosition = (TextView) convertView.findViewById(R.id.txtPosition);
            vh.txtTitle    = (TextView) convertView.findViewById(R.id.txtTitle);
            vh.txtDetail   = (TextView) convertView.findViewById(R.id.txtDetail);
            vh.txtCall     = (TextView) convertView.findViewById(R.id.txtCall);
            vh.txtWhatsApp = (TextView) convertView.findViewById(R.id.txtWhatsApp);
            vh.linCall     = (LinearLayout) convertView.findViewById(R.id.linCall);
            vh.linWhatsApp = (LinearLayout) convertView.findViewById(R.id.linWhatsApp);

            vh.txtTitle.setTypeface(fontUbuntuB);
            vh.txtDetail.setTypeface(fontUbuntuL);
            vh.txtPosition.setTypeface(fontUbuntuL);
            vh.txtCall.setTypeface(fontUbuntuB);
            vh.txtWhatsApp.setTypeface(fontUbuntuB);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        // Set Data
        vh.txtPosition.setText(""+(position+1));
        vh.txtTitle.setText(arrayNama.get(position));

        if (arrayUserType.get(position).equalsIgnoreCase("1")) {
            vh.txtDetail.setText("Kategori User : " + arrayUserTypeName.get(position)  + "\n" + "Jenis Tanaman : " + arrayTanamanName.get(position));
        } else if (arrayUserType.get(position).equalsIgnoreCase("3")) {
            vh.txtDetail.setText("Kategori User : " + arrayUserTypeName.get(position)  + "\n" +  "Perusahaan : " + arrayAgronomisName.get(position));
        } else {
            vh.txtDetail.setText("Kategori User : " + arrayUserTypeName.get(position));
        }

        Glide.with(_context).load("https://graph.facebook.com/" + arrayUserId.get(position) + "/picture?type=normal")
                .placeholder(R.drawable.img_loader).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(100, 100)
                .dontAnimate().into(vh.imgProfile);

        vh.linCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (arrayNoTelp.get(position).equalsIgnoreCase("")) {
                    Toast.makeText(activity, "No. Telp is Required!", Toast.LENGTH_SHORT).show();
                } else {
                    String uri = "tel:" + arrayNoTelp.get(position).replace("-", "").replace(" ","");
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    activity.startActivity(intent);
                }
            }
        });

        vh.linWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (arrayNoTelp.get(position).equalsIgnoreCase("")) {
                    Toast.makeText(activity, "No. Telp is Required!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Uri uri = Uri.parse("smsto:" + "+62" + arrayNoTelp.get(position).substring(1));
                        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                        i.setPackage("com.whatsapp");
                        i.putExtra("sms_body", "The text goes here");
                        i.putExtra("chat", true);
                        activity.startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(activity, "no whatsapp!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        vh.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, ShowProfileActivity.class);
                intent.putExtra("UserId", arrayUserId.get(position));
                _context.startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout relItem;
        ImageView imgProfile;
        TextView txtPosition;
        TextView txtTitle;
        TextView txtDetail;
        TextView txtCall;
        TextView txtWhatsApp;
        LinearLayout linCall, linWhatsApp;
    }

}