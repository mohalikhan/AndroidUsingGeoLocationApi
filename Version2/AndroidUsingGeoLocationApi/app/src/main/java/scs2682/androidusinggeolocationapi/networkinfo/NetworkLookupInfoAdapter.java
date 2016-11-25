package scs2682.androidusinggeolocationapi.networkinfo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import scs2682.androidusinggeolocationapi.AppActivity;
import scs2682.androidusinggeolocationapi.R;
import scs2682.androidusinggeolocationapi.model.NetworkLookup;

public class NetworkLookupInfoAdapter extends RecyclerView.Adapter<CellViewHolder> implements OnViewHolderLongClickListener {
    public List<NetworkLookup> lookupList = new ArrayList<>();

    private final LayoutInflater layoutInflater;
    private final OnViewHolderClickListener onViewHolderClickListener;

    private AppActivity.Adapter mainAdapter;

    NetworkLookupInfoAdapter(Context context, OnViewHolderClickListener onViewHolderClickListener) {
        layoutInflater = LayoutInflater.from(context);
        this.onViewHolderClickListener = onViewHolderClickListener;

        setHasStableIds(true);
    }

    @Override
    public CellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.networkinfo_cell, parent, false);
        return new CellViewHolder(view, onViewHolderClickListener, this);
    }

    @Override
    public void onBindViewHolder(CellViewHolder holder, int position) {
        NetworkLookup networkLookup = lookupList.get(position);
        holder.update(networkLookup);
    }

    @Override
    public int getItemCount() {
        return lookupList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setMainAdapter(AppActivity.Adapter mainAdapter){
        this.mainAdapter = mainAdapter;
    }

    @Override
    public void onViewHolderLongClick(@NonNull NetworkLookup networkLookup, int position) {
        mainAdapter.removeCache(networkLookup.ip);
        lookupList.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, lookupList.size());
        notifyItemRangeChanged(position, lookupList.size());
    }
}
