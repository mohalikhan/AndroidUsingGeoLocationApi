package scs2682.androidusinggeolocationapi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import scs2682.androidusinggeolocationapi.map.LocationLookup;
import scs2682.androidusinggeolocationapi.model.NetworkLookup;
import scs2682.androidusinggeolocationapi.networkinfo.NetworkLookupInfo;

public class AppActivity extends AppCompatActivity {

    private Adapter adapter;
    private ViewPager viewPager;
    private static final class Page {
        private final int layoutId;

        private Page(final int layoutId) {
            this.layoutId = layoutId;
        }
    }

    public static final class Adapter extends PagerAdapter {

        private static final int POSITION_NETWORKLOOKUPINFO = 0;
        private static final int POSITION_LOCATIONLOOKUP = 1;

        private final List<Page> pages;
        private final LayoutInflater layoutInflater;

        private final ViewPager viewPager;
        private NetworkLookupInfo networkLookupInfo;
        private LocationLookup locationLookup;

        private Adapter(ViewPager viewPager) {
            Context context = viewPager.getContext();

            this.viewPager = viewPager;
            layoutInflater = LayoutInflater.from(context);

            pages = new ArrayList<>(2);
            pages.add(POSITION_NETWORKLOOKUPINFO, new Page(R.layout.networklookupinfo));
            pages.add(POSITION_LOCATIONLOOKUP, new Page(R.layout.locationlookup));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Page page = pages.get(position);
            View view = layoutInflater.inflate(page.layoutId, container, false);
            container.addView(view);

            if (view instanceof NetworkLookupInfo) {
                networkLookupInfo = (NetworkLookupInfo) view;
                networkLookupInfo.setMainAdapter(this);
            } else if (view instanceof LocationLookup) {
                locationLookup = (LocationLookup) view;
                locationLookup.setMainAdapter(this);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        public void onOpenMap(NetworkLookup networkLookup) {
            viewPager.setCurrentItem(POSITION_LOCATIONLOOKUP);
            locationLookup.updateMap(networkLookup);
        }

        public void onNetworkLookupUpdated() {
            viewPager.setCurrentItem(POSITION_NETWORKLOOKUPINFO);
        }

        public void removeCache(String key) {
            networkLookupInfo.removeCache(key);
        }

        public void removeMarker(String key) {
            locationLookup.removeMarker(key);
        }

        public void addMarker(@Nullable NetworkLookup networkLookup) {
            if (networkLookup != null) {
                locationLookup.addMarker(networkLookup);
            }
        }

        public void addAllMarkers() {
            networkLookupInfo.addAllMarkers();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appactivity);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new Adapter(viewPager);
        viewPager.setAdapter(adapter);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        if (keyCode == KeyEvent.KEYCODE_BACK && viewPager.getCurrentItem() == 1) {
            viewPager.setCurrentItem(0, true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure, you want to close application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                }).setNegativeButton("No", null).show();
    }
}
