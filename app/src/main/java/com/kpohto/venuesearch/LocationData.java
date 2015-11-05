package com.kpohto.venuesearch;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Serializable container of relevant location data that can be stored and restored
 * when application is paused and resumed
 */
public class LocationData implements Serializable {

    public double latitude;
    public double longitude;
    public long timeMillis;

    public LocationData(double lat, double lon, long millis) {
        latitude = lat;
        longitude = lon;
        timeMillis = millis;
    }

    /**
     * This method is used to create user readable representation of LocationData
     * to be used in UI.
     *
     * @return  String representing the stored location and time
     */
    public String toString() {
        GregorianCalendar t = new GregorianCalendar();
        t.setTimeInMillis(timeMillis);
        DateFormat format = SimpleDateFormat.getTimeInstance();
        String time = format.format(new Date(timeMillis));
        return "Location: "+latitude+
                ", "+longitude+
                ". Time: "+time;
    }
}
