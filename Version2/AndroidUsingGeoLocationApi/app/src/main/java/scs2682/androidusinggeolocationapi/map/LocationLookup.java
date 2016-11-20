package scs2682.androidusinggeolocationapi.map;

import android.content.Context;
import android.widget.LinearLayout;

import scs2682.androidusinggeolocationapi.AppActivity;
import scs2682.androidusinggeolocationapi.model.NetworkLookup;

public class LocationLookup extends LinearLayout {
    private AppActivity.Adapter adapter;

    public LocationLookup(Context context) {
        super(context);
    }

    public void setAdapter(AppActivity.Adapter adapter){
        this.adapter = adapter;
    }

    public void updateContact(NetworkLookup networkLookup, int positionInContacts) {
    }
}
