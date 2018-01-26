package com.line2linecoatings.api.tracking.services;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.models.Station;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class StationService {
    public static final Log log = LogFactory.getLog(StationService.class);

    TrackingDAOImpl dao;

    public StationService() {
        dao = new TrackingDAOImpl();
    }

    public List<Station> getAllStations() throws Exception {
        log.info("Start of getAllStations in service");
        List<Station> stations;
        stations = dao.getAllStations();
        log.info("End of getAllStations in service");
        return stations;

    }
}
