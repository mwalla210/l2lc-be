package com.line2linecoatings.api.tracking.caches;

import com.line2linecoatings.api.dao.TrackingDAOImpl;

/**
 * Created by eriksuman on 2/9/18.
 */
public class StationCache extends Cache{
    public void initialize() throws Exception {
        cache = (new TrackingDAOImpl()).getAllStations();
        isInitialized = true;
    }
}
