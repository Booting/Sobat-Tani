package com.sobattani.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobattani.CuacaActivity;
import com.sobattani.FindFriendLocationActivity;
import com.sobattani.LoginActivity;
import com.sobattani.PetaSebaranOPTActivity;
import com.sobattani.ProfileActivity;
import com.sobattani.R;
import com.sobattani.ShopActivity;
import com.sobattani.TopFragment;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.Referensi;

public class MenuAdapter extends BaseAdapter {

    private final LayoutInflater mLayoutInflater;
    private Context context;
    private Typeface fontDroidSandBold;
    private Activity activity;
	private boolean isAMember;
    private SharedPreferences sobatTaniPref;
    
    public MenuAdapter(Context context, boolean mIsAMember) {
        this.mLayoutInflater   = LayoutInflater.from(context);
        this.context           = context;
		this.fontDroidSandBold = FontCache.get(context, "DroidSans-Bold");
		this.activity		   = (Activity) context;
		this.isAMember		   = mIsAMember;
		this.sobatTaniPref 	   = activity.getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
    }
    
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.menu_list_cell, parent, false);
            vh = new ViewHolder();

            vh.imgMenu = (ImageView) convertView.findViewById(R.id.imgMenu);
            vh.txtMenu = (TextView) convertView.findViewById(R.id.txtMenu);
            vh.relItem = (RelativeLayout) convertView.findViewById(R.id.relItem);
            
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.txtMenu.setTypeface(fontDroidSandBold);

        if (position==0) {
        	vh.imgMenu.setImageResource(R.drawable.menu_enam);
        	vh.txtMenu.setText("DATA SOBAT");
        } else if (position==1) {
        	vh.imgMenu.setImageResource(R.drawable.menu_lima);
        	vh.txtMenu.setText("CARI SOBAT");
        } else if (position==2) {
			vh.imgMenu.setImageResource(R.drawable.menu_baru);
			vh.txtMenu.setText("TANYA SOBAT");
		} else if (position==3) {
        	vh.imgMenu.setImageResource(R.drawable.menu_satu);
        	vh.txtMenu.setText("PETA SEBARAN OPT");
        } else if (position==4) {
        	vh.imgMenu.setImageResource(R.drawable.jual_beli);
        	vh.txtMenu.setText("PASAR SOBAT");
        } else if (position==5) {
        	vh.imgMenu.setImageResource(R.drawable.informasi_cuaca);
        	vh.txtMenu.setText("INFORMASI CUACA");
        } else if (position==6) {
        	vh.imgMenu.setImageResource(R.drawable.menu_dua);
        	vh.txtMenu.setText("KELUAR");
        }
        
        vh.relItem.setOnClickListener(new OnClickListener() {
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (position==0) {
					activity.startActivity(new Intent(activity, ProfileActivity.class));
				} else if (position==1) {
					if (isAMember) {
						activity.startActivity(new Intent(activity, FindFriendLocationActivity.class));
					} else {
						showNotification();
					}
				} else if (position==2) {
					if (isAMember) {
						activity.startActivity(new Intent(activity, TopFragment.class));
					} else {
						showNotification();
					}
				} else if (position==3) {
					if (isAMember) {
						activity.startActivity(new Intent(activity, PetaSebaranOPTActivity.class));
					} else {
						showNotification();
					}
				} else if (position==4) {
					if (isAMember) {
						activity.startActivity(new Intent(activity, ShopActivity.class));
					} else {
						showNotification();
					}
				} else if (position==5) {
					if (isAMember) {
						activity.startActivity(new Intent(activity, CuacaActivity.class));
					} else {
						showNotification();
					}
				} else {
					SharedPreferences.Editor editor = sobatTaniPref.edit();
					editor.putString("UserId", "");
					editor.putString("UserName", "");
					editor.putString("Email", "");
					editor.commit();
					
					Intent intent = new Intent(activity, LoginActivity.class);
					activity.startActivity(intent);
					activity.finish();
				}
			}
		});
        
        return convertView;
    }
    
    public void showNotification() {
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Maaf");
        alertDialog.setMessage("Mohon isi Biodata Anda di Data Sobat!");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	dialog.cancel();
            }
        });
        alertDialog.setCancelable(false);
        if (!activity.isFinishing()) { alertDialog.show(); }
    }

    static class ViewHolder {
    	ImageView imgMenu;
        TextView txtMenu;
        RelativeLayout relItem;
    }

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}