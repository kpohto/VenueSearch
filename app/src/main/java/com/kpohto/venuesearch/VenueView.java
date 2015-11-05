package com.kpohto.venuesearch;

import java.util.ArrayList;

/**
 * View interface called by the presenter
 */
public interface VenueView {
    void updateQueryText(String text);
    void updateLocationStatus(String text);
    void updateVenues(ArrayList<String> venues);
}
