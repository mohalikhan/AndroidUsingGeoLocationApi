package scs2682.androidusinggeolocationapi.model;


import android.support.annotation.Nullable;

import org.json.JSONObject;

public class NetworkLookup {

    public final JSONObject json;

    public String countryIso2;
    public String stateAbr;
    public String postal;
    public String continent;
    public String state;
    public double longitude;
    public double latitude;
    public String ds;
    public String network;
    public String city;
    public String country;
    public String ip;

    public final boolean isEmpty;

    public NetworkLookup(@Nullable JSONObject json) {
        this.json = json != null ? json : new JSONObject();

        isEmpty = this.json.length() == 0;
        InitializeData();

        JSONObject restResponse = this.json.optJSONObject("RestResponse");
        if (restResponse != null && restResponse.length() > 0) {
            JSONObject result = restResponse.optJSONObject("result");

            if (result != null && result.length() > 0) {
                countryIso2 = result.optString("countryIso2", "");
                stateAbr = result.optString("stateAbbr", "");
                postal = result.optString("postal", "");
                continent = result.optString("continent", "");
                state = result.optString("state", "");
                longitude = result.optDouble("longitude", 0f);
                latitude = result.optDouble("latitude", 0f);
                ds = result.optString("ds", "");
                network = result.optString("network", "");
                city = result.optString("city", "");
                country = result.optString("country", "");
                ip = result.optString("ip", "");
            }
        }
    }

    private void InitializeData() {
        country = "";
        countryIso2 = "";
        stateAbr = "";
        postal = "";
        continent = "";
        state = "";
        longitude = 0f;
        latitude = 0f;
        ds ="";
        network = "";
        city = "";
        ip = "";
    }
}
