package com.kpohto.venuesearch;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Helper class to construct valid and escaped queries
 */
public class QueryBuilder {

    private static final String URLEncoding = "UTF-8";
    private static final String urlHead = "https://api.foursquare.com/v2/venues/search";
    private static final String clientHead = "?client_id=";
    private static final String secretHead = "&client_secret=";
    private static final String version = "&v=20130815";
    private static final String llHead = "&ll=";
    private static final String queryPart = "&query=";
    public static String client_id; // Set from application context
    public static String client_secret; // Set from application context

    /**
     * Constructs a valid (escaped) query from given parameters
     * @param query Query text to be used
     * @return URL as a string
     */
    public static String createURL(String query, LocationData locationData) {
        if (query != null && client_id != null && client_secret != null) try {
            final String encodedQuery = URLEncoder.encode(query, URLEncoding);
            final String ll = llHead +
                    locationData.latitude + "," +
                    locationData.longitude;
            return urlHead +
                    clientHead + client_id +
                    secretHead + client_secret +
                    version +
                    ll + queryPart + encodedQuery;
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
