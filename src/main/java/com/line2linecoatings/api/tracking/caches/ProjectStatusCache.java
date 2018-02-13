package com.line2linecoatings.api.tracking.caches;

import com.line2linecoatings.api.dao.TrackingDAOImpl;

/**
 * Created by eriksuman on 2/11/18.
 */
public class ProjectStatusCache extends Cache {
    public void initialize() throws Exception {
        cache = (new TrackingDAOImpl()).getAllProjectStatuses();
        isInitialized = true;
    }
}
