package scs2682.androidusinggeolocationapi.networkinfo;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import scs2682.androidusinggeolocationapi.R;
import scs2682.androidusinggeolocationapi.model.NetworkLookup;

public class CellViewHolder extends RecyclerView.ViewHolder {
    private final TextView addressTextView;
    private final TextView continentTextView;
    private final TextView ipAddressTextView;
    private final TextView positionTextView;

    private NetworkLookup networkLookup;

    public CellViewHolder(View view, final OnNetworkLookupInfoClickListener onNetworkLookupInfoClickListener,
                          final OnViewHolderLongClickListener onViewHolderLongClickListener) {
        super(view);

        //go to map
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onNetworkLookupInfoClickListener != null) {
                    onNetworkLookupInfoClickListener.onNetworkLookupClick(networkLookup);
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

    public void update(NetworkLookup networkLookup, boolean orientationLand) {
        addressTextView.setText(getAddress(networkLookup));
        continentTextView.setText(networkLookup.continent);
        ipAddressTextView.setText(Html.fromHtml(getIpAddress(networkLookup)));
        positionTextView.setText(Html.fromHtml(getPosition(networkLookup)));

        //keep object reference
        this.networkLookup = networkLookup;
    }

    private String getAddress(NetworkLookup networkLookup) {
        StringBuilder address = new StringBuilder();
        if (!networkLookup.city.equals(""))
        {
            address.append(networkLookup.city);
        }

        if (!networkLookup.state.equals(""))
        {
            if (address.length() > 0) {
                address.append(", ");
            }
            address.append(networkLookup.state);
        }

        if (!networkLookup.country.equals(""))
        {
            if (address.length() > 0) {
                address.append(", ");
            }
            address.append(networkLookup.country);
        }
        return address.toString();
    }

    private String getIpAddress(NetworkLookup networkLookup) {
        StringBuilder sb = new StringBuilder();
        sb.append(networkLookup.ip);
        sb.append((networkLookup.network == "" ? "" : "  " + "<i><font color='#000000'>" +  networkLookup.network+ "</font></i>"));
        return sb.toString();
    }

    private String getPosition(NetworkLookup networkLookup) {
        StringBuilder sb = new StringBuilder();
        sb.append("Location: ");
        sb.append("<font color='#000000'>" + String.valueOf(networkLookup.latitude) + ", " + String.valueOf(networkLookup.longitude) + "</font>");
        sb.append((networkLookup.postal == "" ? "" : " Postal Code: " + "<font color='#000000'>" + networkLookup.postal+ "</font>"));
        return sb.toString();
    }
}

