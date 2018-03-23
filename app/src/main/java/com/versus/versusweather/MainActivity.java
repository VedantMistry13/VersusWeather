package com.versus.versusweather;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.versus.versusweather.adapters.LocationAdapter;
import com.versus.versusweather.asynctasks.LocationFetchAsyncTask;
import com.versus.versusweather.listeners.OnItemClickListener;
import com.versus.versusweather.models.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener, OnItemClickListener {
    CardView searchCard;
    CardView weatherCard;

    DrawerLayout drawerLayout;

    ImageButton appLogo;
    ImageButton expandWeatherCard;
    ImageButton collapseWeatherCard;

    ImageView weatherImage;

    NavigationView navigationView;

    RecyclerView locationList;

    SearchView locationSearch;

    Switch metricSwitch;

    SwipeRefreshLayout swipeRefreshLayout;

    TextView locationTextView;
    TextView calculatedAtTextView;
    TextView temperature;
    TextView weatherTypeTextView;
    TextView windDetails;
    TextView cloudinessTextView;
    TextView latitudeTextView;
    TextView longitudeTextView;

    LocationAdapter locationAdapter;

    boolean isMetricCelsius;

    public static ArrayList<Location> locationArrayList;
    public static String id = "1275339";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isMetricCelsius = true;

        drawerLayout = findViewById(R.id.drawer_layout);
        appLogo = findViewById(R.id.app_logo);
        searchCard = findViewById(R.id.search_card);
        weatherCard = findViewById(R.id.weather_card);
        expandWeatherCard = findViewById(R.id.expand_weather_card);
        collapseWeatherCard = findViewById(R.id.collapse_weather_card);
        weatherImage = findViewById(R.id.weather_image);
        locationTextView = findViewById(R.id.location);
        calculatedAtTextView = findViewById(R.id.calculated_at);
        temperature = findViewById(R.id.temperature);
        weatherTypeTextView = findViewById(R.id.weather_type);
        windDetails = findViewById(R.id.wind_details);
        cloudinessTextView = findViewById(R.id.cloudiness);
        latitudeTextView = findViewById(R.id.latitude);
        longitudeTextView = findViewById(R.id.longitude);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        metricSwitch = headerView.findViewById(R.id.metric_switch);
        locationSearch = findViewById(R.id.location_search);
        locationList = findViewById(R.id.location_list);

        locationAdapter = new LocationAdapter();
        locationArrayList = new ArrayList<>();

        appLogo.setOnClickListener(MainActivity.this);
        expandWeatherCard.setOnClickListener(MainActivity.this);
        collapseWeatherCard.setOnClickListener(MainActivity.this);
        metricSwitch.setOnCheckedChangeListener(MainActivity.this);
        locationSearch.setOnQueryTextListener(MainActivity.this);
        locationAdapter.setOnItemClickListener(MainActivity.this);
        locationList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        locationList.setItemAnimator(new DefaultItemAnimator());
        locationList.setAdapter(locationAdapter);

        prepareLocationData();
        getWeatherFromAPI();
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(MainActivity.this);
    }

    public void prepareLocationData() {
        new LocationFetchAsyncTask(this.getApplicationContext(), searchCard).execute();
    }

    @Override
    public void onRefresh() {
        getWeatherFromAPI();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void getWeatherFromAPI() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                "http://api.openweathermap.org/data/2.5/weather?id="
                        + id
                        + "&APPID=bd5e378503939ddaee76f12ad7a97608",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            String response = new String(responseBody, "UTF-8");
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject coordObject = jsonObject.getJSONObject("coord");
                            JSONObject mainObject = jsonObject.getJSONObject("main");
                            JSONObject windObject = jsonObject.getJSONObject("wind");
                            JSONObject sysObject = jsonObject.getJSONObject("sys");
                            JSONObject cloudsObject = jsonObject.getJSONObject("clouds");
                            JSONArray weatherArray = jsonObject.getJSONArray("weather");
                            JSONObject weatherObject = weatherArray.getJSONObject(0);

                            String name = jsonObject.getString("name");
                            String country = sysObject.getString("country");
                            String location = String.format(
                                    Locale.getDefault(),
                                    "%s, %s",
                                    name, country
                            );
                            String dataCalculatedAt = jsonObject.getString("dt");
                            Date date = new Date(Long.parseLong(dataCalculatedAt));
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                                    "EEEE, MMMM dd - HH:mm",
                                    Locale.getDefault()
                            );
                            String calculatedAt = simpleDateFormat.format(date);
                            String temp = mainObject.getString("temp");
                            String weatherInDegrees
                                    =   isMetricCelsius
                                        ? String.format(
                                                Locale.getDefault(),
                                                "%d",
                                                (int) (Double.parseDouble(temp) - 273.15)
                                            ) + "°C"
                                        : String.format(
                                                Locale.getDefault(),
                                                "%d",
                                                (int) (Math.round(
                                                        (9.0 / 5.0)
                                                        * (Double.parseDouble(temp) - 273.15)
                                                ) + 32)
                                            ) + "°F";
                            String icon = weatherObject.getString("icon");
                            String weatherType =  weatherObject.getString("main");
                            for (int i = 1; i < weatherArray.length(); i++) {
                                weatherObject = weatherArray.getJSONObject(i);
                                String weatherTypeExtra =  weatherObject.getString("main");
                                weatherType = weatherType + " | " + weatherTypeExtra;
                            }
                            String windSpeed = windObject.getString("speed");
                            String windDirectionInDegree = windObject.getString("deg");
                            String cloudiness = cloudsObject.getString("all");
                            String lat = coordObject.getString("lat");
                            String lon = coordObject.getString("lon");

                            Glide.with(MainActivity.this)
                                    .load("http://openweathermap.org/img/w/" + icon + ".png")
                                    .into(weatherImage);
                            locationTextView.setText(location);
                            calculatedAtTextView.setText(calculatedAt);
                            temperature.setText(weatherInDegrees);
                            weatherTypeTextView.setText(weatherType);
                            windDetails.setText(
                                    "Wind: " + windSpeed + "m/s, " + windDirectionInDegree + "°"
                            );
                            cloudinessTextView.setText("Cloudiness: " + cloudiness + "%");
                            latitudeTextView.setText("Latitude: " + lat + "");
                            longitudeTextView.setText( "Longitude: " + lon + "");
                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                          Throwable error) {
                        try {
                            String response = new String(responseBody, "UTF-8");
                            TextView weatherData = findViewById(R.id.temperature);
                            weatherData.setText(response);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.app_logo:
                drawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.expand_weather_card:
                TransitionManager.beginDelayedTransition(weatherCard);
                expandWeatherCard.setVisibility(View.GONE);
                collapseWeatherCard.setVisibility(View.VISIBLE);
                windDetails.setVisibility(View.VISIBLE);
                cloudinessTextView.setVisibility(View.VISIBLE);
                latitudeTextView.setVisibility(View.VISIBLE);
                longitudeTextView.setVisibility(View.VISIBLE);
                break;
            case R.id.collapse_weather_card:
                TransitionManager.beginDelayedTransition(weatherCard);
                longitudeTextView.setVisibility(View.GONE);
                latitudeTextView.setVisibility(View.GONE);
                cloudinessTextView.setVisibility(View.GONE);
                windDetails.setVisibility(View.GONE);
                collapseWeatherCard.setVisibility(View.GONE);
                expandWeatherCard.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isMetricCelsius = !isChecked;
        getWeatherFromAPI();
        drawerLayout.closeDrawer(Gravity.START);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        locationAdapter.filter(newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void onClick(View view, Location location) {
        id = location.getId();
        Log.d("Clicked", id);
        getWeatherFromAPI();
        locationSearch.setQuery("", true);
        locationSearch.clearFocus();
    }
}
