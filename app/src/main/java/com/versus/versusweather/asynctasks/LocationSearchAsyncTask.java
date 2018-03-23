package com.versus.versusweather.asynctasks;

import android.os.AsyncTask;

import com.versus.versusweather.MainActivity;
import com.versus.versusweather.adapters.LocationAdapter;
import com.versus.versusweather.models.Location;

import java.util.ArrayList;
import java.util.Locale;

public class LocationSearchAsyncTask extends AsyncTask<Void, Void, Void> {
    ArrayList<Location> locationArrayList;
    LocationAdapter locationAdapter;
    String query;

    public LocationSearchAsyncTask(String query, ArrayList<Location> locationArrayList,
                                   LocationAdapter locationAdapter) {
        super();
        this.query = query;
        this.locationAdapter = locationAdapter;
        this.locationArrayList = locationArrayList;
    }

    @Override
    protected Void doInBackground(Void... params) {
        query = query.toLowerCase(Locale.getDefault());
        locationArrayList.clear();
        if (query.length() == 0) {
            locationArrayList.clear();
        } else {
            for (Location location: MainActivity.locationArrayList) {
                if (location.getName().toLowerCase(Locale.getDefault()).contains(query)) {
                    locationArrayList.add(location);
                    if (locationArrayList.size() == 3) {
                        break;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        locationAdapter.notifyDataSetChanged();
    }
}
