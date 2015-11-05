package com.kpohto.venuesearch;

import android.content.Context;
import android.location.LocationProvider;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Model containing all the application data that is stored and restored
 * during application pause and resume
 */
public class VenueModel implements Serializable {

    public LocationData locationData;
    public int locationStatus; // See LocationProvider.AVAILABLE etc.
    public boolean gpsAvailable;
    public String queryText;
    public JSONObject jsonVenues;

    public VenueModel() {
        locationData = null;
        locationStatus = LocationProvider.AVAILABLE;
        gpsAvailable = true;
        queryText = "";
        jsonVenues = null;
    }

    protected void writeObjects(ObjectOutputStream os) throws IOException {
        os.writeObject(locationData);
        os.writeObject(locationStatus);
        os.writeObject(queryText);
        os.writeObject(gpsAvailable);
        if (jsonVenues != null) {
            os.writeObject(jsonVenues.toString());
        } else {
            os.writeObject(null);
        }
    }

    /**
     * Saves the model data to file
     * @param context Application context
     */
    public void save(Context context) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput("appState", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            writeObjects(os);
            os.close();
            fos.close();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void readObjects(ObjectInputStream in) throws
            IOException, JSONException, ClassNotFoundException {
        locationData = (LocationData) in.readObject();
        locationStatus = (int) in.readObject();
        queryText = (String) in.readObject();
        gpsAvailable = (boolean) in.readObject();
        String jsonString = (String)in.readObject();
        if (jsonString != null) {
            jsonVenues = new JSONObject(jsonString);
        }
    }

    /**
     * Loads the model data from file
     * @param context Application context
     */
    public void load(Context context) {
        try {
            FileInputStream fis = context.openFileInput("appState");
            ObjectInputStream in = new ObjectInputStream(fis);
            readObjects(in);
            in.close();
            fis.close();
        } catch(FileNotFoundException ex) {
            // OK
        } catch (ClassNotFoundException | JSONException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
