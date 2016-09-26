package com.sobattani.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.sobattani.R;
import com.sobattani.ShopDetailActivity;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.Referensi;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;

public class ShopAdapter extends BaseAdapter {

    private final String ITEMID 	   = "ItemId";
    private final String SELLERID 	   = "UserId";
    private final String USERNAME 	   = "Nama";
    private final String CATEGORINAME  = "ProdukCategoriName";
    private final String CATEGORINAMEE = "TanamanName";
    private final String ITEMNAME      = "ItemName";
    private final String DESKRIPSI     = "Deskripsi";
    private final String HARGAASLI 	   = "HargaAsli";
    private final String LOKASI 	   = "Lokasi";
    private final String CONTACT 	   = "Contact";
    private final String NOREKENING    = "Stock";
    private final String PICTURE 	   = "Picture";

    private ArrayList<String> arrayItemId = new ArrayList<String>(),
            arraySellerId      = new ArrayList<String>(),
            arrayUserName      = new ArrayList<String>(),
            arrayCategoriName  = new ArrayList<String>(),
            arrayItemName      = new ArrayList<String>(),
            arrayDeskripsi     = new ArrayList<String>(),
            arrayHargaAsli 	   = new ArrayList<String>(),
            arrayLokasi        = new ArrayList<String>(),
            arrayContact       = new ArrayList<String>(),
            arrayStock         = new ArrayList<String>(),
            arrayPicture       = new ArrayList<String>();

    private final LayoutInflater mLayoutInflater;
    private final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
    public JSONArray jsonItemList;
    private Context _context;
    private ArrayList<Double> arrayImageHeight = new ArrayList<Double>();
    private Typeface fontUbuntuL, fontUbuntuB;
    private Activity activity;
    private String UserType;
    
    public ShopAdapter(Context context, JSONArray jsonItemList, String mUserType) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.jsonItemList    = jsonItemList;
        _context             = context;
        fontUbuntuL 		 = FontCache.get(context, "DroidSans");
		fontUbuntuB 		 = FontCache.get(context, "DroidSans-Bold");
		activity			 = (Activity) _context;
        this.UserType        = mUserType;
        
