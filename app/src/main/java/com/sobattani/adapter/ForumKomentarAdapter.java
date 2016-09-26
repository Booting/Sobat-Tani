package com.sobattani.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.sobattani.R;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.ImageLoader;
import com.sobattani.Utils.Referensi;
import org.json.JSONArray;
import org.json.JSONException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ForumKomentarAdapter extends BaseAdapter {

    private final String REPLYID      = "ReplyId";
    private final String USERID       = "UserId";
    private final String NAMA         = "Nama";
    private final String TEXT 	      = "Text";
    private final String IMAGE        = "Image";
    private final String LIKECOUNT    = "LikeCount";
    private final String REPLYLIKEID  = "ReplyLikeId";
    private final String ISLIKE       = "IsLike";
    private final String DATE         = "Datee";

    private ArrayList<String> arrayReplyId = new ArrayList<String>(),
            arrayUserId       = new ArrayList<String>(),
            arrayNama         = new ArrayList<String>(),
            arrayText         = new ArrayList<String>(),
            arrayImage        = new ArrayList<String>(),
            arrayLikeCount    = new ArrayList<String>(),
            arrayReplyLikeId  = new ArrayList<String>(),
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
    private ForumKomentarAdapterListener listener;

    public ForumKomentarAdapter(Context context, JSONArray jsonItemList, ForumKomentarAdapterListener mListener) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.jsonItemList    = jsonItemList;
        mImageLoader         = new ImageLoader(context);
        _context             = context;
        fontUbuntuL 		 = FontCache.get(context, "Ubuntu-L");
		fontUbuntuB 		 = FontCache.get(context, "Ubuntu-B");
		activity			 = (Activity) _context;
        listener             = mListener;

        for (int i = 0; i < this.jsonItemList.length(); i++) {
            arrayReplyId.add(this.jsonItemList.optJSONObject(i).optString(REPLYID));
            arrayUserId.add(this.jsonItemList.optJSONObject(i).optString(USERID));
            arrayNama.add(this.jsonItemList.optJSONObject(i).optString(NAMA));
            arrayText.add(this.jsonItemList.optJSONObject(i).optString(TEXT));
            arrayLikeCount.add(this.jsonItemList.optJSONObject(i).optString(LIKECOUNT));
            arrayReplyLikeId.add(this.jsonItemList.optJSONObject(i).optString(REPLYLIKEID));
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
            convertView = mLayoutInflater.inflate(R.layout.forum_komentar_cell, parent, false);
            vh = new ViewHolder();

            vh.relItem	       = (LinearLayout) convertView.findViewById(R.id.relItem);
            vh.imgItem         = (DynamicHeightImageView) convertView.findViewById(R.id.imgItem);
            vh.imgProfile      = (ImageView) convertView.findViewById(R.id.imgProfile);
            vh.txtReplyId      = (TextView) convertView.findViewById(R.id.txtReplyId);
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
            vh.relLike         = (LinearLayout) convertView.findViewById(R.id.relLike);
            vh.relComment      = (LinearLayout) convertView.findViewById(R.id.relComment);

            vh.txtName.setTypeface(fontUbuntuB);
            vh.txtDate.setTypeface(fontUbuntuL);
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
        vh.txtReplyId.setText(arrayReplyId.get(position));
        vh.txtName.setText(arrayNama.get(position));
        vh.txtText.setText(arrayText.get(position));
        vh.txtCountLike.setText(arrayLikeCount.get(position));
        vh.txtIsLike.setText(arrayIsLike.get(position));
        vh.txtLikeId.setText(arrayReplyLikeId.get(position));

        if (vh.txtIsLike.getText().toString().equalsIgnoreCase("1")) {
            vh.imgLike.setImageResource(R.drawable.thumb_up);
        } else {
            vh.imgLike.setImageResource(R.drawable.thumb_up_outline);
        }

        int loader = R.drawable.img_loader;
        mImageLoader.DisplayImage("https://graph.facebook.com/" + arrayUserId.get(position) + "/picture?type=normal", loader, vh.imgProfile);

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
                mImageLoader.DisplayImage(Referensi.url + "/pictures/" + jArray.get(0).toString().replace(" ", "%20"), loader, vh.imgItem);
            } else {
                vh.imgItem.setVisibility(View.GONE);
            }
        } catch (JSONException e) { e.printStackTrace(); }

        vh.relLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLikeClicked(vh.txtReplyId.getText().toString(), vh.txtLikeId.getText().toString(), vh.txtIsLike.getText().toString());
            }
        });

        return convertView;
    }

    static class ViewHolder {
    	LinearLayout relItem, relLike, relComment;
        DynamicHeightImageView imgItem;
        ImageView imgProfile, imgLike;
        TextView txtReplyId, txtName, txtDate, txtText, txtCountLike, txtLike, txtCountComment, txtComment, txtIsLike, txtLikeId;
    }

    public interface ForumKomentarAdapterListener {
        public void onLikeClicked(String ReplyId, String ReplyLikeId, String diLike);
    }

}