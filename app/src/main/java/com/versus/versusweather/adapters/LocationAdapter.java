package com.versus.versusweather.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.versus.versusweather.R;
import com.versus.versusweather.asynctasks.LocationSearchAsyncTask;
import com.versus.versusweather.listeners.OnItemClickListener;
import com.versus.versusweather.models.Location;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private ArrayList<Location> locationArrayList;
    private LocationSearchAsyncTask locationSearchAsyncTask;
    private OnItemClickListener onItemClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView locationTextView;

        ViewHolder(View view) {
            super(view);
            locationTextView = view.findViewById(R.id.location_text);
            view.setOnClickListener(ViewHolder.this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null ) {
                onItemClickListener.onClick(v, locationArrayList.get(getAdapterPosition()));
            }
        }
    }


    public LocationAdapter() {
        this.locationArrayList = new ArrayList<>();
        this.locationSearchAsyncTask = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Location location = locationArrayList.get(position);
        holder.locationTextView.setText(location.getName() + ", " + location.getCountry());
    }

    @Override
    public int getItemCount() {
        return locationArrayList.size();
    }

    public void filter(String charText) {
        if (locationSearchAsyncTask != null) {
            locationSearchAsyncTask.cancel(true);
        }
        locationSearchAsyncTask = new LocationSearchAsyncTask(
                charText,
                locationArrayList,
                LocationAdapter.this
        );
        locationSearchAsyncTask.execute();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

