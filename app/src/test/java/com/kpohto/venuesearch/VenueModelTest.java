package com.kpohto.venuesearch;

import android.location.LocationProvider;
import junit.framework.TestCase;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class VenueModelTest extends TestCase {

    private class TestVenueModel extends VenueModel  {
        public void testWriteObjects(ObjectOutputStream os) throws IOException {
            writeObjects(os);
        }
        public void testReadObjects(ObjectInputStream in) throws
                IOException, JSONException, ClassNotFoundException {
            readObjects(in);
        }
    }

    private TestVenueModel model;
    private TestVenueModel model2;

    public void setUp() throws Exception {
        super.setUp();
        model = new TestVenueModel();
        model2 = new TestVenueModel();

        // JSON is not functional in unit tests
        assertTrue(new JSONObject("{\"object\": \"first\"}").toString() == null);
    }

    public void tearDown() throws Exception {
        model = null;
        model2 = null;
    }

    public void testSave() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(baos);
        model.testWriteObjects(os);
        os.close();
        baos.close();
    }

    public void testLoad() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(baos);

        model.locationData = new LocationData(1.0, 2.0, 3);
        model.locationStatus = LocationProvider.TEMPORARILY_UNAVAILABLE;
        model.gpsAvailable = false;
        model.queryText = "one";
        model.jsonVenues = new JSONObject("{\"object\": \"first\"}");
        assertNotNull(model.jsonVenues);
        model.testWriteObjects(os);
        os.flush();
        os.close();

        ByteArrayInputStream bain = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bain);
        model2.locationData = null;
        model2.locationStatus = LocationProvider.OUT_OF_SERVICE;
        model2.gpsAvailable = true;
        model2.queryText = "two";
        model2.jsonVenues = null;
        model2.testReadObjects(in);

        assertEquals(model2.locationData.latitude, new Double(1.0));
        assertEquals(model2.locationData.longitude, new Double(2.0));
        assertEquals(model2.locationData.timeMillis, 3);
        assertEquals(model2.locationStatus, LocationProvider.TEMPORARILY_UNAVAILABLE);
        assertEquals(model2.gpsAvailable, false);
        assertEquals(model2.queryText, "one");
        assertEquals(model2.jsonVenues, null);

        in.close();
        bain.close();
        baos.close();
    }
}