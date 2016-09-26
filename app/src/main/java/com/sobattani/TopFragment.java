package com.sobattani;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.TypeFaceSpan;
import com.viewpagerindicator.UnderlinePageIndicator;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class TopFragment extends AppCompatActivity {
    private ViewPager viewPager;
    private UnderlinePageIndicator underlinePageIndicator;
    private Button btnForum, btnTrending, btnRank;
    private Typeface fontUbuntuL, fontUbuntuB;
    private ArrayList<String> arrMenu = new ArrayList<String>();
    private Toolbar toolbar;
    private static TopFragment activityInstance;
    public static TopFragment getInstance() {
        return activityInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.fragment_top);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initToolbar();

        activityInstance = this;
        fontUbuntuL 	 = FontCache.get(TopFragment.this, "Ubuntu-L");
        fontUbuntuB 	 = FontCache.get(TopFragment.this, "Ubuntu-B");

        arrMenu.add("{\"cat_name\":\"Diskusi Sobat\"}");
        arrMenu.add("{\"cat_name\":\"Teramai\"}");
        arrMenu.add("{\"cat_name\":\"Teraktif\"}");

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setTag("pager");
        viewPager.setOffscreenPageLimit(6);
        viewPager.setAdapter(new EventPagerAdapter(getSupportFragmentManager()));

        btnForum = (Button) findViewById(R.id.btnForum);
        btnForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0) {
                    btnForum.setBackgroundColor(getResources().getColor(R.color.colorBgSelected));
                } else {
                    viewPager.setCurrentItem(0);
                }
            }
        });
        viewPager.setCurrentItem(0);

        btnTrending = (Button) findViewById(R.id.btnTrending);
        btnTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);

            }
        });

        btnRank = (Button) findViewById(R.id.btnRank);
        btnRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);

            }
        });

        btnForum.setTypeface(fontUbuntuB);
        btnTrending.setTypeface(fontUbuntuB);
        btnRank.setTypeface(fontUbuntuB);

        underlinePageIndicator = (UnderlinePageIndicator) findViewById(R.id.underlinePageIndicator);
        underlinePageIndicator.setFades(false);
        underlinePageIndicator.setSelectedColor(getResources().getColor(R.color.colorSelected));
        underlinePageIndicator.setViewPager(viewPager);

        underlinePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                if (position==0) {
                    btnRank.setBackgroundColor(getResources().getColor(R.color.colorBgUnselected));
                    btnRank.setTextColor(getResources().getColor(R.color.colorUnselected));
                    btnTrending.setBackgroundColor(getResources().getColor(R.color.colorBgUnselected));
                    btnTrending.setTextColor(getResources().getColor(R.color.colorUnselected));
                    btnForum.setBackgroundColor(getResources().getColor(R.color.colorBgSelected));
                    btnForum.setTextColor(getResources().getColor(R.color.colorSelected));
                } else if (position==1) {
                    btnRank.setBackgroundColor(getResources().getColor(R.color.colorBgUnselected));
                    btnRank.setTextColor(getResources().getColor(R.color.colorUnselected));
                    btnTrending.setBackgroundColor(getResources().getColor(R.color.colorBgSelected));
                    btnTrending.setTextColor(getResources().getColor(R.color.colorSelected));
                    btnForum.setBackgroundColor(getResources().getColor(R.color.colorBgUnselected));
                    btnForum.setTextColor(getResources().getColor(R.color.colorUnselected));
                } else {
                    btnRank.setBackgroundColor(getResources().getColor(R.color.colorBgSelected));
                    btnRank.setTextColor(getResources().getColor(R.color.colorSelected));
                    btnTrending.setBackgroundColor(getResources().getColor(R.color.colorBgUnselected));
                    btnTrending.setTextColor(getResources().getColor(R.color.colorUnselected));
                    btnForum.setBackgroundColor(getResources().getColor(R.color.colorBgUnselected));
                    btnForum.setTextColor(getResources().getColor(R.color.colorUnselected));
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString(getString(R.string.menu_tanya_sobat));
        spanToolbar.setSpan(new TypeFaceSpan(TopFragment.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    public class EventPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
        public EventPagerAdapter(FragmentManager fm) { super(fm); }
        @Override
        public Fragment getItem(int position) {
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(arrMenu.get(position));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //root
            if (jObject.optString("cat_name").equalsIgnoreCase("Diskusi Sobat")) {
                return ForumFragment.newInstance();
            } else if (jObject.optString("cat_name").equalsIgnoreCase("Teramai")) {
                return TrendingFragment.newInstance();
            } else {
                return RankFragment.newInstance();
            }
        }
        @Override
        public int getCount() { return 3; }
        @Override
        public CharSequence getPageTitle(int position) { return ""; }
        @Override
        public void onPageScrollStateChanged(int arg0) { }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) { }
        @Override
        public void onPageSelected(int arg0) {}
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