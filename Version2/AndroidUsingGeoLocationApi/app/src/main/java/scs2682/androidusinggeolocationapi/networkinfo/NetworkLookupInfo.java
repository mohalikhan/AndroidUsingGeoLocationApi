package scs2682.androidusinggeolocationapi.networkinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scs2682.androidusinggeolocationapi.AppActivity;
import scs2682.androidusinggeolocationapi.R;
import scs2682.androidusinggeolocationapi.model.NetworkLookup;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class NetworkLookupInfo extends LinearLayout implements OnNetworkLookupInfoClickListener{

    private static final class DownloadJsonTask extends AsyncTask<String, Void, NetworkLookup> {

        @NonNull
        private final WeakReference<NetworkLookupInfo> networkLookupInfoWeakReference;
        private ProgressDialog progress;
        private DownloadJsonTask(@NonNull NetworkLookupInfo networkLookupInfo) {
            networkLookupInfoWeakReference = new WeakReference<>(networkLookupInfo);
        }

        //background process for json..
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
            catch(NoSuchElementException | IOException | JSONException e) {
                networkLookup = null;
            }
            return networkLookup;
        }

        //before data load, show progree bar, hides keyboard
        @Override
        protected void onPreExecute() {
            //Hide keyboard
            InputMethodManager inputMethodManager = (InputMethodManager)networkLookupInfoWeakReference.get().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(networkLookupInfoWeakReference.get().getWindowToken(), 0);

            if (progress == null) {
                progress = new ProgressDialog(networkLookupInfoWeakReference.get().getContext());
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            }
            progress.show();
        }

        //when data receive...
        @Override
        protected void onPostExecute(NetworkLookup networkLookup) {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }

            if (networkLookupInfoWeakReference.get() != null) {
               networkLookupInfoWeakReference.get().updateNetworkInfo(networkLookup);
            }
        }
    }

    private AppActivity.Adapter mainAdapter;
    private final List<NetworkLookup> lookupList = new ArrayList<>();
    private final NetworkLookupInfoAdapter networkLookupInfoAdapter;
    private static final String PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private DownloadJsonTask downloadJsonTask;

    public NetworkLookupInfo(Context context) {
        this(context, null, 0);
    }

    public NetworkLookupInfo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkLookupInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        networkLookupInfoAdapter = new NetworkLookupInfoAdapter(context, this);
    }

    public void updateNetworkInfo(NetworkLookup networkLookup) {
        mainAdapter.onNetworkLookupUpdated();
        if (networkLookup != null) {
            if (validateLookupInfo(networkLookup)) {
                networkLookupInfoAdapter.lookupList.add(networkLookup);
                networkLookupInfoAdapter.notifyItemInserted(networkLookupInfoAdapter.lookupList.size() - 1);
            }
        }
    }

    private boolean validateLookupInfo(NetworkLookup networkLookup) {
        //if no location
        if (networkLookup.latitude == 0.0 || networkLookup.longitude == 0.0) {
            Toast.makeText(getContext(), "No location of this ip address!", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        //if same ip address has been added.
        final int size = networkLookupInfoAdapter.lookupList.size();
        if ( size > 0) {
            for(int i = 0; i < size; i++){
                NetworkLookup lookup = networkLookupInfoAdapter.lookupList.get(i);
                if (lookup.ip.equals(networkLookup.ip)) {
                    Toast.makeText(getContext(), "Data already exists!", Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
            }
        }
        return true;
    }

    public void setMainAdapter(AppActivity.Adapter mainAdapter){
        this.mainAdapter = mainAdapter;
        networkLookupInfoAdapter.setMainAdapter(mainAdapter);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final TextView ipAddressText = (TextView) findViewById(R.id.addressText);

        findViewById(R.id.lookup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validation
                String ipAddress = ipAddressText.getText().toString();
                if (TextUtils.isEmpty(ipAddress))
                {
                    Toast.makeText(getContext(), "IP address is empty", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                else {
                    if (!validate(ipAddress))
                    {
                        Toast.makeText(getContext(), "Invalid IP address", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                }

                try {
                    downloadJsonTask = new DownloadJsonTask(NetworkLookupInfo.this);
                    downloadJsonTask.execute(String.format(getResources().getString(R.string.iplookupurl), ipAddress));
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(networkLookupInfoAdapter);
    }

    @Override
    public void onNetworkLookupClick(@NonNull NetworkLookup networkLookup) {
        if (mainAdapter != null){
            mainAdapter.onOpenMap(networkLookup);
        }
    }

    private static boolean validate(final String ip){
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}
