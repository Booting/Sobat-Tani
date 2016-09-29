package com.sobattani;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sobattani.Utils.ImageLoader;
import com.sobattani.Utils.LoopViewPager;
import com.sobattani.Utils.Referensi;
import com.viewpagerindicator.CirclePageIndicator;
import org.json.JSONArray;
import org.json.JSONException;

public class PhotoViewer extends Activity {
	private static final String ISLOCKED_ARG = "isLocked";
    private LoopViewPager mViewPager;
    private JSONArray jsonImages;
    private ImageLoader imgLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.no_anim, R.anim.no_anim);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photo_viewer);
        
        Intent intent     = getIntent();
        String jsonArray  = intent.getStringExtra("image_list");
        int imagePosition = intent.getIntExtra("position", 0);
        
        try {
            jsonImages = new JSONArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        imgLoader  = new ImageLoader(this);
        mViewPager = (LoopViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new PhotoPagerAdapter(PhotoViewer.this));

        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((LoopViewPager) mViewPager).setLocked(isLocked);
        }
        
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        indicator.setCurrentItem(imagePosition);
        
        RelativeLayout relClose = (RelativeLayout) findViewById(R.id.relClose);
        relClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    class PhotoPagerAdapter extends PagerAdapter {
        LayoutInflater inflater = null;
        Context ctx;

        public PhotoPagerAdapter(Context ctx) {
            this.ctx = ctx;
            inflater = LayoutInflater.from(this.ctx);
        }

        @Override
        public int getCount() {
            return jsonImages.length();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            if (jsonImages.optString(position).contains("https")) {
            	Glide.with(PhotoViewer.this).load(jsonImages.optString(position)).placeholder(R.drawable.img_loader)
                        .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate().into(imageView);
            } else {
                Glide.with(PhotoViewer.this).load(Referensi.URL_CLOUDINARY + jsonImages.optString(position)).placeholder(R.drawable.img_loader)
                        .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate().into(imageView);
            }
            
            container.addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private boolean isViewPagerActive() {
        return (mViewPager != null && mViewPager instanceof LoopViewPager);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG, ((LoopViewPager) mViewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }

}
