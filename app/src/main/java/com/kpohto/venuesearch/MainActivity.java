package com.kpohto.venuesearch;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Activity acting as the view and receiving user input
 */
public class MainActivity extends AppCompatActivity implements VenueView {

    private static Context context;
    private VenuePresenter presenter;
    private QueryChangeListener queryChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup secrets
        QueryBuilder.client_id = getString(R.string.foursquare_client_id);
        QueryBuilder.client_secret = getString(R.string.foursquare_client_secret);

        context = getApplicationContext();
        presenter = new VenuePresenter(context, this);
        enableTextChangedListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("myapp", "onPause");
        presenter.saveModel();
        removeLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("myapp", "onResume");
        presenter.loadModel();
        requestLocationUpdates();
    }

    /**
     * Enables listening of query changes, used for catching user input
     */
    private void enableTextChangedListener() {
        queryChangeListener = new QueryChangeListener(presenter);
        EditText e = (EditText)findViewById(R.id.edit_query);
        e.addTextChangedListener(queryChangeListener);
    }

    /**
     * Disables listening of query changes, used when query is changed programmatically
     * (Otherwise query change could be interpreted as user input)
     */
    private void disableTextChangedListener() {
        EditText e = (EditText)findViewById(R.id.edit_query);
        e.removeTextChangedListener(queryChangeListener);
    }

    /**
     * Called by presenter to update query text on UI
     * @param text New replacing query text
     */
    @Override
    public void updateQueryText(String text) {
        if (text != null) {
            EditText e = (EditText) findViewById(R.id.edit_query);
            Log.d("myapp", "setText " + text);

            // Prevent requests
            disableTextChangedListener();

            // Move cursor to end of text with append
            e.setText("");
            e.append(text);

            // Allow requests again
            enableTextChangedListener();
        }
    }

    /**
     * Called by presenter to update location status on UI
     * @param text Location status as human readable text
     */
    @Override
    public void updateLocationStatus(String text) {
        TextView t = (TextView)findViewById(R.id.textStatus);
        t.setText(text);
        checkNetworkStatus();
    }

    /**
     * Inform user about missing network connection
     */
    private void checkNetworkStatus() {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();
        TextView t = (TextView)findViewById(R.id.textStatus);
        if (!isConnected) {
            t.setText(getString(R.string.status_no_network));
        }
    }

    /**
     * Called by presenter to update list of venues on UI
     * @param venues List of venues
     */
    @Override
    public void updateVenues(ArrayList<String> venues) {
        ListView venueList = (ListView) findViewById(android.R.id.list);
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>
                        (this,android.R.layout.simple_list_item_1, venues);
        venueList.setAdapter(arrayAdapter);
    }

    /**
     * Requests location updates from location provider
     * The location updates are received by the presenter
     */
    private void requestLocationUpdates() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        presenter.setLastKnownLocation(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        presenter.setProviderStatus(enabled);
        if (enabled) {
            lm.removeUpdates(presenter);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, presenter);
        }
    }

    /**
     * Cancels request for location updates for the presenter
     */
    private void removeLocationUpdates() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(presenter);
    }
}
