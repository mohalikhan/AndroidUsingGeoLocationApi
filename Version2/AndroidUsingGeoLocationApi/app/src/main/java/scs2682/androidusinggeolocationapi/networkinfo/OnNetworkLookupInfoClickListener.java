package scs2682.androidusinggeolocationapi.networkinfo;

import android.support.annotation.NonNull;

import scs2682.androidusinggeolocationapi.model.NetworkLookup;

public interface OnNetworkLookupInfoClickListener {
    void onNetworkLookupClick(@NonNull NetworkLookup networkLookup);
}
