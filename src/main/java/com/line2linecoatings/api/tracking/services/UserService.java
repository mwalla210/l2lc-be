package com.line2linecoatings.api.tracking.services;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.models.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
}
