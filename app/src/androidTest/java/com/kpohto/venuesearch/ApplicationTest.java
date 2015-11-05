package com.kpohto.venuesearch;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.test.ApplicationTestCase;

import java.util.ArrayList;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    class TestView implements VenueView {
        public boolean testLocationUpdated = false;
        public boolean testVenuesUpdated = false;

        @Override
        public void updateQueryText(String text) {

        }

        @Override
        public void updateLocationStatus(String text) {
            testLocationUpdated = true;
        }

        @Override
        public void updateVenues(ArrayList<String> venues) {
            testVenuesUpdated = true;
        }
    }

    public ApplicationTest() {
        super(Application.class);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testPresenter_onLocationChanged() throws Exception {
        TestView view = new TestView();
        VenuePresenter pres = new VenuePresenter(getContext(), view);
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(11.111);
        location.setLongitude(22.222);

        assertTrue(!view.testLocationUpdated);
        pres.onLocationChanged(location);
        assertTrue(view.testLocationUpdated);
    }

    public void testPresenter_onStatusChanged() throws Exception {
        TestView view = new TestView();
        VenuePresenter pres = new VenuePresenter(getContext(), view);

        assertTrue(!view.testLocationUpdated);
        pres.onStatusChanged(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null);
        assertTrue(view.testLocationUpdated);
    }

    public void testPresenter_onProviderEnabledDisabled() throws Exception {
        TestView view = new TestView();
        VenuePresenter pres = new VenuePresenter(getContext(), view);

        assertTrue(!view.testLocationUpdated);
        pres.onProviderDisabled(LocationManager.GPS_PROVIDER);
        assertTrue(view.testLocationUpdated);

        view.testLocationUpdated = false;
        pres.onProviderEnabled(LocationManager.GPS_PROVIDER);
        assertTrue(view.testLocationUpdated);
    }

    public void testPresenter_getVenues() throws Exception {
        class TestPresenter extends VenuePresenter {
            public TestPresenter(Context ctx, VenueView v) {
                super(ctx, v);
            }
            public void addVenues(String s) {
                setVenues(s);
            }
            public ArrayList<String> testGetVenues() {return getVenues();}
        }

        TestView view = new TestView();
        TestPresenter pres = new TestPresenter(getContext(), view);
        ArrayList<String> venues = pres.testGetVenues();
        assertTrue(venues.size() == 0);

        final String testResponse = "{" +
                "\"meta\":{\"code\":200,\"requestId\":\"\"}," +
                "\"response\":{\"venues\":[" +
                "    {\"name\":\"name_1\"," +
                "    \"location\":{\"address\":\"address_1\",\"distance\":\"12345\"}}," +
                "    {\"name\":\"name_2\"," +
                "    \"location\":{\"address\":\"address_2\",\"distance\":\"54321\"}}," +
                "    {\"name\":\"name_3\"," +
                "    \"location\":{\"distance\":\"1\"}}" +
                "    ]}" +
                "}";
        assertTrue(!view.testVenuesUpdated);
        pres.addVenues(testResponse);
        assertTrue(view.testVenuesUpdated);
        venues = pres.testGetVenues();
        assertTrue(venues.size() == 3);

        view.testVenuesUpdated = false;
        pres.addVenues(null);
        assertTrue(!view.testVenuesUpdated);

        view.testVenuesUpdated = false;
        pres.addVenues("");
        assertTrue(!view.testVenuesUpdated);
    }
}