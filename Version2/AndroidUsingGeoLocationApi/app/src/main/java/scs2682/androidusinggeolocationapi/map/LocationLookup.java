package scs2682.androidusinggeolocationapi.map;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import scs2682.androidusinggeolocationapi.AppActivity;
import scs2682.androidusinggeolocationapi.R;
import scs2682.androidusinggeolocationapi.model.NetworkLookup;

public class LocationLookup extends LinearLayout implements OnMapReadyCallback {
    private AppActivity.Adapter adapter;
    private Context context;
    public double longitude;
    public double latitude;

    public LocationLookup(Context context) {
        this(context, null, 0);
    }

    public LocationLookup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationLookup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setAdapter(AppActivity.Adapter adapter){
        this.adapter = adapter;
    }

    public void updateMap(NetworkLookup networkLookup) {
        if (networkLookup != null) {
            this.longitude = networkLookup.longitude;
            this.latitude = networkLookup.latitude;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

//        final SupportMapFragment mapFragment = (SupportMapFragment) Activity.getSupportFragmentManager();
//                .findFragmentById(R.id.map);

        //Setup Google Map;
        //SetupMap(mapFragment);

    }

    private void SetupMap(SupportMapFragment mapFragment) {
        // first check do we have an internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            mapFragment.getMapAsync(this);
        }
        else {
            // no network
            Toast.makeText(getContext(), "No network", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
