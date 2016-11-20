package scs2682.androidusinggeolocationapi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import scs2682.androidusinggeolocationapi.map.LocationLookup;
import scs2682.androidusinggeolocationapi.model.NetworkLookup;
import scs2682.androidusinggeolocationapi.networkinfo.NetworkLookupInfo;

public class AppActivity extends AppCompatActivity {

    private static final class Page {
        private final int layoutId;

        private Page(final int layoutId, @NonNull final String title) {
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

            pages = new ArrayList<>(1);
            pages.add(POSITION_NETWORKLOOKUPINFO, new Page(R.layout.networklookupinfo, ""));
            //pages.add(POSITION_LOCATIONLOOKUP, new Page(R.layout.locationlookup, ""));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Page page = pages.get(position);
            View view = layoutInflater.inflate(page.layoutId, container, false);
            container.addView(view);

           if (view instanceof NetworkLookupInfo) {
                networkLookupInfo = (NetworkLookupInfo) view;
                networkLookupInfo.setAdapter(this);
            }
            /*else if (view instanceof LocationLookup) {
               locationLookup = (LocationLookup) view;
               locationLookup.setAdapter(this);
            }*/
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

        public void onOpenMap(NetworkLookup networkLookup, int positionInNetworkLookupInfo) {
            viewPager.setCurrentItem(POSITION_LOCATIONLOOKUP);
            locationLookup.updateContact(networkLookup, positionInNetworkLookupInfo);
        }

        public void onNetworkLookupUpdated(NetworkLookup networkLookup, int positionInNetworkLookupInfo) {
            viewPager.setCurrentItem(POSITION_NETWORKLOOKUPINFO);
            networkLookupInfo.updateContact(networkLookup, positionInNetworkLookupInfo);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appactivity);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new Adapter(viewPager));
    }
}