        for (int i = 0; i < this.jsonItemList.length(); i++) {
            arrayItemId.add(this.jsonItemList.optJSONObject(i).optString(ITEMID));
        	arraySellerId.add(this.jsonItemList.optJSONObject(i).optString(SELLERID));
        	arrayUserName.add(this.jsonItemList.optJSONObject(i).optString(USERNAME));
            if (UserType.equalsIgnoreCase("1")) {
                arrayCategoriName.add(this.jsonItemList.optJSONObject(i).optString(CATEGORINAMEE));
            } else {
                arrayCategoriName.add(this.jsonItemList.optJSONObject(i).optString(CATEGORINAME));
            }
            arrayItemName.add(this.jsonItemList.optJSONObject(i).optString(ITEMNAME));
        	arrayDeskripsi.add(this.jsonItemList.optJSONObject(i).optString(DESKRIPSI));
        	arrayHargaAsli.add(this.jsonItemList.optJSONObject(i).optString(HARGAASLI));
        	arrayLokasi.add(this.jsonItemList.optJSONObject(i).optString(LOKASI));
            arrayStock.add(this.jsonItemList.optJSONObject(i).optString(NOREKENING));
        	arrayContact.add(this.jsonItemList.optJSONObject(i).optString(CONTACT));
        	try {
        		JSONArray jsonProdImage = new JSONArray(this.jsonItemList.optJSONObject(i).optString(PICTURE));
        		arrayPicture.add(imageURLwithRatio(jsonProdImage.toString()));
            } catch (JSONException e) {
            	arrayPicture.add("");
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
            convertView = mLayoutInflater.inflate(R.layout.shop_cell, parent, false);
            vh = new ViewHolder();

            vh.relItem		  = (LinearLayout) convertView.findViewById(R.id.relItem);
            vh.imgItem        = (DynamicHeightImageView) convertView.findViewById(R.id.imgItem);
            vh.txtItemName    = (TextView) convertView.findViewById(R.id.txtItemName);
            vh.txtSeller      = (TextView) convertView.findViewById(R.id.txtSeller);
            vh.txtHargaDiskon = (TextView) convertView.findViewById(R.id.txtHargaDiskon);
            vh.txtDeskripsi   = (TextView) convertView.findViewById(R.id.txtDeskripsi);
            vh.txtLocation    = (TextView) convertView.findViewById(R.id.txtLocation);
            vh.txtKategori    = (TextView) convertView.findViewById(R.id.txtKategori);
            vh.txtContact	  = (TextView) convertView.findViewById(R.id.txtContact);
            vh.txtNoRekening  = (TextView) convertView.findViewById(R.id.txtNoRekening);
            vh.txtSellerId    = (TextView) convertView.findViewById(R.id.txtSellerId);
            vh.txtItemId      = (TextView) convertView.findViewById(R.id.txtItemId);

            vh.txtItemName.setTypeface(fontUbuntuB);
            vh.txtSeller.setTypeface(fontUbuntuL);
            vh.txtHargaDiskon.setTypeface(fontUbuntuL);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        double positionHeight = getPositionRatio(position);
        vh.imgItem.setHeightRatio(positionHeight);
        
        JSONArray jArray = null;
        try {
            jArray = new JSONArray(arrayPicture.get(position));
	        Glide.with(_context).load(Referensi.url + "/pictures/" + jArray.get(0).toString())
                    .placeholder(R.drawable.img_loader).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                    .dontAnimate().into(vh.imgItem);
		} catch (JSONException e) { e.printStackTrace(); }
        
        vh.txtHargaDiskon.setText("Rp "+Referensi.currencyFormater(Double.parseDouble(arrayHargaAsli.get(position))).replace(",", "."));
        vh.txtItemName.setText(arrayItemName.get(position));
        vh.txtSeller.setText(arrayUserName.get(position));
        vh.txtDeskripsi.setText(arrayDeskripsi.get(position));
        vh.txtLocation.setText(arrayLokasi.get(position));
        vh.txtKategori.setText(arrayCategoriName.get(position));
        vh.txtContact.setText(arrayContact.get(position));
        vh.txtNoRekening.setText(arrayStock.get(position));
        vh.txtSellerId.setText(arraySellerId.get(position));
        vh.txtItemId.setText(arrayItemId.get(position));
        
        vh.relItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				activity.startActivityForResult(new Intent(activity, ShopDetailActivity.class).
                        putExtra("ItemId", vh.txtItemId.getText().toString()).
                        putExtra("SellerId", vh.txtSellerId.getText().toString()).
                        putExtra("ItemPicture", arrayPicture.get(position)).
						putExtra("ItemName", vh.txtItemName.getText().toString()).
                        putExtra("HargaDiskon", vh.txtHargaDiskon.getText().toString()).
		        		putExtra("ItemSeller", vh.txtSeller.getText().toString()).
		        		putExtra("NoRekening", vh.txtNoRekening.getText().toString()).
		        		putExtra("ItemLocation", vh.txtLocation.getText().toString()).
		        		putExtra("ItemKategori", vh.txtKategori.getText().toString()).
		        		putExtra("ItemContact", vh.txtContact.getText().toString()).
						putExtra("ItemDeskripsi", vh.txtDeskripsi.getText().toString()), 1);
			}
		});
        
        return convertView;
    }

    static class ViewHolder {
    	LinearLayout relItem;
        DynamicHeightImageView imgItem;
        TextView txtItemName;
        TextView txtSeller;
        TextView txtHargaDiskon;
        TextView txtDeskripsi;
        TextView txtLocation;
        TextView txtKategori;
        TextView txtContact;
        TextView txtNoRekening;
        TextView txtSellerId;
        TextView txtItemId;
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

}