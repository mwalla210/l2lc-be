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

    public Station createStation(Station station) throws Exception{
        log.info("Start of createStation in service");
        Station newStation = null;
        newStation = dao.createStation(station);
        log.info("End of createStation in service");
        return newStation;
    }

    public boolean removeStation(int id) throws Exception{
        log.info("Start of removeStation in service");
        boolean removed;
        removed = dao.removeStation(id);
        log.info("End of removeStation in service");
        return removed;

    }

    public List<Station> getAllStations() throws Exception {
        log.info("Start of getAllStations in service");
        List<Station> stations;
        stations = dao.getAllStations();
        log.info("End of getAllStations in service");
        return stations;

    }
}
