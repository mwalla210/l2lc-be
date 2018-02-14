package com.line2linecoatings.api.tracking.services;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.models.TimeEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.List;

public class TimeEntryService {
    public static final Log log = LogFactory.getLog(ProjectService.class);
    public static TrackingDAOImpl dao;

    public TimeEntryService() {
        dao = new TrackingDAOImpl();
    }

    public TimeEntry createTimeEntry(int projectId, int employeeId, String station) throws Exception {
        log.info("Start of createTimeEntry with id " + projectId);
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setProjectId(projectId);
        timeEntry.setEmployeeId(employeeId);
        timeEntry.setStation(station);
        timeEntry.setCreated(new Date());
        timeEntry = dao.createTimeEntry(timeEntry);
        log.info("End of createTimeEntry with id " + projectId);
        return timeEntry;
    }

    public List<TimeEntry> getTimeEntries(int projectId) throws Exception{
        log.info("Start of getTimeEntries with id " + projectId);
        List<TimeEntry> timeEntries = null;
        if (dao.getProject(projectId) != null) {
            timeEntries = dao.getTimeEntries(projectId);
        }
        log.info("End of getTimeEntries with id " + projectId);
        return timeEntries;
    }
}
