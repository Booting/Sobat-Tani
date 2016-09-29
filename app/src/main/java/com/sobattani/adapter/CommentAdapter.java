package com.sobattani.adapter;

import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sobattani.R;
import com.sobattani.ShowProfileActivity;
import com.sobattani.Utils.CircleImageView;
import com.sobattani.Utils.CustomTypefaceSpan;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.Referensi;

public class CommentAdapter extends BaseAdapter {
    private static LayoutInflater mInflater = null;
    private Context context;
	private Typeface fontLatoRegular, fontLatoHeavy;
    private JSONArray jsonArray;
    private SharedPreferences sobatTaniPref;

    public CommentAdapter(Context mContext, JSONArray mJsonArray) {
        context         = mContext;
        mInflater       = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fontLatoRegular = FontCache.get(context, "Lato-Regular");
        fontLatoHeavy   = FontCache.get(context, "Lato-Heavy");
        jsonArray       = mJsonArray;
        sobatTaniPref   = context.getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
    }

    public static class ViewHolder {
        public TextView txtComment;
        public CircleImageView profileImage;
    }

    @Override
    public int getCount() {
    	return jsonArray.length();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({ "InflateParams" })
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_comment, null);

            holder 		      	= new ViewHolder();
            holder.profileImage = (CircleImageView) convertView.findViewById(R.id.profileImage);
            holder.txtComment   = (TextView) convertView.findViewById(R.id.txtComment);
            holder.txtComment.setTypeface(fontLatoRegular);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final JSONObject jObj = jsonArray.optJSONObject(position);
        Glide.with(context).load("https://graph.facebook.com/" + jObj.optString("UserId") + "/picture?type=normal")
                .placeholder(R.drawable.img_loader).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(100, 100)
                .dontAnimate().into(holder.profileImage);

        //Message
        String strUsername;
        if (jObj.optString("Nama").equalsIgnoreCase(sobatTaniPref.getString("UserName", ""))) {
            strUsername = context.getString(R.string.you);
        } else {
            strUsername = jObj.optString("Nama");
        }
        holder.txtComment.setText(strUsername + " : \"" + jObj.optString("CommentText") + "\"");
        String strMessages   = holder.txtComment.getText().toString();
        Spannable spannables = new SpannableString(strMessages);
        spannables.setSpan(new CustomTypefaceSpan("", fontLatoHeavy), 0, strUsername.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannables.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.onesignal_bgimage_notif_title_color)), 0, strUsername.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        holder.txtComment.setText(spannables);

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowProfileActivity.class);
                intent.putExtra("UserId", jObj.optString("UserId"));
                context.startActivity(intent);
            }
        });

        return convertView;
    }
    
}
