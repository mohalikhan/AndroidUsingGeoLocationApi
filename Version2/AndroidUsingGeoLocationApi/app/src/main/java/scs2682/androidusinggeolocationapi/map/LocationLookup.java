package scs2682.androidusinggeolocationapi.map;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import scs2682.androidusinggeolocationapi.AppActivity;
import scs2682.androidusinggeolocationapi.R;
import scs2682.androidusinggeolocationapi.model.NetworkLookup;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class LocationLookup extends LinearLayout implements OnMapReadyCallback {
    private AppActivity.Adapter adapter;

    private GoogleMap googleMap;

    public LocationLookup(Context context) {
        this(context, null, 0);
    }

    public LocationLookup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationLookup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(AppActivity.Adapter adapter){
        this.adapter = adapter;
    }

    public void updateMap(NetworkLookup networkLookup) {
        if (networkLookup != null) {
            loadMap(networkLookup);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final SupportMapFragment mapFragment = (SupportMapFragment) ((AppCompatActivity)getContext()).getSupportFragmentManager().findFragmentById(R.id.map);

        //Setup Google Map;
        SetupMap(mapFragment);
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
        this.googleMap = googleMap;

        //Default values for google map
        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);
    }

    private void loadMap(@Nullable NetworkLookup networkLookup){
        //if network lookup has data
        if (networkLookup != null && networkLookup.latitude != 0 && networkLookup.longitude != 0) {
            //load positions
            LatLng location = new LatLng(networkLookup.latitude, networkLookup.longitude);
            googleMap.addMarker(new MarkerOptions().position(location)
                    .title(networkLookup.city)
                    .snippet(networkLookup.country)).showInfoWindow();

            //finally, load map with higher zoom.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f));
        }
    }
}
