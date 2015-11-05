package com.kpohto.venuesearch;

import junit.framework.TestCase;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static org.junit.Assert.*;

public class QueryBuilderTest extends TestCase {

    @Test
    public void testCreateURL() throws Exception {
        QueryBuilder.client_id = "";
        QueryBuilder.client_secret = "";
        LocationData locationData = new LocationData(0.1, 0.2, 33);

        String query = "simple_query";
        String escapedQuery = URLEncoder.encode(query, "UTF-8");
        assertTrue(query.equals(escapedQuery));
        String urlString = QueryBuilder.createURL(query, locationData);
        assertTrue(urlString.contains(escapedQuery));
        URL url = new URL(urlString); //validate url

        query = "contains\nspecial character?&$.,-{}[]()";
        escapedQuery = URLEncoder.encode(query, "UTF-8");
        assertTrue(!query.equals(escapedQuery));
        urlString = QueryBuilder.createURL(query, locationData);
        assertTrue(urlString.contains(escapedQuery));
        url = new URL(urlString); //validate url
    }
}