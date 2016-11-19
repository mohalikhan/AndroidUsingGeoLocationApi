package scs2682.androidusinggeolocationapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scs2682.androidusinggeolocationapi.model.NetworkLookup;

public class AppActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DownloadJsonTask downloadJsonTask;
    private static final String PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private static final class DownloadJsonTask extends AsyncTask<String, Void, NetworkLookup> {

        @NonNull
        private final WeakReference<AppActivity> appActivityWeakReference;

        private DownloadJsonTask(@NonNull AppActivity appActivity) {
            appActivityWeakReference = new WeakReference<>(appActivity);
        }

        @Override
        protected NetworkLookup doInBackground(String... urls) {
            String urlString = urls != null && urls.length > 0 ? urls[0] : "";

            if (TextUtils.isEmpty(urlString)) {
                return null;
            }

            InputStream inputStream;
            NetworkLookup networkLookup = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.connect();

                int statusCode = connection.getResponseCode();
                String statusMessage = connection.getResponseMessage();

                //Log staus code and message
                Log.w("DownloadJsonTask", "statusCode = " + statusCode + " and message = " + statusMessage);

                //get input stream
                inputStream = connection.getInputStream();

                //convert stream to text
                String jsonString = new Scanner(inputStream, "UTF-8")
                        .useDelimiter("\\A")
                        .next();

                //close input stream
                inputStream.close();

                //finally disconnect
                connection.disconnect();

                //get the json object
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject restResponse = jsonObject.optJSONObject("RestResponse");

                //if there is data loaded
                if (restResponse != null && restResponse.length() > 0) {
                    networkLookup = new NetworkLookup(jsonObject);
                }
            }
            catch(IOException | JSONException e) {
                networkLookup = null;
            }
            return networkLookup;
        }

        @Override
        protected void onPostExecute(NetworkLookup networkLookup) {
            if (appActivityWeakReference.get() != null && appActivityWeakReference.get().isValid()) {
                // activity is fine, call load Map;
                appActivityWeakReference.get().loadMap(networkLookup);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appactivity);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //Setup Google Map
        SetupMap(mapFragment);

        final TextView addressText = (TextView) findViewById(R.id.addressText);
        final Button lookupButton = (Button) findViewById(R.id.lookup);
        //Lookup click
        lookupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validation
                String ipAddress = addressText.getText().toString();
                if (TextUtils.isEmpty(ipAddress))
                {
                    Toast.makeText(AppActivity.this, "IP address is empty", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                else {
                    if (!validate(ipAddress))
                    {
                        Toast.makeText(AppActivity.this, "Invalid IP address", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                }

                try {
                    downloadJsonTask = new DownloadJsonTask(AppActivity.this);
                    downloadJsonTask.execute(String.format(getResources().getString(R.string.iplookupurl), ipAddress));
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (downloadJsonTask != null) {
            downloadJsonTask.cancel(true);
            downloadJsonTask = null;
        }
        super.onDestroy();
    }

    private void SetupMap(SupportMapFragment mapFragment) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Default values for google map
        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);
    }

    private void loadMap(@Nullable NetworkLookup networkLookup){
        //if network lookup has data
        if (networkLookup != null && networkLookup.latitude != 0 && networkLookup.longitude != 0) {
            //load positions
            LatLng location = new LatLng(networkLookup.latitude, networkLookup.longitude);
            mMap.addMarker(new MarkerOptions().position(location)
                    .title(networkLookup.city)
                    .snippet(networkLookup.country)).showInfoWindow();

            //finally, load map with higher zoom.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f));
        }
    }

    private static boolean validate(final String ip){
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    private boolean isValid() {
        return !isDestroyed() && !isFinishing();
    }
}
