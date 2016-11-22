package scs2682.androidusinggeolocationapi.networkinfo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import scs2682.androidusinggeolocationapi.R;
import scs2682.androidusinggeolocationapi.model.NetworkLookup;

public class CellViewHolder extends RecyclerView.ViewHolder {
    private final TextView ipAddressTextView;
    private final TextView longitudeTextView;
    private final TextView latitudeTextView;

    private NetworkLookup networkLookup;

    public CellViewHolder(View view, final OnNetworkLookupInfoClickListener onNetworkLookupInfoClickListener) {
        super(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onNetworkLookupInfoClickListener != null) {
                    onNetworkLookupInfoClickListener.onNetworkLookupClick(networkLookup);
                }
            }
        });

        ipAddressTextView = (TextView) view.findViewById(R.id.ipAddress);
        longitudeTextView = (TextView) view.findViewById(R.id.longitude);
        latitudeTextView = (TextView) view.findViewById(R.id.latitude);
    }

    public void update(NetworkLookup networkLookup) {
        ipAddressTextView.setText(networkLookup.ip);
        longitudeTextView.setText(String.valueOf(networkLookup.longitude));
        latitudeTextView.setText(String.valueOf(networkLookup.latitude));

        this.networkLookup = networkLookup;
    }
}

