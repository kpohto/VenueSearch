package com.kpohto.venuesearch;

import junit.framework.TestCase;
import org.junit.Test;

public class LocationDataTest extends TestCase {

    @Test
    public void testToString() throws Exception {
        LocationData data = new LocationData(0, 0, 0);
        assertNotNull(data.toString());

        long t = System.currentTimeMillis();
        data = new LocationData(11.1, 22.2, t);
        assertEquals(data.latitude, 11.1);
        assertEquals(data.longitude, 22.2);
        assertEquals(data.timeMillis, t);
    }
}