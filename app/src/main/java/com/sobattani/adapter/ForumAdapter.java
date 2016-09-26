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
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.sobattani.ForumKomentarActivity;
import com.sobattani.R;
import com.sobattani.ShowProfileActivity;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.ImageLoader;
import com.sobattani.Utils.Referensi;
import org.json.JSONArray;
import org.json.JSONException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ForumAdapter extends BaseAdapter {

    private final String THREADID     = "ThreadId";
    private final String USERID       = "UserId";
    private final String USERTYPENAME = "UserTypeName";
    private final String NAMA         = "Nama";
    private final String NOTELP       = "NoTelp";
    private final String GCMID        = "gcm_registration_id";
    private final String TEXT 	      = "Text";
    private final String IMAGE        = "Image";
    private final String LIKECOUNT    = "LikeCount";
    private final String COMMENTCOUNT = "CommentCount";
    private final String THREADLIKEID = "ThreadLikeId";
    private final String ISLIKE       = "IsLike";
    private final String DATE         = "Datee";

    private ArrayList<String> arrayThreadId = new ArrayList<String>(),
            arrayUserId       = new ArrayList<String>(),
            arrayUserTypeName = new ArrayList<String>(),
            arrayNama         = new ArrayList<String>(),
            arrayNoTelp       = new ArrayList<String>(),
            arrayGcmId        = new ArrayList<String>(),
            arrayText         = new ArrayList<String>(),
            arrayImage        = new ArrayList<String>(),
            arrayLikeCount    = new ArrayList<String>(),
            arrayCommentCount = new ArrayList<String>(),
            arrayThreadLikeId = new ArrayList<String>(),
            arrayIsLike       = new ArrayList<String>(),
            arrayDate         = new ArrayList<String>();

    private final LayoutInflater mLayoutInflater;
    private final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
    private ImageLoader mImageLoader;
    public JSONArray jsonItemList;
    private Context _context;
    private ArrayList<Double> arrayImageHeight = new ArrayList<Double>();
    private Typeface fontUbuntuL, fontUbuntuB;
    private Activity activity;
    private ForumAdapterListener listener;

    public ForumAdapter(Context context, JSONArray jsonItemList, ForumAdapterListener mListener) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.jsonItemList    = jsonItemList;
        mImageLoader         = new ImageLoader(context);
        _context             = context;
        fontUbuntuL 		 = FontCache.get(context, "Ubuntu-L");
		fontUbuntuB 		 = FontCache.get(context, "Ubuntu-B");
		activity			 = (Activity) _context;
        listener             = mListener;

        for (int i = 0; i < this.jsonItemList.length(); i++) {
            if (!this.jsonItemList.optJSONObject(i).optString(DATE).equalsIgnoreCase("")) {
                arrayThreadId.add(this.jsonItemList.optJSONObject(i).optString(THREADID));
                arrayUserId.add(this.jsonItemList.optJSONObject(i).optString(USERID));
                arrayUserTypeName.add(this.jsonItemList.optJSONObject(i).optString(USERTYPENAME));
                arrayNama.add(this.jsonItemList.optJSONObject(i).optString(NAMA));
                arrayNoTelp.add(this.jsonItemList.optJSONObject(i).optString(NOTELP));
                arrayGcmId.add(this.jsonItemList.optJSONObject(i).optString(GCMID));
                arrayText.add(this.jsonItemList.optJSONObject(i).optString(TEXT));
                arrayLikeCount.add(this.jsonItemList.optJSONObject(i).optString(LIKECOUNT));
                arrayCommentCount.add(this.jsonItemList.optJSONObject(i).optString(COMMENTCOUNT));
                arrayThreadLikeId.add(this.jsonItemList.optJSONObject(i).optString(THREADLIKEID));
                arrayIsLike.add(this.jsonItemList.optJSONObject(i).optString(ISLIKE));
                arrayDate.add(this.jsonItemList.optJSONObject(i).optString(DATE));
                try {
                    JSONArray jsonProdImage = new JSONArray(this.jsonItemList.optJSONObject(i).optString(IMAGE));
                    arrayImage.add(imageURLwithRatio(jsonProdImage.toString()));
                } catch (JSONException e) {
                    arrayImage.add("");
                }
            }
        }
    }
    
    private String imageURLwithRatio(String imageName) throws NumberFormatException {
        arrayImageHeight.add(1.0);
        return imageName;
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
            convertView = mLayoutInflater.inflate(R.layout.forum_cell, parent, false);
            vh = new ViewHolder();

            vh.relItem	       = (LinearLayout) convertView.findViewById(R.id.relItem);
            vh.imgItem         = (DynamicHeightImageView) convertView.findViewById(R.id.imgItem);
            vh.imgProfile      = (ImageView) convertView.findViewById(R.id.imgProfile);
            vh.txtThreadId     = (TextView) convertView.findViewById(R.id.txtThreadId);
            vh.txtName         = (TextView) convertView.findViewById(R.id.txtName);
            vh.txtDate         = (TextView) convertView.findViewById(R.id.txtDate);
            vh.txtText         = (TextView) convertView.findViewById(R.id.txtText);
            vh.txtCountLike    = (TextView) convertView.findViewById(R.id.txtCountLike);
            vh.txtLike         = (TextView) convertView.findViewById(R.id.txtLike);
            vh.txtCountComment = (TextView) convertView.findViewById(R.id.txtCountComment);
            vh.txtComment      = (TextView) convertView.findViewById(R.id.txtComment);
            vh.imgLike         = (ImageView) convertView.findViewById(R.id.imgLike);
            vh.txtIsLike       = (TextView) convertView.findViewById(R.id.txtIsLike);
            vh.txtLikeId       = (TextView) convertView.findViewById(R.id.txtLikeId);
            vh.txtGcmId        = (TextView) convertView.findViewById(R.id.txtGcmId);
            vh.relLike         = (RelativeLayout) convertView.findViewById(R.id.relLike);
            vh.relComment      = (RelativeLayout) convertView.findViewById(R.id.relComment);
            vh.txtKategori     = (TextView) convertView.findViewById(R.id.txtKategori);
            vh.imgPhone        = (ImageView) convertView.findViewById(R.id.imgPhone);
            vh.imgWhatsApp     = (ImageView) convertView.findViewById(R.id.imgWhatsApp);

            vh.txtName.setTypeface(fontUbuntuB);
            vh.txtDate.setTypeface(fontUbuntuL);
            vh.txtKategori.setTypeface(fontUbuntuL);
            vh.txtText.setTypeface(fontUbuntuL);
            vh.txtCountLike.setTypeface(fontUbuntuL);
            vh.txtLike.setTypeface(fontUbuntuL);
            vh.txtCountComment.setTypeface(fontUbuntuL);
            vh.txtComment.setTypeface(fontUbuntuL);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        // Set Data
        vh.txtThreadId.setText(arrayThreadId.get(position));
        vh.txtName.setText(arrayNama.get(position));
        vh.txtText.setText(arrayText.get(position));
        vh.txtCountLike.setText(arrayLikeCount.get(position));
        vh.txtCountComment.setText(arrayCommentCount.get(position));
        vh.txtIsLike.setText(arrayIsLike.get(position));
        vh.txtLikeId.setText(arrayThreadLikeId.get(position));
        vh.txtGcmId.setText(arrayGcmId.get(position));
        vh.txtKategori.setText(arrayUserTypeName.get(position));

        if (vh.txtIsLike.getText().toString().equalsIgnoreCase("1")) {
            vh.imgLike.setImageResource(R.drawable.thumb_up);
        } else {
            vh.imgLike.setImageResource(R.drawable.thumb_up_outline);
        }

        Glide.with(_context).load("https://graph.facebook.com/" + arrayUserId.get(position) + "/picture?type=normal")
                .placeholder(R.drawable.img_loader).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(100, 100)
                .dontAnimate().into(vh.imgProfile);

        long lastUpdate     = Long.parseLong(arrayDate.get(position));
        long remainingDays  = Referensi.getRemainingDays(lastUpdate);
        Date dateLastUpdate = new Date(lastUpdate);

        if (remainingDays == 0) {
            Date dtLastUpdate1 = null;
            try {
                dtLastUpdate1 = Referensi.getSimpleDateFormatHours().parse(Referensi.getSimpleDateFormatHours().format(dateLastUpdate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long eventMillis1   = dtLastUpdate1.getTime();
            long diffMillis1    = Referensi.getCurrentMillis() - eventMillis1;
            long remainingHours = TimeUnit.MILLISECONDS.toHours(diffMillis1);

            if (remainingHours == 0) {
                long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis1);
                vh.txtDate.setText(""+remainingMinutes+" minutes");
            } else {
                vh.txtDate.setText(""+remainingHours+" hours");
            }
        } else {
            vh.txtDate.setText(""+remainingDays+" days");
        }

        JSONArray jArray = null;
        try {
            vh.imgItem.setHeightRatio(1.0);
            if (!arrayImage.get(position).equalsIgnoreCase("[]")) {
                vh.imgItem.setVisibility(View.VISIBLE);
                jArray = new JSONArray(arrayImage.get(position));
                Glide.with(_context).load(Referensi.url + "/pictures/" + jArray.get(0).toString())
                        .placeholder(R.drawable.img_loader).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(500, 500)
                        .dontAnimate().into(vh.imgItem);
            } else {
                vh.imgItem.setVisibility(View.GONE);
            }
        } catch (JSONException e) { e.printStackTrace(); }

        vh.relLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLikeClicked(vh.txtThreadId.getText().toString(), vh.txtLikeId.getText().toString(), vh.txtIsLike.getText().toString());
            }
        });

        vh.relComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, ForumKomentarActivity.class).
                        putExtra("ThreadId", vh.txtThreadId.getText().toString()).
                        putExtra("GcmId", vh.txtGcmId.getText().toString()).
                        putExtra("LikeCount", vh.txtCountLike.getText().toString()));
            }
        });

        vh.imgPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayNoTelp.get(position).equalsIgnoreCase("")) {
                    Toast.makeText(activity, "No. Telp is Required!", Toast.LENGTH_SHORT).show();
                } else {
                    String uri = "tel:" + arrayNoTelp.get(position).replace("-","").replace(" ","");
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    activity.startActivity(intent);
                }
            }
        });

        vh.imgWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayNoTelp.get(position).equalsIgnoreCase("")) {
                    Toast.makeText(activity, "No. Telp is Required!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Uri uri = Uri.parse("smsto:"+ "+62"+arrayNoTelp.get(position).substring(1));
                        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                        i.setPackage("com.whatsapp");
                        i.putExtra("sms_body", "The text goes here");
                        i.putExtra("chat",true);
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
        vh.txtName.setOnClickListener(new View.OnClickListener() {
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
    	LinearLayout relItem;
        DynamicHeightImageView imgItem;
        ImageView imgProfile, imgLike, imgPhone, imgWhatsApp;
        TextView txtThreadId, txtName, txtDate, txtText, txtCountLike, txtLike, txtCountComment, txtComment, txtIsLike, txtLikeId, txtGcmId,
                txtKategori;
        RelativeLayout relLike, relComment;
    }

    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        if (ratio == 0) {
            ratio = getImageHeightRatio(position);
            sPositionHeightRatios.append(position, ratio);
        }
        return ratio;
    }

    private double getImageHeightRatio(int position) {
        double ratio = (arrayImageHeight.get(position) / 300.0) / 2.0;
        if (ratio > 1.5)
            ratio = 1.1;
        if (ratio < 1) {
            ratio = 0.8;
        }
        return ratio;
    }

    public interface ForumAdapterListener {
        public void onLikeClicked(String ThreadId, String ThreadLikeId, String diLike);
    }

}