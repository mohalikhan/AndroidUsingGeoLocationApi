package scs2682.androidusinggeolocationapi.networkinfo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import scs2682.androidusinggeolocationapi.R;
import scs2682.androidusinggeolocationapi.model.NetworkLookup;

public class CellViewHolder extends RecyclerView.ViewHolder {
    private final TextView ipAddress;
    private final TextView longitude;
    private final TextView latitude;

    public CellViewHolder(View view, final OnNetworkLookupInfoClickListener onNetworkLookupInfoClickListener) {
        super(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onNetworkLookupInfoClickListener != null) {
                    NetworkLookup networkLookup = new NetworkLookup(ipAddress.getText().toString(), Double.parseDouble(longitude.getText().toString()), Double.parseDouble(latitude.getText().toString()));
                    onNetworkLookupInfoClickListener.onNetworkLookupClick(networkLookup);
                }
            }
        });

        ipAddress = (TextView) view.findViewById(R.id.ipAddress);
        longitude = (TextView) view.findViewById(R.id.longitude);
        latitude = (TextView) view.findViewById(R.id.latitude);
    }

    public void update(NetworkLookup networkLookup, int position) {
        ipAddress.setText(networkLookup.ip);
        longitude.setText(String.valueOf(networkLookup.longitude));
        latitude.setText(String.valueOf(networkLookup.latitude));
    }
}

