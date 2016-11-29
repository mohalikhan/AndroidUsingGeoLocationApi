package scs2682.androidusinggeolocationapi.networkinfo;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import scs2682.androidusinggeolocationapi.R;
import scs2682.androidusinggeolocationapi.model.NetworkLookup;

public class CellViewHolder extends RecyclerView.ViewHolder {
    private final TextView addressTextView;
    private final TextView continentTextView;
    private final TextView ipAddressTextView;
    private final TextView positionTextView;

    private NetworkLookup networkLookup;

    public CellViewHolder(View view, final OnViewHolderClickListener onViewHolderClickListener,
                          final OnViewHolderLongClickListener onViewHolderLongClickListener) {
        super(view);

        //go to map
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onViewHolderClickListener != null) {
                    onViewHolderClickListener.onViewHolderClick(networkLookup);
                }
            }
        });

        //delete record
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Delete Info ?")
                        .setMessage("Do you want to delete the record ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onViewHolderLongClickListener.onViewHolderLongClick(networkLookup, getAdapterPosition());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //do nothing.
                            }
                        })
                        .show();
                return true;
            }
        });

        addressTextView = (TextView) view.findViewById(R.id.address);
        continentTextView = (TextView) view.findViewById(R.id.continent);
        ipAddressTextView = (TextView) view.findViewById(R.id.ipAddress);
        positionTextView = (TextView) view.findViewById(R.id.position);
    }

    public void update(NetworkLookup networkLookup) {
        addressTextView.setText(getAddress(networkLookup));
        continentTextView.setText(networkLookup.continent);
        ipAddressTextView.setText(Html.fromHtml(getIpAddress(networkLookup)));
        positionTextView.setText(Html.fromHtml(getPosition(networkLookup)));

        //keep object reference
        this.networkLookup = networkLookup;
    }

    @NonNull
    private String getAddress(NetworkLookup networkLookup) {
        StringBuilder address = new StringBuilder();
        if (!TextUtils.isEmpty(networkLookup.city))
        {
            address.append(networkLookup.city);
        }

        if (!TextUtils.isEmpty(networkLookup.state))
        {
            if (address.length() > 0) {
                address.append(", ");
            }
            address.append(networkLookup.state);
        }

        if (!TextUtils.isEmpty(networkLookup.country))
        {
            if (address.length() > 0) {
                address.append(", ");
            }
            address.append(networkLookup.country);
        }
        return address.toString();
    }

    @NonNull
    private String getIpAddress(NetworkLookup networkLookup) {
        StringBuilder sb = new StringBuilder();
        sb.append(networkLookup.ip);
        sb.append(TextUtils.isEmpty(networkLookup.network) ? "" : "  " + "<i><font color='#000000'>" +  networkLookup.network+ "</font></i>");
        return sb.toString();
    }

    @NonNull
    private String getPosition(NetworkLookup networkLookup) {
        StringBuilder sb = new StringBuilder();
        sb.append("Location: ");
        sb.append("<font color='#000000'>" + String.valueOf(networkLookup.latitude) + ", " + String.valueOf(networkLookup.longitude) + "</font>");
        sb.append((TextUtils.isEmpty(networkLookup.postal) ? "" : " Postal Code: " + "<font color='#000000'>" + networkLookup.postal+ "</font>"));
        return sb.toString();
    }
}

