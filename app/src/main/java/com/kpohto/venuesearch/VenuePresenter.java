package com.kpohto.venuesearch;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Presenter that gets events and updates the model and view
 * Presenter acts on user input and GPS location updates
 */
public class VenuePresenter implements LocationListener {

    private Context context;
    private Resources res;
    private VenueView view;
    private VenueModel model;

    /**
     * Constructor
     * @param ctx Application context to obtain text resources
     * @param v View
     */
    public VenuePresenter(Context ctx, VenueView v) {
        context = ctx;
        res = context.getResources();
        view = v;
        model = new VenueModel();
    }

    /**
     * @return Array of strings representing the venues
     */
    protected ArrayList<String> getVenues() {
        ArrayList<String> result = new ArrayList<>();

        if (model.jsonVenues != null) try {
            JSONObject jResp = model.jsonVenues.getJSONObject("response");
            JSONArray jArr = jResp.getJSONArray("venues");

            for (int i = 0; i < jArr.length(); i++) {
                JSONObject jVenue = jArr.getJSONObject(i);
                String name = jVenue.getString("name");
                JSONObject jLoc = jVenue.getJSONObject("location");
                String address = res.getString(R.string.no_address_available);
                try {
                    address = jLoc.getString("address");
                } catch (JSONException ex) {
                    // No address object
                }
                String distance = jLoc.getString("distance");
                String venueStr = name + "\n" +
                        res.getString(R.string.listview_address) +
                        address + "\n" +
                        res.getString(R.string.listview_distance) +
                        distance;
                result.add(venueStr);
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * @return Returns location status as human readable text
     */
    private String getLocationString() {
        String statusString = "";

        if (model.locationData != null) {
            long now = System.currentTimeMillis();
            long timedif = (now - model.locationData.timeMillis) / (60 * 1000);
            statusString = model.locationData.toString() + " (" + timedif + " " +
                    res.getString(R.string.status_minutes_ago) + ")";
        }

        if (!model.gpsAvailable) {
            if (statusString.length() != 0) {
                statusString += " ";
            }
            statusString += res.getString(R.string.status_gps_disabled);
        }
        else if (model.locationStatus == LocationProvider.OUT_OF_SERVICE) {
            if (statusString.length() != 0) {
                statusString += " ";
            }
            statusString += res.getString(R.string.status_location_unavailable);
        }
        if (statusString.length() == 0) {
            statusString = res.getString(R.string.status_waiting_for_location);
        }
        return statusString;
    }

    /**
     * Called by UI to signal user input (query text change)
     * @param query New query text
     */
    public void queryChanged(String query) {
        view.updateLocationStatus(getLocationString());
        if (model.queryText.equals(query)) {
            return;
        }
        model.queryText = query;
        if (model.locationData != null) {
            new AsyncVenueRequest().execute(query);
        }
    }

    /**
     * From LocationListener
     */
    @Override
    public void onLocationChanged(Location location) {
        model.locationData = new LocationData(
                location.getLatitude(),
                location.getLongitude(),
                System.currentTimeMillis()
        );
        view.updateLocationStatus(getLocationString());
    }

    /**
     * From LocationListener
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        model.locationStatus = status;
        view.updateLocationStatus(getLocationString());
    }

    /**
     * From LocationListener
     */
    @Override
    public void onProviderEnabled(String provider) {
        setProviderStatus(true);
        view.updateLocationStatus(getLocationString());
    }

    /**
     * From LocationListener
     */
    @Override
    public void onProviderDisabled(String provider) {
        setProviderStatus(false);
        view.updateLocationStatus(getLocationString());
    }

    /**
     * Called by view to store the current application data (model)
     */
    public void saveModel() {
        model.save(context);
    }

    /**
     * Called by view to restore the application data (model)
     */
    public void loadModel() {
        model.load(context);
        view.updateQueryText(model.queryText);
        view.updateLocationStatus(getLocationString());
        view.updateVenues(getVenues());
    }

    /**
     * Called by view to set the initial status of location provider
     * Also called to update status when provider is enabled / disabled
     * @param enabled Status (true is enabled, false is disabled)
     */
    public void setProviderStatus(boolean enabled) {
        model.gpsAvailable = enabled;
    }

    /**
     * Called by view.
     * Sets the last known location to model, is used as
     * location until more recent GPS Location is received.
     * @param location Location to be stored as current
     */
    public void setLastKnownLocation(Location location) {
        if (location != null) {
            model.locationData = new LocationData(
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getTime()
            );
        }
        view.updateLocationStatus(getLocationString());
    }

    /**
     * This class is used to create asynchronous requests to the venue provider
     */
    private class AsyncVenueRequest extends AsyncTask<String, Void, String> {

        /**
         * From AsyncTask
         */
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(QueryBuilder.createURL(params[0], model.locationData));
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append('\n');
                }
                return stringBuilder.toString();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        /**
         * From AsyncTask
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                setVenues(s);
            }
        }
    }

    /**
     * Sets new venues to model if parsing is successful
     * @param s JSON object as string
     */
    protected void setVenues(String s) {
        if (s != null) try {
            JSONObject json = new JSONObject(s);
            JSONObject meta = json.getJSONObject("meta");
            if (meta != null) {
                int code = meta.getInt("code");
                if (code == 200) {
                    model.jsonVenues = json;
                    view.updateVenues(getVenues());
                }
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}
