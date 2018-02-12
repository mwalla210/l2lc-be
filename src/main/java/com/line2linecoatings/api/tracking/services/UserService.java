package com.line2linecoatings.api.tracking.services;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.caches.Cache;
import com.line2linecoatings.api.tracking.models.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class UserService {
    public static final Log log = LogFactory.getLog(UserService.class);
    public static TrackingDAOImpl dao;

    public UserService() {
        dao = new TrackingDAOImpl();
    }

    public User createUser(User user) throws Exception{
        log.info("Start of createUser() in service");
        User createdUser = null;

        if (user.isAdmin() == null) {
            user.setAdmin(false);
        }
        createdUser = dao.createUser(user);
        log.info("End of createUser() in service");
        return createdUser;
    }

    public User getUser(int id) throws Exception {
        log.info("Start of getUser() in service with id " + id);
        User user = null;
        user = dao.getUser(id);
        log.info("End of getUser() in service with id " + id);
        return user;
    }

    public User login(User user) throws Exception {
        log.info("Start of login() in service");
        User loginUser = null;
        loginUser = dao.login(user);
        log.info("End of login() in service");
        return loginUser;
    }

    public List<String> getAllStations() throws Exception {
        log.info("Start of getAllStations in service");
        List<String> stations;
        stations = Cache.stationCache.getAllNames();
        log.info("End of getAllStations in service");
        return stations;
    }
}
