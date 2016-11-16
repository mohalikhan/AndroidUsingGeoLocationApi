package scs2682.androidusinggeolocationapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AppActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appactivity);

        final TextView addressText = (TextView) findViewById(R.id.addressText);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        final Button lookupButton = (Button) findViewById(R.id.lookup);

        //Lookup click
        lookupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validation
                String address = addressText.getText().toString();
                if (TextUtils.isEmpty(address))
                {
                    Toast.makeText(AppActivity.this, "Address is empty", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                // first check do we have an internet connection
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context
                        .CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {

                    mapFragment.getMapAsync(AppActivity.this);
                }
                else {
                    // no network
                    Toast.makeText(AppActivity.this, "No network", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Default values for google map
        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);

        LatLng toronto = new LatLng(43.6667, -79.4167);
        mMap.addMarker(new MarkerOptions().position(toronto).title("Marker in Toronto"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toronto, 14.0f));
    }
}
