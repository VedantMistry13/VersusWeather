package com.versus.versusweather.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.google.gson.Gson;
import com.versus.versusweather.MainActivity;
import com.versus.versusweather.models.Location;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class LocationFetchAsyncTask extends AsyncTask<Void, Void, Void> {
    Context context;
    CardView cardView;

    public LocationFetchAsyncTask(Context context, CardView cardView) {
        super();
        this.context = context;
        this.cardView = cardView;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            InputStream inputStream = context.getAssets().open("city.list.min.json");
            int size = inputStream.available();
            byte[] byteBuffer = new byte[size];
            inputStream.read(byteBuffer);
            String jsonData = new String(byteBuffer, "UTF-8");
            Gson gson = new Gson();
            Location[] locations = gson.fromJson(jsonData, Location[].class);
            Collections.addAll(MainActivity.locationArrayList, locations);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        cardView.setVisibility(View.VISIBLE);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setInterpolator(context, android.R.interpolator.accelerate_decelerate);
        cardView.startAnimation(scaleAnimation);
    }
}
