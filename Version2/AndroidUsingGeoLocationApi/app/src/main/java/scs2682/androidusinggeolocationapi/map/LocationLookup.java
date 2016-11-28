package scs2682.androidusinggeolocationapi.map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.WeakHashMap;

import scs2682.androidusinggeolocationapi.AppActivity;
import scs2682.androidusinggeolocationapi.R;
import scs2682.androidusinggeolocationapi.model.NetworkLookup;

public class LocationLookup extends LinearLayout implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private WeakHashMap<String, Marker> markers;
    private AppActivity.Adapter mainAdapter;

    public LocationLookup(Context context) {
        this(context, null, 0);
    }

    public LocationLookup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationLookup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    public void setMainAdapter(AppActivity.Adapter mainAdapter){
        this.mainAdapter = mainAdapter;
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
            Toast.makeText(getContext(), "No network available!", Toast.LENGTH_SHORT).show();
        }
    }

    //On map ready...
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        markers = new WeakHashMap<String, Marker>();

        //Default values for google map
        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);

        mainAdapter.addAllMarkers();
    }

    //load map when user click on list
    private void loadMap(@Nullable NetworkLookup networkLookup){
        //if network lookup has data
        if (networkLookup != null && networkLookup.latitude != 0 && networkLookup.longitude != 0) {
            //load positions
            LatLng location = new LatLng(networkLookup.latitude, networkLookup.longitude);
            Marker marker = getMarker(networkLookup.ip);
            //just in case marker is not added previously.
            if (marker == null) {
                marker = addMarker(networkLookup);
            }
            marker.showInfoWindow();

            //finally, load map with higher zoom.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f));
        }
    }

    public void removeMarker(@NonNull String key) {
        if (googleMap != null && markers.size() > 0) {
            for(int i = 0; i < markers.size(); i++) {
                Marker marker = markers.get(key);
                if (marker != null) {
                    markers.remove(key);
                    marker.remove();
                    break;
                }
            }
        }
    }

    public Marker addMarker(@NonNull NetworkLookup networkLookup) {
        if (googleMap != null) {
            LatLng location = new LatLng(networkLookup.latitude, networkLookup.longitude);
            Marker marker = googleMap.addMarker(new MarkerOptions().position(location)
                    .title(TextUtils.isEmpty(networkLookup.city) ? networkLookup.country : (networkLookup.city + "-" + networkLookup.countryIso2))
                    .snippet(networkLookup.ip));
            markers.put(networkLookup.ip, marker);
            return marker;
        }
        return null;
    }

    private Marker getMarker(@NonNull String key) {
        if (googleMap != null && markers.size() > 0) {
            for(int i = 0; i < markers.size(); i++) {
                Marker marker = markers.get(key);
                if (marker != null) {
                   return marker;
                }
            }
        }
        return null;
    }
}
